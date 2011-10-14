package alice.tuprologx.eclipse.core;

import alice.tuprolog.Theory;
import alice.tuprolog.event.OutputListener;
import alice.tuprolog.event.SpyListener;

public interface IPrologEngine {

	public void addTheory(Theory theory);

	public String getTheory();

	public void rename(String name);

	public void stop();

	public void accept();

	public String next();

	public void query(final String q);

	public String[] getLibrary();

	public void removeLibrary(String lib);

	public void addLibrary(String lib);

	// public void refresh();
	public void refresh(String theoryToShow);

	public String getName();

	public void addSpyListener(SpyListener spyListener);

	public void addOutputListener(OutputListener outputListener);

	public void removeSpyListeners();

	public void removeOutputListeners();

	public boolean hasOpenAlternatives();

}
