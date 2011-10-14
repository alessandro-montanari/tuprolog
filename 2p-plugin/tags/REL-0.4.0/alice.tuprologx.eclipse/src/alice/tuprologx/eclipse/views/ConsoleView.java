package alice.tuprologx.eclipse.views;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import alice.tuprologx.eclipse.TuProlog;
import alice.tuprologx.eclipse.core.IPrologEngine;
import alice.tuprologx.eclipse.core.PrologQuery;
import alice.tuprologx.eclipse.core.PrologQueryFactory;
import alice.tuprologx.eclipse.core.PrologQueryResult;
import alice.tuprologx.eclipse.core.PrologQueryScope;


public class ConsoleView extends ViewPart{
	
	private Tree tree;
	private SashForm sash;
	private Label queryName;
	private Text spy;
	private Text result;
	private Text output;
	private Text project;
	private Text engines;
	private Text files;
	private Button prev;
	private Button next;
	private Button execute;
	private Composite resultViewer;
	private PrologQuery query;
	private Vector<PrologQueryResult> queryResults;
	private int queryResultIndex;
	
	public ConsoleView()
	{
		super();
	}
	
	public void createPartControl(Composite parent) {
		GridData groupData = new GridData();
		groupData.grabExcessHorizontalSpace = true;
		groupData.horizontalAlignment = SWT.FILL;
		
		GridData filesData = new GridData();
		filesData.grabExcessHorizontalSpace = true;
		filesData.grabExcessVerticalSpace = true;
		filesData.horizontalAlignment = SWT.FILL;
		filesData.verticalAlignment = SWT.FILL;
		
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 1;
		
		GridLayout tabLayout = new GridLayout();
		tabLayout.numColumns = 2;
		
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 3;
		
		Composite mainFrame = new Composite(parent, SWT.NONE);
		mainFrame.setLayout(groupLayout);
		mainFrame.setLayoutData(groupData);
		queryName = new Label(mainFrame,SWT.NONE);
		queryName.setText("To display a query select it from the Query View.");
		queryName.setAlignment(SWT.CENTER);
		
		CTabFolder notebook = new CTabFolder(mainFrame, SWT.TOP | SWT.BORDER);
		notebook.setLayout(groupLayout);
		notebook.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		CTabItem queryResult = new CTabItem(notebook,SWT.NONE);
		queryResult.setImage(TuProlog.getIconFromResources("console.gif"));
		queryResult.setText("Results");
		
		CTabItem queryScope = new CTabItem(notebook, SWT.NONE);
		queryScope.setImage(TuProlog.getIconFromResources("scope.gif"));
		queryScope.setText("Scope");
		
		CTabItem debug = new CTabItem(notebook,SWT.NONE);
		debug.setImage(TuProlog.getIconFromResources("Debugger.gif"));
		debug.setText("Debug");
		
		notebook.setSelection(queryResult);
		
		//Costruzione Tab Result
		sash = new SashForm(notebook, SWT.HORIZONTAL);
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree = new Tree(sash, SWT.BORDER);
		tree.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				IPrologEngine engine = (IPrologEngine) item.getData();
				queryResults = query.getEngineSolutions(engine);
				queryResultIndex = 0;
				refreshResultViewer();
			}
		});
		queryResult.setControl(sash);
		
		resultViewer = new Composite(sash, SWT.NONE);
		resultViewer.setLayout(tabLayout);
		
		spy = new Text(notebook, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);
		spy.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		debug.setControl(spy);
		
		Label resultLabel = new Label(resultViewer,SWT.NONE);
		resultLabel.setText("Solution: ");
		
		result = new Text(resultViewer, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.SCROLL_LINE);
		result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label outputLabel = new Label(resultViewer,SWT.NONE);
		outputLabel.setText("I/O Output: ");
		
		output = new Text(resultViewer, SWT.MULTI | SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY | SWT.SCROLL_LINE);
		output.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		sash.setWeights(new int[] { 1, 3});
		Group solExplorer = new Group(resultViewer,SWT.NONE);
		solExplorer.setText("Solution explorer");
		GridData solExplorerLayout = new GridData(SWT.FILL,SWT.BOTTOM,true,false);
		solExplorerLayout.horizontalSpan = 2;
		solExplorer.setLayout(buttonLayout);
		solExplorer.setLayoutData(solExplorerLayout);
		
		prev = new Button(solExplorer,SWT.PUSH);
		prev.setText("Previous Result");
		prev.setLayoutData(new GridData(SWT.FILL,SWT.None,true,false));
		prev.setEnabled(false);
		prev.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				queryResultIndex--;
				refreshResultViewer();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		execute = new Button(solExplorer,SWT.PUSH);
		execute.setText("Execute Query");
		execute.setLayoutData(new GridData(SWT.FILL,SWT.None,true,false));
		execute.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				PrologQueryFactory.getInstance().executeQuery(query);
				queryResultIndex=0;
				tree.setSelection(tree.getItem(0));
				refreshResultViewer();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		next = new Button(solExplorer,SWT.PUSH);
		next.setText("Next Result");
		next.setLayoutData(new GridData(SWT.FILL,SWT.None,true,false));
		next.setEnabled(false);
		next.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				queryResultIndex++;
				refreshResultViewer();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		//Costruzione Tab Scope
		
		Composite scopeViewer = new Composite(notebook,SWT.NONE);
		scopeViewer.setLayout(tabLayout);
		queryScope.setControl(scopeViewer);	
		Label projectLabel = new Label(scopeViewer,SWT.NONE);
		projectLabel.setText("Project: ");
		project = new Text(scopeViewer, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		project.setLayoutData(groupData);
		Label enginesLabel = new Label(scopeViewer,SWT.NONE);
		enginesLabel.setText("Engines: ");
		engines = new Text(scopeViewer, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		engines.setLayoutData(groupData);
		Label filesLabel = new Label(scopeViewer,SWT.NONE);
		filesLabel.setText("Files: ");
		files = new Text(scopeViewer, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);
		files.setLayoutData(filesData);
	}

	@Override
	public void setFocus() {
		resultViewer.setFocus();
	}
	
	public void setQuery( PrologQuery query )
	{
		tree.removeAll();
		this.query = query;
		queryName.setText("Inspecting query: " + query.getQuery());
		project.setText("");
		engines.setText("");
		files.setText("");
		spy.setText("");
		result.setText("");
		output.setText("");
		
		if( query != null)
		{
			Vector<IPrologEngine> engines = query.getAllEngines();
			for(int i = 0; i < engines.size() ; i++)
			{
				IPrologEngine engine = engines.get(i);
				TreeItem item = new TreeItem(tree,SWT.NONE);
				item.setText(engine.getName());
				item.setData(engine);
				item.setImage(TuProlog.getIconFromResources("theory.gif"));
				tree.setSelection(item);
			}
			Vector<PrologQueryScope> scopes = query.getAllScopes();
			String projStr = "";
			String engStr = "";
			String fileStr = "";
			for(int i = 0 ; i < scopes.size() ; i++)
			{
				PrologQueryScope scope = scopes.get(i);
				projStr += scope.getProject().getName() + ", ";
				engStr += scope.getEngine().getName() + ", ";
				fileStr += scope.getEngine().getName() + ": ";
				for(int j = 0; j < scope.getFiles().size() ; j++)
				{
					fileStr += scope.getFiles().get(j) + ", "; 
				}
				fileStr += "\n";
			}
			projStr = projStr.substring(0,projStr.length()-2);
			engStr = engStr.substring(0,engStr.length()-2);
			fileStr = fileStr.substring(0,fileStr.length()-2);
			this.project.setText(projStr);
			this.engines.setText(engStr);
			this.files.setText(fileStr);
		}
	}
	
	private void refreshResultViewer()
	{
		TreeItem engineNode = tree.getSelection()[0];
		IPrologEngine engine = (IPrologEngine) engineNode.getData();
		queryResults = query.getEngineSolutions(engine);
		PrologQueryResult result = queryResults.get(queryResultIndex);
		this.output.setText(result.getOutput());
		this.result.setText(result.getResult());
		this.spy.setText(result.getSpy());
		if(queryResultIndex <= 0)
		{
			queryResultIndex = 0;
			prev.setEnabled(false);
		}
		else
			prev.setEnabled(true);
		if(queryResultIndex >= queryResults.size() - 1)
		{
			queryResultIndex = queryResults.size() - 1;
			next.setEnabled(false);
		}
		else
			next.setEnabled(true);
	}
}