package alice.tuprologx.eclipse.core;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;

import alice.tuprologx.eclipse.util.EventListener;

public class PrologQuery{

	private String query;
	private Vector<PrologQueryScope> scopes;
	private Vector<PrologQueryResult> results;

	public PrologQuery()
	{
		this.query = "";
	}
	
	public PrologQuery(String Query)
	{
		this.query = Query;
	}
	
	public void setQuery(String query)
	{
		this.query = query;
	}
	
	public String getQuery()
	{
		return this.query;
	}
	
	public Vector<PrologQueryScope> getAllScopes() {
		return scopes;
	}
	
	public void resetResults()
	{
		results = new Vector<PrologQueryResult>();
	}
	
	public Vector<IPrologEngine> getAllEngines()
	{
		Vector<IPrologEngine> engines = new Vector<IPrologEngine>();
		if(scopes != null)
		{
			for( int i = 0; i < scopes.size(); i++)
			{
				IPrologEngine engine = scopes.get(i).getEngine();
				if (!engines.contains(engine))
				{
					engines.add(engine);
				}
			}
		}
		return engines;
	}
	
	public PrologQueryScope getEngineScope(IPrologEngine engine)
	{
		if(scopes != null)
		{
			for( int i = 0; i < scopes.size(); i++)
			{
				if(scopes.get(i).getEngine() == engine)
				{
					return scopes.get(i);
				}
			}
		}
		
		return null;
	}
	
	public Vector<PrologQueryResult> getEngineSolutions(IPrologEngine engine)
	{
		Vector<PrologQueryResult> res = new Vector<PrologQueryResult>();
		if( results != null)
		{
			for( int i = 0; i < results.size(); i++)
			{
				if(results.get(i).getEngine().equals(engine))
				{
					res.add(results.get(i));
				}
			}
		}
		return res;
	}

	public void addScope(PrologQueryScope scope) {
		if(this.scopes == null)
		{
			this.scopes = new Vector<PrologQueryScope>();
		}
		this.scopes.add(scope);
	}
	
	public Vector<PrologQueryResult> getResults()
	{
		return results;
	}
	
	public void addResult(PrologQueryResult result)
	{
		if(results == null)
		{
			this.results = new Vector<PrologQueryResult>();
		}
		if(!results.contains(result))
		{
			this.results.add(result);
		}
	}
	
	public void removeResult(PrologQueryResult result)
	{
		results.remove(result);
	}
}