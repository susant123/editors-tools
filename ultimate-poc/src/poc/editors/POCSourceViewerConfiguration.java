package poc.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import poc.Backend;
import poc.bean.RequestBean;
import poc.constants.POCConstants;
import poc.document.POCDocumentPartitioner;
import poc.exception.CustomException;

public class POCSourceViewerConfiguration extends SourceViewerConfiguration {

	// FIXME Should be taken from somewhere. But I don't see, as a mode is
	// related to a document, and here the viewer configuration is agnostic of
	// the document.
	// A solution would be to take a reference to the editor, that way we could
	// be able to request from it the document. Then the mode. But in that case,
	// there is no pre-fetch of the configuration data.

	private Map<String, Object> configuration = null;

	public POCSourceViewerConfiguration() {
		super();
		try {
			RequestBean requestBean = new RequestBean();
			requestBean.setModule(POCConstants.MODULE_EDITOR);
			requestBean.setMethod(POCConstants.METHOD_CONFIGURATION);

			configuration = Backend.get().rpc(requestBean);
		} catch (CustomException e) {
			configuration = new HashMap<String, Object>();
			configuration.put(POCConstants.KEY_TAB_WIDTH, POCConstants.DEFAULT_TAB_WIDTH);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// General configuration

	@Override
	public int getTabWidth(ISourceViewer sourceViewer) {
		if (configuration != null) {
			return ((Double) configuration.get(POCConstants.KEY_TAB_WIDTH)).intValue();
		}
		return super.getTabWidth(sourceViewer);
	}

	// Highlighting

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new POCTokenScanner());
		reconciler.setDamager(dr, POCConstants.PARTITION_NAME);
		reconciler.setRepairer(dr, POCConstants.PARTITION_NAME);

		return reconciler;
	}

	// Pending implementation

	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		return super.getContentFormatter(sourceViewer);
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		return super.getContentAssistant(sourceViewer);
	}

}
