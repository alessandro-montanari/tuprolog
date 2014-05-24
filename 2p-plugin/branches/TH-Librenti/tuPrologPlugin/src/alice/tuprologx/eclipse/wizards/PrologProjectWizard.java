package alice.tuprologx.eclipse.wizards;

import java.lang.reflect.InvocationTargetException;

import alice.tuprolog.lib.IOLibrary;
import alice.tuprologx.eclipse.core.*;
import alice.tuprologx.eclipse.properties.PropertyManager;
import alice.tuprologx.eclipse.views.ConsoleView;
import alice.tuprologx.eclipse.TuProlog;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.Display;


public class PrologProjectWizard extends Wizard implements INewWizard {

	private PrologProjectWizardPage page;
	private Display display;
	private ConsoleView console;

	public PrologProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
		display = Display.getDefault();
	}

	public void addPages() {
		page = new PrologProjectWizardPage();
		addPage(page);
	}

	/*
	 * This method is called when 'Finish' button is pressed in the wizard.
	 */
	public boolean performFinish() {
		final String projectName = page.getProjectName();
		final String[] libraries = page.getLibrariesToLoad();
		final boolean allTheories = page.allTheories();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(projectName, libraries, allTheories, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}

		return true;

	}

	private void doFinish(String projectName, String[] libraries,
			boolean allTheories, IProgressMonitor monitor) throws CoreException {

		display.asyncExec(new Runnable() {
			public void run() {
				TuProlog.showPerspective();
			}
		});

		// create the prolog project
		monitor.beginTask("Creating " + projectName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		final IProject project = root.getProject(projectName);

		// creazione ed apertura del project
		if (!(project.exists())) {
			project.create(monitor);
			project.open(monitor);
		}

		// associazione della nature
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = PrologNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		monitor.worked(1);

		// creazione e configurazione del motore
		PrologEngine engine = PrologEngineFactory.getInstance().insertEntry(
				projectName, "Engine1");
		String[] libs = engine.getLibrary();
		for (int i = 0; i < libs.length; i++)
			engine.removeLibrary(libs[i]);

		for (int i = 0; i < libraries.length; i++)
			engine.addLibrary(libraries[i]);
		/**
		 * As the JavaIDE the execution of the application has been set IO.setExecutionType(IOLibrary.graphicExecution)
		 */
		final IOLibrary IO = (IOLibrary)engine.getLibrary("alice.tuprolog.lib.IOLibrary");
		if (IO != null) { // IOLibrary could not be loaded
			IO.setExecutionType(IOLibrary.graphicExecution);
			System.out.println("IO.setExecutionType(IOLibrary.graphicExecution);");
			/** 
			 * PrologProjectWizard sets the UserContextInputStream to ConsoleView
			 * so it takes a reference of ConsoleView via the following operations
			 */
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() { 
				@Override
				public void run() { 
					IWorkbenchWindow dwindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow(); 
					final IWorkbenchPage wp = dwindow.getActivePage(); 
					try {
						IViewReference[] viewList = wp.getViewReferences();
						for(IViewReference ref : viewList){
							if(ref.getId().equalsIgnoreCase("alice.tuprologx.eclipse.views.ConsoleView")){
								console = (ConsoleView) ref.getView(false);
								console.setUserContextInputStream(IO.getUserContextInputStream());
							}
						}
					}
					catch (NullPointerException e) {
					}
				}});
		}
			
		PropertyManager.addEngineInProperty(project, engine.getName());
		PropertyManager.setLibraryInProperties(project, engine.getName(),
				libraries);
		PropertyManager.setTheoriesInProperty(project, engine.getName(), null,
				allTheories);
		monitor.worked(2);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}
