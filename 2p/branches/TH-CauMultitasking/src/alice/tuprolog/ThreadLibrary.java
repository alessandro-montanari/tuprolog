/**
 * @author Robertino Aniello
 *
 */

package alice.tuprolog;
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import alice.tuprolog.Library;



@SuppressWarnings("serial")
public class ThreadLibrary extends Library {

	//protected Prolog engine;
	protected EngineManager engineManager;

	private Hashtable<String, PrintStream> files = new Hashtable<String, PrintStream>();
	
	
	void setEngine(Prolog en) {	
		/*try {
			file.createNewFile();
			log = new PrintStream(file);
		} catch (IOException e) {}*/
		
		/*if (!(en instanceof CProlog)){
			System.out.println("Current Prolog engine does not support multi-threading");
			throw new RuntimeException("Current Prolog engine does not support multi-threading");
		}*/
        engine = en;
        engineManager = en.getEngineManager();
        //engineManager=((CProlog) en).getEngineManager();
        
        /*if (engineManager==null){
        	System.out.println("Current Prolog engine does not support multi-threading");
			throw new RuntimeException("Current Prolog engine does not support multi-threading");
		}*/
	}
	
	//Tenta di unificare a t l'identificativo del thread corrente
	public boolean thread_id_1 (Term t) throws PrologError{
        int id = engineManager.runnerId();
        try{
        	unify(t,new Int(id));
		} catch (InvalidTermException e) {
			throw PrologError.syntax_error(engine.getEngineManager(),-1, e.line, e.pos, t);
		}
		return true;
	}
	
	
	/*Crea un nuovo thread di identificatore id e comincia ad eseguire il goal dato.
	status dˆ la possibilitˆ di specificare i diritti di accesso al thread: public, protected, private (deve essere un atomo).
	*/
	/*public boolean thread_create_3 (Term goal, Term id, Term status){
	 * int permits;
		status = status.getTerm();		
		if (!status.isAtom() || !status.isAtomic()) 
			return false;
		String s = status.toString();
		if (s.contains("private")) 
			permits=2;
		else if (s.contains("protected")) 
			permits=1;
		else if (s.contains("public")) 
			permits=0;
		else return false;
		return engineManager.threadCreate(goal, id);
	}*/
	
	/*Crea un nuovo thread di identificatore id e comincia ad eseguire il goal dato
	Diritto di accesso (di default): pretected 
	*/
	public boolean thread_create_2 (Term goal, Term id){
		return engineManager.threadCreate(goal, id);
	}
	
	/*Aspetta la terminazione del thread di identificatore id e ne raccoglie il risultato, 
	unificando il goal risolto a result. Il thread viene eliminato dal sistema*/
	public boolean thread_join_2(Term id, Term result) throws PrologError{
		id = id.getTerm();
		if (!(id instanceof Int)) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "integer", id);
		SolveInfo res = engineManager.join(((Int)id).intValue());
		if (res == null) return false;
		//log.println("Join completato con successo");
		Term status;
		try {
			status = res.getSolution();
		} catch (NoSolutionException e) {
			//status = new Struct("FALSE");		
			return false;
		}
		try{
			unify (result, status);
		} catch (InvalidTermException e) {
			throw PrologError.syntax_error(engine.getEngineManager(),-1, e.line, e.pos, result);
		}
		return true;
	}

	public boolean new_log_file_1(Term name) throws PrologError{
		name = name.getTerm();
		File file = new File(name.toString());
		PrintStream log = null;
		try {
			file.createNewFile();
			log=new PrintStream(file);
		} catch (IOException e) {
			 throw PrologError.permission_error(engine.getEngineManager(),
                     "file", "stream", name,
                     new Struct(e.getMessage()));
		}
		files.put(name.toString(), log);
		return true;
	}
	
	public boolean write_log_2(Term name, Term arg) throws PrologError{
		arg = arg.getTerm();
		name = name.getTerm();
		PrintStream log = files.get(name.toString());
		if(log == null)
			 throw PrologError.existence_error(engineManager, 2, "file", name, new Struct("File name does not exist."));
		log.println(arg.toString());
		return true;
	}
		
	public boolean thread_read_2(Term id, Term result) throws PrologError{
		id=id.getTerm();
		if (!(id instanceof Int)) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "integer", id);
		SolveInfo res=engineManager.read( ((Int)id).intValue());
		if (res==null) return false;
		Term status;
		try {
			status = res.getSolution();
		} catch (NoSolutionException e) {
			//status = new Struct("FALSE");
			return false;
		}
		/*boolean uni;
		synchronized (log){
			log.println("\nUnificazione(" +
					Thread.currentThread().getId() +
					"): result->  " +
					result +
					"; status-> " +
					status);
			uni= unify (result, status);
			log.println(" - Successo: " +
					uni +
					"  Termini dopo l'unificazione: " +
					"result-> " +
					result +
					" status-> "+
					status);
			}*/
		try{
			unify (result, status);
		} catch (InvalidTermException e) {
			throw PrologError.syntax_error(engine.getEngineManager(),-1, e.line, e.pos, result);
		}
		return true;
	}
	
	public boolean thread_has_next_1(Term id) throws PrologError{
		id=id.getTerm();
		if (!(id instanceof Int)) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "integer", id);
		return engineManager.hasNext(((Int)id).intValue());
	}
	
	
	public boolean thread_next_sol_1(Term id) throws PrologError{
		id=id.getTerm();
		if (!(id instanceof Int)) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "integer", id);
		return engineManager.nextSolution(((Int)id).intValue());
	}
	
	public boolean thread_detach_1 (Term id) throws PrologError{
		id=id.getTerm();
		if (!(id instanceof Int)) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "integer", id);
		engineManager.detach(((Int)id).intValue());
		return true;
	}
	
	public boolean thread_sleep_1(Term millisecs) throws PrologError{
		millisecs=millisecs.getTerm();
		if (!(millisecs instanceof Int)) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "integer", millisecs);
		long time=((Int)millisecs).intValue();
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			System.out.println("ERRORE SLEEP");
			return false;
		}
		return true;
	}
	
	/*public boolean share_read_permits_2(Term shared, Term reader){
		shared=shared.getTerm();
		reader=reader.getTerm();
		if (!(shared instanceof Int) || !(reader instanceof Int)) return false;
		return engineManager.add_reader(((Int)shared).intValue(), ((Int)reader).intValue());
	}
	
	public boolean share_modify_permits_2(Term shared, Term reader){
		shared=shared.getTerm();
		reader=reader.getTerm();
		if (!(shared instanceof Int) || !(reader instanceof Int)) return false;
		return engineManager.add_owner(((Int)shared).intValue(), ((Int)reader).intValue());
	}*/
	
	public boolean thread_send_msg_2(Term msg, Term id) throws PrologError{
		id=id.getTerm();
		if (id instanceof Int) 
			return engineManager.sendMsg(msg, ((Int)id).intValue());	
		if (!id.isAtomic() || !id.isAtom()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom, atomic or integer", id);
		return engineManager.sendMsg(msg, id.toString());
	}
	
	public  boolean  thread_get_msg_2(Term msg, Term id) throws PrologError{
		id=id.getTerm();
		if (id instanceof Int) 
			return engineManager.getMsg(msg, ((Int)id).intValue());
		if (!id.isAtom() || !id.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom, atomic or integer", id);
		return engineManager.getMsg(msg,id.toString());
	}	
	
	public  boolean  thread_peek_msg_2(Term msg, Term id) throws PrologError{
		id=id.getTerm();
		if (id instanceof Int) 
			return engineManager.peekMsg(msg, ((Int)id).intValue());
		if (!id.isAtom() || !id.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom, atomic or integer", id);
		return engineManager.peekMsg(msg,id.toString());
	}

	public  boolean  thread_wait_msg_2(Term msg, Term id) throws PrologError{
		id=id.getTerm();
		if (id instanceof Int) 
			return engineManager.waitMsg(msg, ((Int)id).intValue());
		if (!id.isAtom() || !id.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom, atomic or integer", id);
		return engineManager.waitMsg(msg,id.toString());
	}

	public  boolean  thread_remove_msg_2(Term msg, Term id) throws PrologError{
		id=id.getTerm();
		if (id instanceof Int) 
			return engineManager.removeMsg(msg, ((Int)id).intValue());
		if (!id.isAtom() || !id.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom, atomic or integer", id);
		return engineManager.removeMsg(msg,id.toString());
	}
	
	/*public boolean thread_get_msg_1 (Term msg){
		return engineManager.getMsg(msg);
	}*/
	
	/*public boolean thread_peek_msg_1 (Term msg){
		return engineManager.peekMsg(msg);
	}*/
	
	/*public boolean thread_wait_msg_1 (Term msg){
		return engineManager.waitMsg(msg);
	}*/
	
	/*public boolean thread_remove_msg_1 (Term msg){
		return engineManager.removeMsg(msg);
	}*/
	
	
	public boolean msg_queue_create_1(Term q) throws PrologError{
		q= q.getTerm();
		if (!q.isAtomic() || !q.isAtom()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", q);
		return engineManager.createQueue(q.toString());
	}
	
	public boolean msg_queue_destroy_1 (Term q) throws PrologError{
		q=q.getTerm();
		if (!q.isAtomic() || !q.isAtom()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", q);
		engineManager.destroyQueue(q.toString());
		return true;
	}
	
	/*public boolean msg_queue_size_1(Term n){
		Int size = new Int(engineManager.queueSize());
		return unify(n,size);
	}*/
	
	public boolean msg_queue_size_2(Term n, Term id) throws PrologError{
		id=id.getTerm();
		int size;
		if (id instanceof Int) 
			size=engineManager.queueSize(((Int)id).intValue());
		else{
			if (!id.isAtom() || !id.isAtomic())
				throw PrologError.type_error(engine.getEngineManager(), 1,
	                    "atom, atomic or integer", id);
			size=engineManager.queueSize(id.toString());
		}
		if (size<0) return false;
		return unify(n, new Int(size));
	}
	
	
	public boolean mutex_create_1(Term mutex) throws PrologError{
		mutex=mutex.getTerm();
		if (!mutex.isAtom() || !mutex.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", mutex);
		return engineManager.createLock(mutex.toString());
	}
	
	public boolean mutex_destroy_1(Term mutex) throws PrologError{
		mutex=mutex.getTerm();
		if (!mutex.isAtom() || !mutex.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", mutex);
		engineManager.destroyLock(mutex.toString());
		return true;
	}
	
	public boolean mutex_lock_1(Term mutex) throws PrologError{
		mutex=mutex.getTerm();
		if (!mutex.isAtom() || !mutex.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", mutex);
		return engineManager.mutexLock(mutex.toString());
	}
	
	public boolean mutex_trylock_1(Term mutex) throws PrologError{
		mutex=mutex.getTerm();
		if (!mutex.isAtom() || !mutex.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", mutex);
		return engineManager.mutexTryLock(mutex.toString());
	}
	
	public boolean mutex_unlock_1(Term mutex) throws PrologError{
		mutex=mutex.getTerm();
		if (!mutex.isAtom() || !mutex.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", mutex);
		return engineManager.mutexUnlock(mutex.toString());
	}
	
	public boolean mutex_isLocked_1(Term mutex) throws PrologError{
		mutex=mutex.getTerm();
		if (!mutex.isAtom() || !mutex.isAtomic()) 
			throw PrologError.type_error(engine.getEngineManager(), 1,
                    "atom or atomic", mutex);
		return engineManager.isLocked(mutex.toString());
	}
	
	public boolean mutex_unlock_all_0(){
		engineManager.unlockAll();
		return true;
	}
	
	public String getTheory(){
		return 
		"thread_execute(GOAL, ID):- thread_create(GOAL, ID), '$next'(ID). \n" +
		"'$next'(ID). \n"+
		"'$next'(ID) :- '$thread_execute2'(ID). \n"+
		"'$thread_execute2'(ID) :- not thread_has_next(ID),!,false. \n" +
		"'$thread_execute2'(ID) :- thread_next_sol(ID). \n" +
		"'$thread_execute2'(ID) :- '$thread_execute2'(ID). \n" +
	
		"with_mutex(MUTEX,GOAL):-mutex_lock(MUTEX), call(GOAL), !, mutex_unlock(MUTEX).\n" +
		"with_mutex(MUTEX,GOAL):-mutex_unlock(MUTEX), fail."		
		;
	
	}
	
	public String getTheory(int a){
		switch(a){
		case 1 :  return "genitore(bob,a).\n" +
				"genitore(bob,b).\n" +
				"genitore(bob,c).\n" +
				"genitore(bob,d)."
			;
		case 2 : return "start(X,Y) :- crea_log('FileProva'), thread_create(genitore(b,X), ID2),  thread_create(thread1(ID2), ID), thread_create(genitore(bob,Y), ID3), thread_next_sol(ID3), thread_read(ID2,X), thread_read(ID3,Y), write_log('FileProva', X).\n" +		
				"thread1(ID2):- thread_next_sol(ID2), thread_next_sol(ID2), thread_next_sol(ID2).\n"+
				"crea_log(NOME):- new_log_file(NOME).\n"+
				"genitore(bob,a).\n" +
				"genitore(b,b).\n" +
				"genitore(bob,c).\n" +
				"genitore(b,d).\n" +
				"genitore(bob,gdh).\n"+
				"genitore(b,e).\n" +
				"genitore(b,f)." 
			;
		case 3: return "start(X) :- thread_execute(X, ID), lettura(ID,X).\n" +
				"lettura(ID, X):- thread_read(ID, X).\n" +
				"genitore(bob,b).\n" +
				"genitore(bob,c).\n" +
				"genitore(bob,a).\n" +
				"genitore(bob,d)."
			;
		case 4: return "start(X) :- msg_queue_create('CODA'), thread_create(thread1(X), ID), thread_sleep(500), invio('CODA', 'messaggio molto importante'), lettura(ID,X).\n" +
				"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
				"invio(ID, M):- thread_send_msg(a(M),ID). \n" +
				"lettura(ID, X):- thread_join(ID, thread1(X)). "	
			;
		case 5: return "start(X) :- mutex_lock('mutex'), thread_create(thread1(X), ID), msg_queue_create('CODA'), invio('CODA', 'messaggio molto importante'), lettura(ID,X). \n" +
				"thread1(X) :- mutex_lock('mutex'), thread_peek_msg(a(X),'CODA'), mutex_unlock('mutex'). \n" +
				"invio(Q, M):- thread_send_msg(a(M),Q), mutex_unlock('mutex'). \n" +
				"lettura(ID, X):- thread_read(ID, thread1(X))."	
			;
		case 6: return "start(X) :- thread_create(thread1(X,ID), ID), invio(ID, 'messaggio molto importante'), lettura(ID,X). \n" +
				"thread1(X, ID) :- thread_wait_msg(a(X), ID). \n" +
				"invio(ID, M):- thread_send_msg(a(M),ID). \n" +
				"lettura(ID, X):- thread_join(ID, thread1(X,ID))." 
			;
		default : return null;
		}
	}
}
