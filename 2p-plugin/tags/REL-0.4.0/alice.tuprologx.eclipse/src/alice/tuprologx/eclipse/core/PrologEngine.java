package alice.tuprologx.eclipse.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import alice.tuprolog.*;
import alice.tuprolog.event.*;

class PrologEngine implements IPrologEngine {
	private String name;
	private IProject project;
	private Prolog prolog;
	private SolveInfo si = null;
	public static Display display;
	@SuppressWarnings("unused")
	private boolean hasNext;

	private void setNext(boolean next) {
		this.hasNext = next;
	}

	public boolean hasOpenAlternatives() {
		return prolog.hasOpenAlternatives();
	}

	public PrologEngine(String projectName, String name) {
		this.name = name;
		prolog = new Prolog();
		prolog.setSpy(true);
		project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		display = Display.getDefault();
	}

	public void rename(String name) {
		this.name = name;
	}

	public void refresh(String theoryToShow) {
	}

	public String getTheory() {
		Theory theory = prolog.getTheory();
		return theory.toString();
	}

	public void addTheory(Theory theory) {
		try {
			prolog.clearTheory();
			if (theory != null)
				prolog.addTheory(theory);
		} catch (InvalidTheoryException e) {
		}
	}

	public void stop() {
		prolog.solveHalt();
		restoreScope();
	}

	public void accept() {
		prolog.solveEnd();
		restoreScope();
	}

	private void restoreScope() {
		if (PrologBuilder.isAlternativeBuild()) {
			try {
				project.build(IncrementalProjectBuilder.FULL_BUILD,
						PrologBuilder.BUILDER_ID, null, null);
				PrologBuilder.setAlternativeBuild(false);
			} catch (CoreException e) {
			}
		}
	}

	public String next() {
		if (si == null) {
			setNext(false);
			return "";
		}
		String result = "";
		try {
			if (si.isSuccess()) {
				String binds = si.toString();

				if (!prolog.hasOpenAlternatives()) {
					if (binds.equals("")) {
						result = "yes.\nSolution: "
								+ prolog.toString(si.getSolution());
						setNext(false);
					} else {
						result = "yes.\nSolution: " + binds + "\n"
								+ prolog.toString(si.getSolution());
						setNext(false);
					}
					si = null;

				} else {
					result = "yes.\nSolution: " + binds + "\n"
							+ prolog.toString(si.getSolution()) + "?";
					si = prolog.solveNext();
					setNext(true);
				}
			} else {
				result = "no";
				setNext(false);
				si = null;
				restoreScope();

			}
			return result;
		} catch (NoSolutionException nse) {
			nse.printStackTrace();
		} catch (NoMoreSolutionException nmse) {
			nmse.printStackTrace();
		}
		return result;
	}

	public void query(final String q) {
		try {
			si = prolog.solve(q);
		} catch (MalformedGoalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] getLibrary() {
		if (prolog == null)
			return new String[] {};
		return prolog.getCurrentLibraries();
	}

	public void removeLibrary(String lib) {
		try {
			prolog.unloadLibrary(lib);
		} catch (InvalidLibraryException ile) {
		}
	}

	public void addLibrary(String lib) {
		try {
			if (prolog.getLibrary(lib) == null)
				prolog.loadLibrary(lib);
		} catch (InvalidLibraryException ile) {
		}
	}

	public String getName() {
		return name;
	}

	public void addSpyListener(SpyListener spyListener) {
		prolog.addSpyListener(spyListener);
	}

	public void addOutputListener(OutputListener outputListener) {
		prolog.addOutputListener(outputListener);
	}

	public void removeSpyListeners() {
		prolog.removeAllSpyListeners();
	}

	public void removeOutputListeners() {
		prolog.removeAllOutputListeners();

	}

}