package alice.tuprologx.eclipse.views;

import java.io.ByteArrayInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import alice.tuprolog.event.ReadEvent;
import alice.tuprolog.event.ReadListener;
import alice.tuprolog.lib.UserContextInputStream;

/***
 * This class has been created and inserted to ConsoleView
 * to conform the management of input of plugin as
 * that of JavaIDE 
 */

public class InputViewer extends Composite {
	
	private UserContextInputStream stream;
	private Text input = new Text(this, SWT.MULTI | SWT.BORDER |  SWT.SCROLL_LINE);

	InputViewer(SashForm sashIn, UserContextInputStream str) {
		super(sashIn, SWT.NONE);
		init();
		stream = str;
		stream.setReadListener(new ReadListener(){
			@Override
			public void readCalled(ReadEvent event) {
				input.setFocus();
				input.setEnabled(true);
			}
		});
	}
	
	public void init()
	{
		GridLayout tabLayout = new GridLayout();
		tabLayout.numColumns = 3;
		this.setLayout(tabLayout);
		
		input.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		input.setEnabled(false);
		input.addKeyListener(new org.eclipse.swt.events.KeyListener() {
			@Override
			public void keyPressed(org.eclipse.swt.events.KeyEvent arg0) {
				if(arg0.keyCode == 13)
				{ 
					stream.putInput(new ByteArrayInputStream(input.getText().toString().getBytes()));
					input.setEnabled(false);
					input.setText("");
				}
			}
			
			@Override
			public void keyReleased(org.eclipse.swt.events.KeyEvent arg0) {}			
		});		
	}
	
	public Text getInput() {
		return input;
	}
}