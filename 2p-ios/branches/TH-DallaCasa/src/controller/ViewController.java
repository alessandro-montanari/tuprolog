package controller;

import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.*;
import org.robovm.rt.bro.annotation.Callback;

import alice.tuprolog.*;
import alice.tuprolog.event.*;
import view.View;

@CustomClass("ViewController")
public class ViewController extends UIViewController implements WarningListener, OutputListener, SpyListener {
	
	@SuppressWarnings("unused")
	private View view;
	private UITextField textField;
	private UITextView textView;
	
	private Prolog engine = null;
	private String result = "";
	private final String incipit = "tuProlog system - release " + Prolog.getVersion() +
									"\n2p-ios RoboVM Project\n";
    
    public ViewController(View view) {
        super("ViewController", null);
        this.view = view;
        init_prolog();
    }
    
    @Callback
    @BindSelector("dismissKeyboard:")
    private static void dismissKeyboard(ViewController self, Selector sel, UIView view) {
    	self.textField.resignFirstResponder();
    }
    
    @Callback
    @BindSelector("solve:")
    private static void solve(ViewController self, Selector sel, UIView view) {
    	self.textField.resignFirstResponder();
    	self.query(self.textField.getText());
    }
    
    private void init_prolog() {
    	if (engine == null) {
	    	engine = new Prolog();
	        engine.addWarningListener(this);
	        engine.addOutputListener(this);
	        engine.addSpyListener(this);
    	}
    }
    
    private void query(String goal) {
    	if (goal != null && goal != "") {
	        solveGoal(goal);   
	        showResult();
    	}
    }
    
    private void showResult() {
    	textView.setFont(UIFont.getFont("Helvetica Neue", 16.0));
    	textView.setText(incipit + "\n" + result);
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
        		if(info.isHalted())
        			result += "halt.";
        		else
	                result += "no.";
            } else
                if (!engine.hasOpenAlternatives()) {
                    String binds = info.toString();
                    if (binds.equals("")) {
                        result += "yes.";
                    } else
                    	result += solveInfoToString(info) + "\nyes.";
                } else {
                	result += solveInfoToString(info) + " ? ";
//                    become("getChoice");
                }
        } catch (MalformedGoalException ex){
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
            if(s.length()>0)
                s.substring(0,s.length()-1);   
        } catch (NoSolutionException e) {}
        return s;
    }

    /**
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
    **/
    
    public void onOutput(OutputEvent e) {
        result += e.getMsg() + "\n\n";
    }
    public void onSpy(SpyEvent e) {
        result += e.getMsg() + "\n\n";
    }

	public void onWarning(WarningEvent e) {
        result += e.getMsg() + "\n\n";
    }
    
    // TextField, TextView getter and setter
    @Property
    public UITextField getTextField() {
    	return textField;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setTextField(UITextField textField) {
    	this.textField = textField;
    }
    @Property
    public UITextView getTextView() {
    	return textView;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setTextView(UITextView textView) {
    	this.textView = textView;
    }
    

}