package poc.document;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import poc.Backend;
import poc.bean.POCDocument;
import poc.bean.RequestBean;
import poc.constants.POCConstants;

public class POCDocumentProvider extends FileDocumentProvider {

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		// In practice `element` is a FileDocumentInput
		POCDocument document = (POCDocument) super.createDocument(element);
		// (FileEditorInput)element
		// String name = (FileEditorInput)element.getName;
		if (document != null) {
			IDocumentPartitioner partitioner = new POCDocumentPartitioner();
			document.setDocumentPartitioner(partitioner);
		}

		RequestBean requestBean = new RequestBean();
		requestBean.setModule(POCConstants.MODULE_EDITOR);
		requestBean.setMethod(POCConstants.METHOD_INIT);
		requestBean.getArgument().setMode(POCConstants.MODE_HTML);

		try {
			document.setGuid(Double.toString((Double) Backend.get().rpc(requestBean).get(POCConstants.KEY_GUID)));
			document.addDocumentListener(new POCDocumentListener(document));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	@Override
	protected IDocument createEmptyDocument() {
		return new POCDocument();
	}
}
