package poc.document;

import java.io.IOException;

import org.apache.http.ParseException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import poc.Backend;
import poc.bean.POCDocument;
import poc.bean.RequestBean;
import poc.constants.POCConstants;
import poc.exception.CustomException;

import com.google.gson.JsonSyntaxException;

public class POCDocumentListener implements IDocumentListener {

	private POCDocument document = null;

	public POCDocumentListener(POCDocument document) {
		this.document = document;
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	@Override
	public void documentChanged(DocumentEvent event) {

		RequestBean requestBean = new RequestBean();
		requestBean.setModule(POCConstants.MODULE_EDITOR);
		requestBean.setMethod(POCConstants.METHOD_UPDATE);
		requestBean.getArgument().setGuid(this.document.getGuid());
		requestBean.getArgument().setStart(event.getOffset());
		requestBean.getArgument().setEnd(event.getOffset() + event.getLength());
		requestBean.getArgument().setSource(event.getText());

		try {
			Backend.get().rpc(requestBean);
		} catch (CustomException | JsonSyntaxException | ParseException | IOException e) {
			e.printStackTrace();
		}
	}

}
