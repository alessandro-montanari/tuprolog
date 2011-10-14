package alice.tuprologx.eclipse;

import alice.tuprologx.eclipse.core.OpenProjectListener;
import alice.tuprologx.eclipse.util.*;
import alice.tuprologx.eclipse.perspective.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.plugin.*;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.ui.IViewReference;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class TuProlog extends AbstractUIPlugin {
	// The shared instance.
	private static TuProlog plugin;
	// Resource bundle.
	private ResourceBundle resourceBundle;
	public final static String PROLOG_PARTITIONING = "__prolog_partitioning"; //$NON-NLS-1$

	private TokenManager tokenManager;

	public static String[] PROLOG_EXTENSIONS = { "pro", "pl" };

	/**
	 * The constructor.
	 */
	public TuProlog() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("alice.tuprologx.eclipse.TuPrologResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		/*
		 * qui recuperare le proprietà persistenti, creare i motori richiamo un
		 * metodo statico di una classe apposita per es.
		 */
		alice.tuprologx.eclipse.properties.PropertyManager
				.initializeWorkspace();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new OpenProjectListener());

	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static TuProlog getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = TuProlog.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * 
	 * @uml.property name="resourceBundle"
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	// *** Start code for visualization support ***

	/**
	 * Returns the Prolog token manager.
	 */
	public TokenManager getTokenManager() {
		if (tokenManager == null)
			tokenManager = new TokenManager(this.getPreferenceStore());
		return tokenManager;
	}
	
	public static IProject getActiveProject() {
		IWorkbenchWindow win = getDefault().getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = win.getActivePage();
	        if (page != null) {
	            IEditorPart editor = page.getActiveEditor();
	            if (editor != null) {
	                IEditorInput input = editor.getEditorInput();
	                if (input instanceof IFileEditorInput) {
	                    return ((IFileEditorInput)input).getFile().getProject();
	                }
	            }
	        }
	        return null;
	}

	public static void showPerspective() {
		IWorkbench workbench = TuProlog.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IAdaptable input;
		if (page != null)
			input = page.getInput();
		else
			input = ResourcesPlugin.getWorkspace().getRoot();
		try {
			workbench.showPerspective(PrologPerspective.ID_PERSPECTIVE, window,
					input);
		} catch (WorkbenchException e) {
		}
	}

	public static void log(final IStatus status) {
		getDefault().getLog().log(status);
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public static Image getIconFromResources(String filename) {
		Image icon = null;

		Bundle bundle = Platform.getBundle("alice.tuprologx.eclipse");
		URL url = bundle.getEntry("icons/" + filename);
		String absPath = null;
		try {
			absPath = FileLocator.toFileURL(url).getFile().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		icon = new Image(null, absPath);
		return icon;
	}

}
