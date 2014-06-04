package application;

import org.robovm.apple.coregraphics.*;
import org.robovm.apple.uikit.*;

public class TextViewDelegate extends UITextViewDelegateAdapter {

	private ViewController viewController = null;
	private CGRect originalFrame = null;
	private CGRect frame = null;
	private double offset = 0;
	private String theory = "";
	
	public TextViewDelegate(ViewController viewController) {
		this.viewController = viewController;
	}
	
    @Override
    public boolean shouldBeginEditing(UITextView textView) {
    	if (textView.getTextColor() == UIColor.colorLightGray()) {
    		textView.setText("");
    		textView.setTextColor(UIColor.colorBlack());
    	}
    	/**Static Layout**/
    	if (originalFrame == null) {
    		originalFrame = textView.getFrame();
//    		offset = 40.0;
    		offset = 0.0;
    		frame = new CGRect(originalFrame.origin().x(), originalFrame.origin().y(), originalFrame.getWidth(), originalFrame.getHeight()+offset);
//    		textView.setFrame(frame);
//			viewController.refreshView(offset);
    	} else {
    		/**Dinamic Layout**/
    		if (textView.getContentSize().height() < originalFrame.getHeight()*3) {
				offset = textView.getContentSize().height() - originalFrame.getHeight();
				frame = new CGRect(textView.getFrame().origin(), textView.getContentSize());
				textView.setFrame(frame);
				viewController.refreshView(offset);
			} else if (textView.getContentSize().height() > originalFrame.getHeight()*3){
				offset = frame.getHeight() - originalFrame.getHeight();
				textView.setFrame(frame);
				viewController.refreshView(offset);
			}
    	}
    	
    	return true;
    }
    
    @Override
    public void didEndEditing(UITextView textView) {
    	textView.setFrame(originalFrame);
		viewController.refreshView(originalFrame.getHeight()-frame.getHeight());
    }
    
    @Override
    public void didChange(UITextView textView) {
    	boolean choice = (!textView.getText().isEmpty()) ? true : false;
    	viewController.enableTheoryButton(choice);
    	
    	/**Dinamic Layout**/
    	if (theory.isEmpty())
    		theory = textView.getText();
    	else {
    		String newTheory = textView.getText();
	    	if (!newTheory.isEmpty()) {
	    		if (theory.length() > newTheory.length()) {
	    			if (textView.getContentSize().height() < originalFrame.getHeight()*3)
		    			if (textView.getContentSize().height() != frame.getHeight()) {
		    				offset = textView.getContentSize().height() - frame.getHeight();
							frame = new CGRect(textView.getFrame().origin(), textView.getContentSize());
							textView.setFrame(frame);
							viewController.refreshView(offset);
		    			}
	    		} else {
					char c = newTheory.charAt(newTheory.length()-1);
					if (c == '\n')
						if (frame.getHeight() < originalFrame.getHeight()*3) {
							offset = textView.getContentSize().height() - frame.getHeight();
							frame = new CGRect(textView.getFrame().origin(), textView.getContentSize());
							textView.setFrame(frame);
							viewController.refreshView(offset);
						}
				}
	    	}
	    	theory = newTheory;
    	}
    }
}
