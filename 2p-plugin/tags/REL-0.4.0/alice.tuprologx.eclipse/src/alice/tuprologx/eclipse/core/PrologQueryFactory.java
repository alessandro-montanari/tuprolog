package alice.tuprologx.eclipse.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Theory;
import alice.tuprologx.eclipse.util.EventListener;
import alice.tuprologx.eclipse.views.QueryList;



public class PrologQueryFactory extends Observable {
	/* Questa factory si occupa di gestire e creare le query.
	 * in tal modo è possibile accedere a tutte le informazioni
	 * relative ad una query come, ad esempio, tutte le query associate
	 * ad un motore.
	 */
		
	private static PrologQueryFactory instance;
	Vector<PrologQuery> queries;
	private int queryId;
	
	public static PrologQueryFactory getInstance(){
		if (instance == null){
			instance = new PrologQueryFactory();
		}
		return instance;
	}
	
	public PrologQueryFactory()
	{
		queryId = 0;
		queries = new Vector<PrologQuery>();
	}
	
	/*public Vector getProjectQueries(String projectName){
		if(projectName != null){
			return (Vector) queries.get(projectName);
		}
		else{
			return null;
		}
	}*/
	
	public void addQuery(PrologQuery query)
	{
		if(!queries.contains(query))
		{
			queries.add(query);
			setChanged();
			notifyObservers();
		}
	}
	
	public Vector getQueries(){
		return queries;
	}
	
	public void removeQuery(PrologQuery query){
		queries.remove(query);
		setChanged();
		notifyObservers();
	}
	
	public boolean executeQuery(PrologQuery query)
	{
		Hashtable scopeTable = new Hashtable();
		String queryString = query.getQuery();
		Vector<PrologQueryScope> scopes = query.getAllScopes();
		query.resetResults();
		for(int i = 0; i < scopes.size() ; i++)
		{
			PrologQueryScope scope = scopes.get(i);
			IPrologEngine engine = scope.getEngine();
			Theory oldTheory = null;
			try {
				oldTheory = new Theory(engine.getTheory());
			} catch (InvalidTheoryException e1) {
				e1.printStackTrace();
			}
			Vector<String> files = scope.getFiles();
			if( files.size() > 0){
				Theory scopeTheory = null;
				for( int j = 0 ; j < files.size() ; j++)
				{
					try
					{
						IFile file = scope.getProject().getFile(files.get(j));
						if( j == 0)
							scopeTheory = new Theory(file.getContents());
						else
							scopeTheory.append(new Theory(file.getContents()));
					} catch (InvalidTheoryException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			else
			{
				engine.addTheory(null);
			}
			EventListener listener = new EventListener();
			engine.addOutputListener(listener);
			engine.addSpyListener(listener);
			engine.query(query.getQuery());
			
			do
			{
				PrologQueryResult result = new PrologQueryResult(engine);
				result.setSpy(listener.getSpy());
				result.setLost(listener.getSpy());
				result.setOutput(listener.getOutput());
				result.setResult(engine.next());
				query.addResult(result);
			}while( engine.hasOpenAlternatives() );
			
			PrologQueryResult lastResult = new PrologQueryResult(engine);
			lastResult.setSpy(listener.getSpy());
			lastResult.setLost(listener.getSpy());
			lastResult.setOutput(listener.getOutput());
			lastResult.setResult(engine.next());
			query.addResult(lastResult);
			addQuery(query);
			
			engine.removeSpyListeners();
			engine.removeOutputListeners();
			engine.addTheory(oldTheory);
		}
		return true;
	}
}
