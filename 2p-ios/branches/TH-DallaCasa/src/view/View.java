package view;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;

import controller.ViewController;

public class View extends UIApplicationDelegateAdapter {

    private UIWindow window = null;
    private ViewController viewController = null;
	private UITextView textView = null;
    
    @SuppressWarnings("rawtypes")
    @Override
    public boolean didFinishLaunching(UIApplication application,
            NSDictionary launchOptions) {

        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        viewController = new ViewController(this);
        window.setRootViewController(viewController);
        window.makeKeyAndVisible();
        
        return true;
    }
    
    public void showResult(String result) {
    	textView = new UITextView(new CGRect(40, 271, 252, 277));
    	textView.setFont(UIFont.getFont("Helvetica Neue", 16.0));
    	textView.setText(result);
    	window.addSubview(textView);
    }

    public static void main(String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, View.class);
        pool.close();
    }
}