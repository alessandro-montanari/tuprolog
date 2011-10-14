package alice.tuprologx.eclipse.editors;

import alice.tuprologx.eclipse.scanners.*;
import alice.tuprologx.eclipse.util.TokenManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * Source viewer configuration for Prolog code. Implements a presentation
 * reconciler for syntax highligting
 */
public class PrologSourceViewerConfiguration extends SourceViewerConfiguration {

	private final TokenManager tokenManager;

	public PrologSourceViewerConfiguration(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return PrologPartitionScanner.getLegalContentTypes();
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {

		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler
				.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr;
		// dr = new MyDamagerRepairer(new DefaultScanner(tokenManager));
		dr = new DefaultDamagerRepairer(new DefaultScanner(tokenManager));
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(new CommentScanner(tokenManager));
		reconciler.setDamager(dr, PrologPartitionScanner.PROLOG_COMMENT);
		reconciler.setRepairer(dr, PrologPartitionScanner.PROLOG_COMMENT);

		dr = new ListDamagerRepairer(new ListScanner(tokenManager));
		reconciler.setDamager(dr, PrologPartitionScanner.PROLOG_LIST);
		reconciler.setRepairer(dr, PrologPartitionScanner.PROLOG_LIST);

		return reconciler;
	}
}
