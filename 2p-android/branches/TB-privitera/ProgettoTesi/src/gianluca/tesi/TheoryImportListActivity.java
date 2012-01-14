package gianluca.tesi;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TheoryImportListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				this.FileInDirectory()));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, should return back to the calling activity with
				// some info to import the theory selected,
				// TODO What to put into the bundle?
				Bundle bundle = new Bundle();
				String st = (String) parent.getItemAtPosition(position);
				bundle.putString("nomeFile", st);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});
	}

	public class FileFilterAll implements java.io.FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name == null)
				return false;
			if (name.startsWith("//"))
				return false;

			if (name.endsWith(".pl") || name.endsWith(".txt")
					|| name.endsWith(".doc"))
				return true;
			return false;
		}

	}

	public String[] FileInDirectory() {

		File d = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

		String a[] = d.list(new FileFilterAll()); // creo un array di stringhe e
													// lo riempio con la lista
													// dei files presenti nella
													// directory

		return a;
	}

}
