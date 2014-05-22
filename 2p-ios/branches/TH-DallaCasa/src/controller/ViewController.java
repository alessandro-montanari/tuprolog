package controller;

import java.io.IOException;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.*;
import org.robovm.rt.bro.annotation.*;

import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Var;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;
import alice.tuprolog.event.SpyEvent;
import alice.tuprolog.event.SpyListener;
import alice.tuprolog.event.WarningEvent;
import alice.tuprolog.event.WarningListener;
import view.View;

@CustomClass("ViewController")
public class ViewController extends UIViewController implements WarningListener, OutputListener, SpyListener {
	
	private Prolog engine;
	private View view = null;
//	private UITextField textField = null;
	UITextView text = new UITextView(new CGRect(48, 271, 252, 277));
	private final String incipit = "tuProlog system - release " + Prolog.getVersion() + "\n2p-ios RoboVM Project";
	
    public ViewController() {
        super("ViewController", null);
    }
    
    public ViewController(View view) {
        super("ViewController", null);
        this.view = view;
    }
    
 /* *
    Costruttore a cui passare l'istanza di UITextField per poter sempre essere
    in grado di recuperare il testo presente nella casella
    public MyViewController(UITextField textField) {
        super("MyViewController", null);
        this.textField = textField;
    }
 * */

    private void clicked(UIButton button) {
//    	textField.getText() non funziona, l'istanza non e' quella della view 
//        System.out.println("Testo: " + textField.getText());
        System.out.println("Bottone cliccato");
        
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
    	System.out.println("TextField: finita la scrittura");
    	System.out.println("Goal richiesto: " + goal);
    	textField.resignFirstResponder();
    	
    	if (goal != null && goal != "") {
    		init_prolog();
	    	System.out.println(incipit);
	        String result = solveGoal(goal);
	        
	    	text.setFont(UIFont.getFont("Helvetica Neue", 16.0));
	    	text.setText(incipit + "\n" + result);
	    	view.addSubview(text);
    	}
    }
    @Callback
    @BindSelector("query:")
    private static void query(ViewController self, Selector sel, UITextField textField) {
    	self.query(textField);
    }
    
/* *DEBUG HANDLER
    private void keyboardInput(UITextField textField) {
    	System.out.println("TextField: inserito un carattere");
    	System.out.println(textField.getText());
    }
    @Callback
    @BindSelector("keyboardInput:")
      //Gestore dell'evento di cambiamento del testo del textField
    private static void keyboardInput(MyViewController self, Selector sel, UITextField textField) {
    	self.keyboardInput(textField);
    }
* */

    String solveGoal(String goal){

    	String output = "";
        try {
        	SolveInfo info = engine.solve(goal);
   
            /*Castagna 06/2011*/        	
        	//if (engine.isHalted())
        	//	System.exit(0);
            /**/
            if (!info.isSuccess()) {
            	/*Castagna 06/2011*/        		
        		if(info.isHalted())
        		{
        			System.out.println("halt.");
        			output = "halt.";
        		}
        		else {
        		/**/ 
                System.out.println("no.");
                output = "no.";
        		}
            } else
                if (!engine.hasOpenAlternatives()) {
                    String binds = info.toString();
                    if (binds.equals("")) {
                        System.out.println("yes.");
                        output = "yes.";
                    } else {
                    	output = solveInfoToString(info) + "\nyes.";
                        System.out.println(output);
                    }
                } else {
                	output = solveInfoToString(info) + " ? ";
                    System.out.print(output);
//                    become("getChoice");
                }
        } catch (MalformedGoalException ex){
            System.out.println("syntax error in goal:\n"+goal);
            output = "syntax error in goal:\n"+goal;
        }
        return output;
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
        System.out.print(e.getMsg());
    }
    public void onSpy(SpyEvent e) {
        System.out.println(e.getMsg());
    }
    public void onWarning(WarningEvent e) {
        System.out.println(e.getMsg());
    }
    
}