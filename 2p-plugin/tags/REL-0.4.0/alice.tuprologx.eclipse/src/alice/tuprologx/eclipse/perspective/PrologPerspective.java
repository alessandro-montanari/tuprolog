package alice.tuprologx.eclipse.perspective;

import org.eclipse.ui.*;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

public class PrologPerspective implements IPerspectiveFactory {

	public static String ID_PERSPECTIVE = "alice.tuprologx.eclipse.perspective.PrologPerspective";

	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();

		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.15f, editorArea);
		topLeft.addView(ProjectExplorer.VIEW_ID);
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		IFolderLayout tuP = layout.createFolder("topRight", IPageLayout.RIGHT,
				0.75f, editorArea);

		IFolderLayout util = layout.createFolder("Util", IPageLayout.BOTTOM,
				0.65f, editorArea);
		util.addView(IPageLayout.ID_TASK_LIST);
		util.addView(IPageLayout.ID_PROBLEM_VIEW);
		util.addView("alice.tuprologx.eclipse.views.ConsoleView");
		tuP.addView("alice.tuprologx.eclipse.views.MethodView");
		tuP.addView("alice.tuprologx.eclipse.views.QueryList");
		tuP.addView("alice.tuprologx.eclipse.views.TheoryView");

	}

}
