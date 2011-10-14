package alice.tuprologx.eclipse.core;

import alice.tuprolog.*;
import alice.tuprologx.eclipse.properties.*;
import alice.tuprologx.eclipse.TuProlog;
import alice.tuprologx.eclipse.editors.PrologEditor;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorPart;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

public class PrologParser extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "alice.tuprologx.eclipse.prologParser";
	@SuppressWarnings("unchecked")
	public static Vector t;
	private Parser parser;
	private boolean correct = true;
	@SuppressWarnings("unchecked")
	private static Vector scope;
	private static boolean allTheories = false;
	@SuppressWarnings("unchecked")
	private static Vector[] alternativeScope;
	private static String projectName;
	@SuppressWarnings("unchecked")
	private Vector terms;
	private IMarker mark;
	private static Theory theory; // la teoria da passare al builder in caso di
									// parse corretto
	public static boolean go = false;

	@SuppressWarnings("unchecked")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (go == true) {
			go = false;
			t = new Vector();
			alternativeScope = new Vector[PrologEngineFactory.getInstance()
					.getProjectEngines(projectName).size()];
			for (int i = 0; i < alternativeScope.length; i++) {
				alternativeScope[i] = new Vector();
				String tmp = "";
				try {
					tmp = args.get(new Integer(i)).toString();
				} catch (Exception e) {
				}
				StringTokenizer st = new StringTokenizer(tmp, ";");
				while (st.hasMoreTokens()) {
					String t = st.nextToken();
					alternativeScope[i].add(t);
				}
			}
		} else
			alternativeScope = null;

		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void fullBuild(final IProgressMonitor monitor) {
		t = new Vector();
		IProject project = getProject();
		projectName = project.getName();
		for (int j = 0; j < PrologEngineFactory.getInstance()
				.getProjectEngines(project.getName()).size(); j++) {
			IPrologEngine engine = PrologEngineFactory.getInstance().getEngine(
					project.getName(), j);

			if (alternativeScope == null) {
				try {
				if (PropertyManager.allTheories(project, engine.getName())) {
					Vector theories = new Vector();
					
						IResource[] resources = project.members();
						for (int i = 0; i < resources.length; i++)
							if ((resources[i] instanceof IFile)
									&& (resources[i].getName().endsWith(".pl")))
								theories.add(resources[i].getName());
					scope = theories;
				} else
					scope = PropertyManager.getTheoriesFromProperties(project,
							engine.getName());
				} catch (Exception e) 
				{
					e.printStackTrace();
				}

			}

			else {
				try {
					scope = alternativeScope[j];
				} catch (Exception e) {
				}
			}
			try {
				terms = new Vector();
				project.accept(new TheoryVisitor());
				if (correct) {

					Term[] termsArray = new Term[terms.size()];
					for (int i = 0; i < terms.size(); i++)
						termsArray[i] = (Term) (terms.elementAt(i));

					Struct struct = new Struct(termsArray);
					try {
						theory = new Theory(struct);
					} catch (InvalidTheoryException e) {
					} // non dovrebbe mai essere lanciata

					// TODO: pulire la tabella dei markers?

				} else {
					theory = null;
					correct = true;
				}

				// engine.addTheory(getTheory());
				t.add((Theory) getTheory());
			} catch (CoreException e) {
			}

		}
	}

	class TheoryVisitor implements IResourceVisitor {
		@SuppressWarnings("unchecked")
		public boolean visit(IResource resource) {
			String theoryText = "";
			int result;
			if (checkTheory(resource)) {

				/*
				 * verifico se esiste un editor aperto sulla mia risorsa
				 */
				boolean existsAnEditor = false;
				boolean isStartup = false;
				IEditorReference[] editorReferences = null;
				IEditorPart editor = null;

				try {
					editorReferences = TuProlog.getDefault().getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getEditorReferences();
				} catch (Exception e) {
					isStartup = true;
				}

				if (!(isStartup)) {

					for (int i = 0; i < editorReferences.length; i++) {

						editor = editorReferences[i].getEditor(false);
						if (editor instanceof PrologEditor) {
							PrologEditor pEditor = (PrologEditor) editor;
							// String pro=pEditor.getProject().getName();
							// String pro2=resource.getProject().getName();
							// String in=pEditor.getEditorInput().getName();
							// String in2=resource.getName();
							if ((pEditor.getProject().getName().equals(resource
									.getProject().getName()))
									&& (pEditor.getEditorInput().getName()
											.equals(resource.getName()))) {
								existsAnEditor = true;
								break;
							}

						}
					}

				}

				if (existsAnEditor) {
					theoryText = ((PrologEditor) (editor)).getText();
				} else {
					theoryText = openFile((IFile) resource);
				}

				boolean flag = true;
				parser = new Parser(theoryText);
				while (flag) {
					result = parser.readTerm(true);

					// String debug=parser.getCurrentTerm().toString();

					switch (result) {
					case (Parser.TERM):

						if (isTheoryInScope(resource)) {
							terms.add(parser.getCurrentTerm());
						}

						break;
					case (Parser.ERROR):
						// richiama segnalazione errori
						showError((IFile) resource, parser.getCurrentLine());
						flag = false;
						if (isTheoryInScope(resource)) {
							correct = false;
						}
						break;
					default:
						flag = false;
						break;
					}
				}

			}
			// return true to continue visiting children.
			return true;
		}
	}

	// metodo che verifica se la risorsa è una teoria
	private boolean checkTheory(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".pl")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			return true;
		} else
			return false;
	}

	// metodo che verifica se la risorsa fa parte dello scope
	private boolean isTheoryInScope(IResource resource) {
		if (allTheories)
			return true;

		if (scope.contains(resource.getName()))
			return true;
		else
			return false;
	}

	private void deleteMarkers(IResource resource) {
		try {
			resource.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	private void showError(IFile file, int line) {
		try {
			mark = file.createMarker(IMarker.PROBLEM);
			mark.setAttribute(IMarker.LINE_NUMBER, line);
			mark.setAttribute(IMarker.MESSAGE, "Syntax Error at/before line "
					+ line);
			mark.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			mark.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {
		}
	}

	private String openFile(IFile file) {
		String ilFile = new String();

		try {
			String path = file.getLocation().toOSString();

			BufferedReader in = new BufferedReader(new FileReader(path));

			String linea = new String();

			while ((linea = in.readLine()) != null) {
				ilFile += linea;
				ilFile += System.getProperty("line.separator");
			}

			in.close();
		} catch (IOException e) {
			ilFile = null;
		}

		return ilFile;

	}

	public static Theory getTheory() {
		return theory;
	}

	public static String getProjectName() {
		return projectName;
	}

}
