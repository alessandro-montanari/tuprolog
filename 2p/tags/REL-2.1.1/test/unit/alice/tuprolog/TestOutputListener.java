package alice.tuprolog;

import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;

class TestOutputListener implements OutputListener {
	
	public String output = "";

	public void onOutput(OutputEvent e) {
		output += e.getMsg();
	}

}
