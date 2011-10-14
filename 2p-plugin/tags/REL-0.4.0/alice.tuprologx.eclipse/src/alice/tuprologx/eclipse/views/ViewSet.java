package alice.tuprologx.eclipse.views;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import alice.tuprologx.eclipse.core.PrologQuery;
import alice.tuprologx.eclipse.core.PrologQueryFactory;

public class ViewSet implements Observer {

	private Method2 method;
	@SuppressWarnings("unused")
	private ConsoleView console;
	@SuppressWarnings("unused")
	private QueryList queryList;
	private TheoryView theoryView;

	public ViewSet() {
		PrologQueryFactory.getInstance().addObserver(this);
	}

	public void refresh(final String theoryToShow) {
		try {
			final IWorkbenchPage wp = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			if (theoryView == null) {
				method = (alice.tuprologx.eclipse.views.Method2) wp
						.showView("alice.tuprologx.eclipse.views.MethodView");
				console = (alice.tuprologx.eclipse.views.ConsoleView) wp
						.showView("alice.tuprologx.eclipse.views.ConsoleView");
				queryList = (alice.tuprologx.eclipse.views.QueryList) wp
						.showView("alice.tuprologx.eclipse.views.QueryList");
				theoryView = (alice.tuprologx.eclipse.views.TheoryView) wp
						.showView("alice.tuprologx.eclipse.views.TheoryView");
				wp.showView(IPageLayout.ID_PROBLEM_VIEW);
			}


			method.refresh();
			theoryView.refresh(theoryToShow);
			queryList.addSelectionListener(new SelectionListener(){
				public void widgetSelected(SelectionEvent e) {
					TreeItem selection = (TreeItem)e.item;
					PrologQuery query = (PrologQuery) selection.getData();
					console.setQuery(query);
					wp.activate(console);
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			
		} catch (PartInitException e) {
		} catch (NullPointerException e) {
		}
	}

	@Override
	public void update(Observable arg0, Object arg1){
		queryList.update();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(console);
		console.setQuery(queryList.getSelectedQuery());
	}

}
