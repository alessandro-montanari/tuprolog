package gianluca.tesi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
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
import alice.util.Automaton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CUIConsole extends Automaton implements Serializable,
		WarningListener, OutputListener, SpyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Prolog engine;
	private SolveInfo info;

	private static TextView textView;
	private static TextView textView1;
	private static AutoCompleteTextView editText;
	private static Button button;
	private static Button btnext;
	private static TextView solution;
	private static TextView output;
	private ArrayList<String> arrayList = new ArrayList<String>();
	private Toast toast;

	final static String incipit = "tuProlog " + Prolog.getVersion();

	public CUIConsole(TextView tv, TextView tv1, AutoCompleteTextView et,
			Button btn, TextView sol, TextView out, Button next, Toast t) {
		engine = new Prolog();

		engine.addWarningListener(this);
		engine.addOutputListener(this);
		engine.addSpyListener(this);

		textView = tv;
		textView1 = tv1;
		editText = et;
		button = btn;
		btnext = next;
		btnext.setEnabled(false);
		solution = sol;
		output = out;
		toast = t;

		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				btnext.setEnabled(false);
				if (editText.getText().toString().equals("")) {
					toast.show();
					// solution.setText("Inserisci regola");
				} else {
					ArrayAdapter<String> aa = new ArrayAdapter<String>(
							tuPrologActivity.getContext(),
							android.R.layout.simple_dropdown_item_1line,
							arrayList);
					if (!arrayList.contains(editText.getText().toString()))
						arrayList.add(editText.getText().toString());

					editText.setAdapter(aa);
					goalRequest();

					if ((engine.hasOpenAlternatives())) {
						btnext.setEnabled(true);
					}
				}
			}

		});

		// esecuzione query
		btnext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// aggiungo query a text view

				String choice = "";
				try {
					if ((info = engine.solveNext()) != null) {
						if (!info.isSuccess()) {
							solution.setText("no.");
							become("goalRequest");
						} else {
							choice = solveInfoToString(info) + "\n";
							solution.setText(choice);
						}
					}
				} catch (NoMoreSolutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public void boot() {
		textView.setText(incipit);
		textView1.setText("Select a Theory!");
		become("goalRequest");
	}

	public void goalRequest() {

		String goal = "";
		while (goal.equals("")) {
			solution.setText("\n "); // ?-
			goal = editText.getText().toString();
		}
		solveGoal(goal);
	}

	void solveGoal(String goal) {
		output.setText("");
		try {
			info = engine.solve(goal);
			if (engine.isHalted())
				System.exit(0);
			if (!info.isSuccess()) {
				solution.setText("no.");
				become("goalRequest");
			} else if (!engine.hasOpenAlternatives()) {
				String binds = info.toString();
				if (binds.equals("")) {
					solution.setText("yes.");

				} else {

					solution.setText(solveInfoToString(info) + "\nyes.");
					String result = solveGetTerm(info);
					if (result.contains(output.getText())) {
						output.setText(result);
					} else {
						output.setText(solveGetTerm(info) + output.getText());
					}
				}
				become("goalRequest");
			} else {
				String choice = "";
				if (info != null) {
					if (!info.isSuccess()) {
						solution.setText("no.");
						become("goalRequest");
					} else {
						choice = solveInfoToString(info) + "\n";
						solution.setText(choice);
						// become("getChoice");
					}
				}
			}
		} catch (MalformedGoalException ex) {
			solution.setText("syntax error in goal:\n" + goal);
			become("goalRequest");
		}
	}

	private String solveGetTerm(SolveInfo result) {
		String s = "";
		try {

			for (@SuppressWarnings("rawtypes")
			Iterator i = result.getBindingVars().iterator(); i.hasNext();) {
				Var v = (Var) i.next();
				if (v != null
						&& !v.isAnonymous()
						&& v.isBound()
						&& (!(v.getTerm() instanceof Var) || (!((Var) (v
								.getTerm())).getName().startsWith("_")))) {
					s += v.getTerm() + "";
				}
			}
		} catch (NoSolutionException e) {
		}
		return s;
	}

	private String solveInfoToString(SolveInfo result) {
		String s = "";
		try {
			for (@SuppressWarnings("rawtypes")
			Iterator i = result.getBindingVars().iterator(); i.hasNext();) {
				Var v = (Var) i.next();
				if (v != null
						&& !v.isAnonymous()
						&& v.isBound()
						&& (!(v.getTerm() instanceof Var) || (!((Var) (v
								.getTerm())).getName().startsWith("_")))) {
					s += v.getName() + " / " + v.getTerm();
					if (i.hasNext())
						s += "\n";
				}
			}
		} catch (NoSolutionException e) {
		}
		return s;
	}

	public void onOutput(OutputEvent e) {
		output.setText(e.getMsg());
	}

	public void onSpy(SpyEvent e) {
		output.setText(e.getMsg());
	}

	public void onWarning(WarningEvent e) {
		output.setText(e.getMsg());
	}

	public static void main(TextView tv, TextView tv1, AutoCompleteTextView et,
			Button btn, TextView sol, TextView out, Button next, Toast t) {
		// TODO Auto-generated method stub

		new Thread(new CUIConsole(tv, tv1, et, btn, sol, out, next, t)).start();

	}

}
