package application;

import org.robovm.apple.uikit.*;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.*;
import org.robovm.rt.bro.annotation.Callback;

import alice.tuprolog.*;
import alice.tuprolog.event.*;

@CustomClass("ViewController")
public class ViewController extends UIViewController implements WarningListener, OutputListener, SpyListener {
	
	@SuppressWarnings("unused")
	private ApplicationDelegate app;
	private UITextView solutionTextView;
	private UITextView warningsTextView;
	private UITextField goalTextField;
	private UITextField theoryTextField;
	private UIButton nextButton;
	private UIButton theoryButton;
	private UITextView theoryTextView;
	
	//Permette di sceglere se visualizzare una textView o un textField per inserire la teoria
	private boolean useTextField = true;
	
	private Prolog engine = null;
	private SolveInfo info = null;
	private String result = "";
	private final String incipit = "tuProlog system - release " + Prolog.getVersion() + "\n";
    
	//Constructor
    public ViewController(ApplicationDelegate app) {
        super("ViewController", null);
        this.app = app;
        init_prolog();
    }
    
    //Objective-C: bound handlers
    @Callback
    @BindSelector("setTheory:")
    private static void setTheory(ViewController self, Selector sel, UIButton button) {
    	dismissKeyboard(self, sel, button);
    	if (self.useTextField)
    		self.setTheory(self.theoryTextField.getText());
    	else
    		self.setTheory(self.theoryTextView.getText());
    }
    @Callback
    @BindSelector("theoryChanged:")
    private static void theoryChanged(ViewController self, Selector sel, UITextField textField) {
    	if (textField.getText().isEmpty())
    		self.theoryButton.setEnabled(false);
    	else
    		self.theoryButton.setEnabled(true);
    }
    @Callback
    @BindSelector("editingBegun:")
    private static void editingBegun(ViewController self, Selector sel, UITextField textField) {
		if (self.useTextField) {
			self.theoryTextView.setHidden(true);
			textField.setHidden(false);
		} else {
			self.theoryTextView.setHidden(false);
			self.theoryTextView.becomeFirstResponder();
			textField.setHidden(true);
		}
    }
    @Callback
    @BindSelector("dismissKeyboard:")
    private static void dismissKeyboard(ViewController self, Selector sel, UIView view) {
    	if (self.theoryTextField.isFirstResponder())
    		self.theoryTextField.resignFirstResponder();
    	else if (self.goalTextField.isFirstResponder())
    		self.goalTextField.resignFirstResponder();
    } 
    @Callback
    @BindSelector("solve:")
    private static void solve(ViewController self, Selector sel, UIView view) {
    	dismissKeyboard(self, sel, view);
    	self.query(self.goalTextField.getText());
    }
    @Callback
    @BindSelector("getNextSolution:")
    private static void getNextSolution(ViewController self, Selector sel, UIButton button) {
    	self.getNextSolution();
    }
    

    //Instance methods
    private void init_prolog() {
    	if (engine == null) {
	    	engine = new Prolog();
	        engine.addWarningListener(this);
	        engine.addOutputListener(this);
	        engine.addSpyListener(this);
    	}
    }
    
    private void setTheory(String theory) {
    	if (theory != null && theory != "") {
			try {
				engine.setTheory(new Theory(theory));
		    	solutionTextView.setText("Teoria aggiunta");
				warningsTextView.setText("");
			} catch (InvalidTheoryException e) {
				warningsTextView.setText("ERROR: Failed to load theory");
			}
    	} else
    		warningsTextView.setText("WARNING: Theory is empty");
    }
    private void query(String goal) {
    	if (goal != null && goal != "") {
	        solveGoal(goal);   
	        solutionTextView.setText(incipit + "\n" + result);
    	} else
    		solutionTextView.setText(incipit + "\n");
    }
    private void solveGoal(String goal){
    	result = "";
    	warningsTextView.setText("");
    	try {
        	info = engine.solve(goal);
         	
        	if (engine.isHalted())
        		System.exit(0);
            if (!info.isSuccess()) {      		
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
                	nextButton.setEnabled(true);
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
            if(s.length()>0)
                s.substring(0,s.length()-1);   
        } catch (NoSolutionException e) {}
        return s;
    }
    public void getNextSolution(){
    	if (info.hasOpenAlternatives()) {
    		try {
		        info = engine.solveNext();
		        if (!info.isSuccess())
		            result += "no.\n";
		        else
		        	result += solveInfoToString(info) + " ? ";
		    } catch (NoMoreSolutionException ex) {
		        result += "no.";
		    }
    	} 
    	solutionTextView.setText(incipit + "\n" + result);
    }
    
    //Warning handlers
    public void onOutput(OutputEvent e) {
    	warningsTextView.setText(e.getMsg());
    }
    public void onSpy(SpyEvent e) {
    	warningsTextView.setText(e.getMsg());
    }
    public void onWarning(WarningEvent e) {
    	warningsTextView.setText(e.getMsg());
    }
    
    
    
    // View elements getters and setters
    @Property
    public UITextField getGoalTextField() {
    	return goalTextField;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setGoalTextField(UITextField textField) {
    	this.goalTextField = textField;
    }
    @Property
    public UITextView getSolutionTextView() {
    	return solutionTextView;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setSolutionTextView(UITextView textView) {
    	this.solutionTextView = textView;
    }
    @Property
    public UITextView getWarningsTextView() {
    	return warningsTextView;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setWarningsTextView(UITextView textView) {
    	this.warningsTextView = textView;
    }
    @Property
    public UIButton getNextButton() {
    	return nextButton;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setNextButton(UIButton button) {
    	this.nextButton = button;
    }
    @Property
    public UIButton getTheoryButton() {
    	return theoryButton;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setTheoryButton(UIButton button) {
    	this.theoryButton = button;
    }
    @Property
    public UITextField getTheoryTextField() {
    	return theoryTextField;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setTheoryTextField(UITextField textField) {
    	this.theoryTextField = textField;
    }
    @Property
    public UITextView getTheoryTextView() {
    	return theoryTextView;
    }
    @Property
    @TypeEncoding("v@:@")
    public void setTheoryTextView(UITextView textView) {
    	this.theoryTextView = textView;
    }
}