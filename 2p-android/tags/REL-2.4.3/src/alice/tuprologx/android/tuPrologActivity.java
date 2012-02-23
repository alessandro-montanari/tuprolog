package alice.tuprologx.android;

import alice.tuprologx.android.R;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Theory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class tuPrologActivity extends Activity {
	/** Called when the activity is first created. */

	private TextView textView;
	private TextView textView1;
	private AutoCompleteTextView editText;
	private Button execute;
	private Button next;
	private TabHost tabHost;
	private TextView solutionView;
	private TextView outputView;
	private Toast toast;
	private static final int ACTIVITY_SELECT = 2;

	private TheoryDbAdapter mDbHelper;

	private static tuPrologActivity context;

	public tuPrologActivity() {
		context = this;
	}

	public static Context getContext() {
		return context;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_page_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.theories_list:
			Intent i = new Intent(this, TheoryListActivity.class);
			startActivityForResult(i, ACTIVITY_SELECT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mDbHelper = new TheoryDbAdapter(this);
		mDbHelper.open();

		textView = (TextView) this.findViewById(R.id.textView);
		textView1 = (TextView) this.findViewById(R.id.textView1);
		editText = (AutoCompleteTextView) this.findViewById(R.id.editText);
		execute = (Button) this.findViewById(R.id.btnExecute);
		next = (Button) this.findViewById(R.id.btnNext);
		tabHost = (TabHost) this.findViewById(R.id.tabhost);

		solutionView = (TextView) this.findViewById(R.id.solutionView);
		outputView = (TextView) this.findViewById(R.id.outputView);

		toast = Toast.makeText(tuPrologActivity.this, "Insert rule",
				Toast.LENGTH_LONG);

		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("Solution").setIndicator("Solution")
				.setContent(R.id.solutionView));
		tabHost.addTab(tabHost.newTabSpec("Output").setIndicator("Output")
				.setContent(R.id.outputView));

		tabHost.getTabWidget();

		CUIConsole.main(textView, textView1, editText, execute, solutionView,
				outputView, next, toast);

	}

	// public class FileFilterAll implements java.io.FilenameFilter {
	// @Override
	// public boolean accept(File dir, String name) {
	// if (name == null)
	// return false;
	// if (name.startsWith("//"))
	// return false;
	//
	// if (name.endsWith(".pl") || name.endsWith(".txt")
	// || name.endsWith(".doc"))
	// return true;
	// return false;
	// }
	// }
	//
	// public String[] FileInDirectory() {
	// File d = new File(Environment.getExternalStorageDirectory()+ "");
	// // System.out.println("Verifico se la directory esiste: " + d.exists());
	// String a[] = d.list(new FileFilterAll()); // creo un array di stringhe e
	// // lo riempio con la lista
	// // dei files presenti nella
	// // directory
	//
	// return a;
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) { // FIX, se premo Back nella ListView non
										// ho un crash.

			Bundle extras = intent.getExtras();

			switch (requestCode) {
			case ACTIVITY_SELECT:
				Long id = extras.getLong(TheoryDbAdapter.KEY_ROWID);
				if (id != null) {
					Cursor theoryCursor = mDbHelper.fetchTheory(id);
					startManagingCursor(theoryCursor);
					Theory oldTheory = CUIConsole.engine.getTheory();

					try {
						Theory t;
						t = new Theory(
								theoryCursor.getString(theoryCursor
										.getColumnIndexOrThrow(TheoryDbAdapter.KEY_BODY))
										+ System.getProperty("line.separator"));
						CUIConsole.engine.setTheory(t);
						textView1
								.setText("Selected Theory : "
										+ theoryCursor.getString(theoryCursor
												.getColumnIndexOrThrow(TheoryDbAdapter.KEY_TITLE)));
						Toast.makeText(
								context,
								"Theory selected: "
										+ theoryCursor.getString(theoryCursor
												.getColumnIndexOrThrow(TheoryDbAdapter.KEY_TITLE)),
								Toast.LENGTH_SHORT).show();
					} catch (InvalidTheoryException e) {
						Toast.makeText(context, "Invalid Theory!",
								Toast.LENGTH_SHORT).show();
						CUIConsole.engine.clearTheory();
						try {
							CUIConsole.engine.setTheory(oldTheory);
						} catch (InvalidTheoryException e1) {
							// Should never get here since oldTheory is a valid
							// Theory.
							e1.printStackTrace();
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}

	}
}
