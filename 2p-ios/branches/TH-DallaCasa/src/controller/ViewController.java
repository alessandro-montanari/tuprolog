package controller;

import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.*;
import org.robovm.rt.bro.annotation.*;

import alice.tuprolog.*;
import alice.tuprolog.event.*;
import view.View;

@CustomClass("ViewController")
public class ViewController extends UIViewController implements WarningListener, OutputListener, SpyListener {
	
	private Prolog engine;
	private View view = null;
	private String result = "";
	private final String incipit = "tuProlog system - release " + Prolog.getVersion() +
									"\n2p-ios RoboVM Project\n";
    
    public ViewController(View view) {
        super("ViewController", null);
        this.view = view;
    }

    private void clicked(UIButton button) {
//    	Handler del touchUpInside sul bottone
    }
    @Callback
    @BindSelector("clicked:")
    private static void clicked(ViewController self, Selector sel, UIButton button) {
    	self.clicked(button);
    }
    
    private void init_prolog() {
    	engine = new Prolog();
        engine.addWarningListener(this);
        engine.addOutputListener(this);
        engine.addSpyListener(this);
    }
    
    private void query(UITextField textField) {
    	String goal = textField.getText();
    	textField.resignFirstResponder();
    	
    	if (goal != null && goal != "") {
    		init_prolog();
	    	System.out.println(incipit);
	        solveGoal(goal);
	        
	        view.showResult(incipit + "\n" + result);
    	}
    }
    @Callback
    @BindSelector("query:")
    private static void query(ViewController self, Selector sel, UITextField textField) {
    	self.query(textField);
    }

    void solveGoal(String goal){

    	result = "";
        try {
        	SolveInfo info = engine.solve(goal);
   
            /*Castagna 06/2011*/        	
        	if (engine.isHalted())
        		System.exit(0);
            if (!info.isSuccess()) {
            	/*Castagna 06/2011*/        		
        		if(info.isHalted()) {
        			System.out.println("halt.");
        			result += "halt.";
        		} else {
	                System.out.println("no.");
	                result += "no.";
        		}
            } else
                if (!engine.hasOpenAlternatives()) {
                    String binds = info.toString();
                    if (binds.equals("")) {
                        System.out.println("yes.");
                        result += "yes.";
                    } else {
                    	result += solveInfoToString(info) + "\nyes.";
                        System.out.println(result);
                    }
                } else {
                	result += solveInfoToString(info) + " ? ";
                    System.out.print(result);
//                    become("getChoice");
                }
        } catch (MalformedGoalException ex){
            System.out.println("syntax error in goal:\n"+goal);
            result += "syntax error in goal:\n"+goal;
        }
    }
    
    private String solveInfoToString(SolveInfo result) {
        String s = "";
        try {
            for (Var v: result.getBindingVars()) {
                if ( !v.isAnonymous() && v.isBound() && (!(v.getTerm() instanceof Var) || (!((Var) (v.getTerm())).getName().startsWith("_")))) {
                    s += v.getName() + " / " + v.getTerm() + "\n";
                }
            }
            /*Castagna 06/2011*/
            if(s.length()>0){
            /**/
                s.substring(0,s.length()-1);    
            }
        } catch (NoSolutionException e) {}
        return s;
    }

    /*
    public void getChoice(){
        String choice="";
        try {
            while (true){
                choice = stdin.readLine();
                if (!choice.equals(";") && !choice.equals(""))
                    System.out.println("\nAction ( ';' for more choices, otherwise <return> ) ");
                else
                    break;
            }
        } catch (IOException ex){}
        if (!choice.equals(";")) {
            System.out.println("yes.");
            engine.solveEnd();
        } else {
            try {
                System.out.println();
                SolveInfo info = engine.solveNext();
                if (!info.isSuccess()){
                    System.out.println("no.");
                } else {
                	System.out.print(solveInfoToString(info) + " ? ");
                }
            }catch (Exception ex){
                System.out.println("no.");
            }
        }
    }
    */
    
    public void onOutput(OutputEvent e) {
        System.err.print(e.getMsg());
        result += e.getMsg() + "\n\n";
    }
    public void onSpy(SpyEvent e) {
        System.err.println(e.getMsg());
        result += e.getMsg() + "\n\n";
    }
    public void onWarning(WarningEvent e) {
        System.err.println(e.getMsg());
        result += e.getMsg() + "\n\n";
    }
    
}