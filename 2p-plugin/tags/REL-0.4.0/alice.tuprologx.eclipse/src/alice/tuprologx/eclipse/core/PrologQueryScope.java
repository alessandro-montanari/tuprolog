package alice.tuprologx.eclipse.core;

import java.util.Vector;

import org.eclipse.core.resources.IProject;

public class PrologQueryScope
{
	//Ogni engine ha uno scope
	private IProject project;
	private IPrologEngine engine;
	private Vector<String> files;
	
	public PrologQueryScope(IPrologEngine engine)
	{
		this.engine = engine;
		files = new Vector<String>();
	}
	
	public IProject getProject() {
		return project;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public IPrologEngine getEngine() {
		return engine;
	}
	
	public Vector<String> getFiles() {
		return files;
	}
	
	public void setFiles(Vector<String> files) {
		this.files = files;
	}
	
	public void addFile(String filename) {
		if(! this.files.contains(filename))
		{
			this.files.add(filename);
		}
	}
	
	public void removeFile(String filename)
	{
		this.files.remove(filename);
	}
}
