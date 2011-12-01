package andrea.tesi;



import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.*;
import java.io.File;
import android.content.Context;


public class tuPrologActivity extends Activity {
    /** Called when the activity is first created. */
	
	private TextView textView;
	private AutoCompleteTextView editText;
	private Button execute;
	private Button next;
	private Spinner spinner;
	private TabHost tabHost;
	private TextView solutionView;
	private TextView outputView;
	private Toast toast;
	
	private static tuPrologActivity context;
    private CUIConsole CUIConsole;
     
     public tuPrologActivity()
     {
    	context = this; 
     }
     
     public static Context getContext()
     {
    	 return context;
     }
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
         textView = (TextView) this.findViewById(R.id.textView);
         editText = (AutoCompleteTextView) this.findViewById(R.id.editText); 
         execute = (Button) this.findViewById(R.id.btnExecute);
         next =(Button) this.findViewById(R.id.btnNext);
         spinner = (Spinner) this.findViewById(R.id.spinner);
         tabHost = (TabHost) this.findViewById(R.id.tabhost);
        
         solutionView = (TextView) this.findViewById(R.id.solutionView);
         outputView = (TextView) this.findViewById(R.id.outputView);
        
         toast = Toast.makeText(tuPrologActivity.this, "Insert rule", Toast.LENGTH_LONG);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, this.FileInDirectory());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("Solution").setIndicator("Solution").setContent(R.id.solutionView));
        tabHost.addTab(tabHost.newTabSpec("Output").setIndicator("Output").setContent(R.id.outputView));
        
        tabHost.getTabWidget();
       
        CUIConsole.main(textView, editText, execute, spinner, solutionView, outputView, next, toast);
         
    }
    

    public class FileFilterAll implements java.io.FilenameFilter {
        public boolean accept(File dir, String name) {
              if (name == null) return false;
              if (name.startsWith("//")) return false;
             
             if (name.endsWith(".pl") || name.endsWith(".txt") || name.endsWith(".doc")) return true;
               return false;
        }
    }
    
	public String[] FileInDirectory()
	{
		File d = new File(Environment.getExternalStorageDirectory()+"");
		//System.out.println("Verifico se la directory esiste: " + d.exists() );
		String a[] =d.list(new FileFilterAll()); //creo un array di stringhe e lo riempio con la lista dei files presenti nella directory
		
		return a;
	}
}

