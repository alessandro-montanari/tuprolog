package alice.tuprologx.eclipse.util;

import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;
import alice.tuprolog.event.SpyEvent;
import alice.tuprolog.event.SpyListener;

public class EventListener implements SpyListener, OutputListener {

	private String spy = "";
	private String output = "";

	@Override
	public void onSpy(SpyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSnapshot() == null) {
			spy = arg0.getMsg();
		} else {
			spy = arg0.getSnapshot().toString();
		}

	}

	@Override
	public void onOutput(OutputEvent arg0) {
		// TODO Auto-generated method stub
		output = arg0.getMsg();
	}

	public String getSpy() {
		return spy;
	}

	public String getOutput() {
		return output;
	}

	public void setSpy(String spy) {
		this.spy = spy;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
