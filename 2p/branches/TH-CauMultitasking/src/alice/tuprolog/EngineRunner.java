/*
 *
 *
 */
package alice.tuprolog;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alex Benini
 *
 * Core engine
 */
@SuppressWarnings("serial")
public class EngineRunner implements java.io.Serializable, Runnable{
    
    private Prolog 			 	mediator;
    private TheoryManager    	theoryManager;
    private PrimitiveManager	primitiveManager;
    private LibraryManager   	libraryManager;
    private EngineManager 		engineManager;

    private PrintStream log;
    private int id;
    private long pid;
    private boolean detached;
    private boolean solving;
    private SolveInfo sinfo;
    private Term query;
    private TermQueue msgs;
    private ArrayList<Boolean> next;
    private int count;
    //private boolean wait;	//per vedere se qualcuno ha fatto una wait su semaphore
    private Lock lockVar;		//condizioni per la lettura
    private Condition cond;
    private Semaphore semaphore;
    
    /* Current environment */
    Engine env;
    /* Last environment used */
    private Engine last_env;
    /* Stack environments of nidicate solving */
    private LinkedList<Engine> stackEnv = new LinkedList<Engine>();
    
    /**
	 * States
	 */
    final State INIT;
    final State GOAL_EVALUATION;
    final State EXCEPTION;
    final State RULE_SELECTION;
    final State GOAL_SELECTION;
    final State BACKTRACK;
    final State END_FALSE;
    final State END_TRUE;
    final State END_TRUE_CP;
    final State END_HALT;
    
    public static final int HALT    = -1;
    public static final int FALSE   =  0;
    public static final int TRUE    =  1;
    public static final int TRUE_CP =  2;
    
    
    public EngineRunner(int id) {
        /* Istanzio gli stati */
        INIT            = new StateInit(this);
        GOAL_EVALUATION = new StateGoalEvaluation(this);
        EXCEPTION        = new StateException(this);
        RULE_SELECTION  = new StateRuleSelection(this);
        GOAL_SELECTION  = new StateGoalSelection(this);
        BACKTRACK       = new StateBacktrack(this);
        END_FALSE       = new StateEnd(this,FALSE);
        END_TRUE        = new StateEnd(this,TRUE);
        END_TRUE_CP     = new StateEnd(this,TRUE_CP);
        END_HALT        = new StateEnd(this,HALT);
		
		this.id = id;
    }
    
    
    /**
     * Config this Manager
     */
    void initialize(Prolog vm) {
        mediator = vm;
        theoryManager    = vm.getTheoryManager();
        primitiveManager = vm.getPrimitiveManager();
        libraryManager   = vm.getLibraryManager();
        engineManager = vm.getEngineManager();
        
        detached = false;
        solving = false;
        sinfo = null;
        msgs = new TermQueue();
        next = new ArrayList<Boolean>();
        count = 0;
        //wait = false;
        lockVar = new ReentrantLock();	
        cond = lockVar.newCondition();
        semaphore = new Semaphore(1,true);
    }
    
    void spy(String action, Engine env) {
        mediator.spy(action,env);
    }
    
    void warn(String message) {
        mediator.warn(message);
    }
    
    /*Castagna 06/2011*/
	void exception(String message) {
		mediator.exception(message);
	}
	/**/
	
	public void detach(){
    	detached = true;
    }
    
	public boolean isDetached(){
    	return detached;
    }
	
    /**
     *  Solves a query
     *
     * @param g the term representing the goal to be demonstrated
     * @return the result of the demonstration
     * @see SolveInfo
     **/
   private synchronized void threadSolve() {
    	try{
    		semaphore.acquire();
    		println("\nThread produttore - acquisisco il lock sul semaforo semaphore\nCerco la prima soluzione");
    		println("pid: "+Thread.currentThread().getId());
    	}
    	catch(InterruptedException e) {}
        
        sinfo = solve();
        
        solving = false;
        println("Thread produttore - ho risolto il goal");
        println("\n\n**- Soluzione computata -**\n"+sinfo+"\n\n");       
        
        lockVar.lock();
		try{
			 cond.signal();
		}
		finally{
			lockVar.unlock();
		}
            
        if (!sinfo.hasOpenAlternatives()) {
            solveEnd();
            println("Thread produttore - non ci sono pi soluzioni; rilascio il semaforo");
            semaphore.release();	//Rilascio definitivamente il lock
        }
        else if(next.isEmpty() || !next.get(count)){
        	synchronized(semaphore){     	
            	try {
            		println("Thread produttore - mi metto in attesa di eventuali altre richieste");
            		//wait = true;
            		semaphore.wait();	//Mi metto in attesa di eventuali altre richieste
            		println("Thread produttore - mi sono risvegliato.");
            		//wait = false;
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}	
            }         
        }
    }
    
    public synchronized SolveInfo solve() {
        try {
            query.resolveTerm();
            
            libraryManager.onSolveBegin(query);
            primitiveManager.identifyPredicate(query);
//            theoryManager.transBegin();
            
            freeze();
            env = new Engine(this, query);
            StateEnd result = env.run();
            defreeze();
            
            sinfo = new SolveInfo(
                    query,
                    result.getResultGoal(),
                    result.getResultDemo(),
                    result.getResultVars()
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sinfo;
    }
    
    /**
     * Gets next solution
     *
     * @return the result of the demonstration
     * @throws NoMoreSolutionException if no more solutions are present
     * @see SolveInfo
     **/
    private synchronized void threadSolveNext() throws NoMoreSolutionException {
    	next.set(count, false);
    	count++;
    	println("\nThread produttore - la soluzione precedente era: "+sinfo);
    	println("Thread produttore - cerco altre soluzioni");
	
		sinfo = solveNext();
		
		solving = false;
		println("Thread produttore - ho risolto il goal");
		println("\n\n**- Soluzione computata -**\n"+sinfo+"\n\n");
       
        
		lockVar.lock();
		try{
			 cond.signal();
		}
		finally{
			lockVar.unlock();
		}
        
        if (!sinfo.hasOpenAlternatives()){
        	solveEnd();
            println("Thread produttore - non ci sono pi soluzioni; rilascio il semaforo");
        }
        else if(count>(next.size()-1) || !next.get(count)){
        	try{
                synchronized(semaphore){
                	println("Thread produttore - mi metto in attesa di eventuali altre richieste");
                	//wait = true;
                	semaphore.wait();	//Mi metto in attesa di eventuali altre richieste
                	println("Thread produttore - mi sono risvegliato.");
                	//wait = false;
                }
            }
            catch(InterruptedException e) {}
        }
    }
    
    public synchronized SolveInfo solveNext() throws NoMoreSolutionException {
        if (hasOpenAlternatives()) {
            refreeze();
            env.nextState = BACKTRACK;
            StateEnd result = env.run();
            defreeze();
            sinfo = new SolveInfo(
                    env.query,
                    result.getResultGoal(),
                    result.getResultDemo(),
                    result.getResultVars()
            );
            
            /*if (!sinfo.hasOpenAlternatives()){
            	solveEnd();
            	
            }*/
            return sinfo;

        } else
            throw new NoMoreSolutionException();       
    }
   
    
    /**
     * Halts current solve computation
     */
    public void solveHalt() {
        env.mustStop();
    }
    
    /**
     * Accepts current solution
     */
    public void solveEnd() {
//        theoryManager.transEnd(sinfo.isSuccess());
//        theoryManager.optimize();
        libraryManager.onSolveEnd();
        engineManager.removeThread(pid);
    }
    
    
    private void freeze() {
        if(env==null) return;
        try {
            if (stackEnv.getLast()==env) return;
        } catch(NoSuchElementException e) {}
        stackEnv.addLast(env);
    }
    
    private void refreeze() {
        freeze();
        env = last_env;            
    }
    
    private void defreeze() {
        last_env = env;
        if (stackEnv.isEmpty()) return;
        env = (Engine)(stackEnv.removeLast());
    }
    
    
    /*
     * Utility functions for Finite State Machine
     */
    
    List<ClauseInfo> find(Term t) {
        return theoryManager.find(t);
    }
    
    void identify(Term t) {
        primitiveManager.identifyPredicate(t);
    }
    
//    void saveLastTheoryStatus() {
//        theoryManager.transFreeze();
//    }
    
    void pushSubGoal(SubGoalTree goals) {
        env.currentContext.goalsToEval.pushSubGoal(goals);
    }
    
    
    void cut() {
        env.choicePointSelector.cut(env.currentContext.choicePointAfterCut);
    }
    
    
    ExecutionContext getCurrentContext() {
        return (env==null)? null : env.currentContext;
    }
    
    
    /**
     * Asks for the presence of open alternatives to be explored
     * in current demostration process.
     *
     * @return true if open alternatives are present
     */
    boolean hasOpenAlternatives() {
        if (sinfo==null) return false;
        return sinfo.hasOpenAlternatives();
    }
    
    
    /**
     * Checks if the demonstration process was stopped by an halt command.
     * 
     * @return true if the demonstration was stopped
     */
    boolean isHalted() {
        if (sinfo==null) return false;
        return sinfo.isHalted();
    }


	@Override
	public void run() {
		pid = Thread.currentThread().getId();
		
		File file = new File("Runner"+pid+"Log.txt");
		try {
			file.createNewFile();
			log=new PrintStream(file);
		} catch (IOException e) {}
		
		solving = true;
		println("\nThread produttore - procedo con la computazione del risultato");
		println("pid: "+pid);
		
		if (sinfo == null) {
			threadSolve();
		}
		try {
			while(hasOpenAlternatives())
				if(next.get(count))
					threadSolveNext();
		} catch (NoMoreSolutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}    
	
	public void println(String s){
		log.println(s);
	}
	
	public int getId(){
		return id;
	}
	
	public SolveInfo getSolution(){
		return sinfo;
	}
	
	public void setGoal(Term goal){
		this.query = goal;
	}

	public boolean nextSolution() {
		solving = true;
		next.add(true);
		//System.out.println("nextsol");	
		
		//if(wait)
		synchronized(semaphore){	
			semaphore.notify();			
		}
		return true;
	}
	
	public SolveInfo read(){
		lockVar.lock();
		try{
			while(solving || sinfo==null)	
			try {
				cond.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//println("read");
		}
		finally{
			lockVar.unlock();
		}
		
		return sinfo;
	}
	
	/*public SolveInfo join(){
		lockVar.lock();
		try{
			while(solving || sinfo==null)
			try {
				cond.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			println("join");
		}
		finally{
			lockVar.unlock();
		}
		return sinfo;
	}*/
	
	public void setSolving(boolean solved){
		solving = solved;
	}
	
	
	public void sendMsg(Term t){			
		msgs.store(t);
	}
	
	
	public boolean getMsg(Term t){
		msgs.get(t, mediator, this);
		return true;
	}
	
	
	public boolean peekMsg (Term t){
		return msgs.peek(t, mediator);
	}
	
	
	public boolean removeMsg(Term t){
		return msgs.remove(t, mediator);
	}

	
	public boolean waitMsg(Term msg) {
		msgs.wait(msg, mediator, this);
		return true;
	}
	
	
	public int msgQSize(){
		return msgs.size();
	}
}