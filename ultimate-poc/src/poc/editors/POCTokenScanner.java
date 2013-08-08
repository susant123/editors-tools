package poc.editors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import poc.Backend;
import poc.bean.POCDocument;
import poc.bean.RequestBean;
import poc.constants.POCConstants;
import poc.exception.CustomException;

public class POCTokenScanner implements ITokenScanner {

	// Tokens ------------------------------------------------------------------
	private List<Map<String, Object>> tokens = null;
	private Iterator<Map<String, Object>> tokensIterator = null;
	private Map<String, Object> currentToken = null;

	// Styles ------------------------------------------------------------------

	private Map<String, Object> defaultStyle = null;
	private Map<String, Object> styles = null;

	private static boolean safeCache = true;
	private Map<String, Object> stylesheets = new HashMap<String, Object>();

	public POCTokenScanner() {
	}

	// Data update

	@Override
	@SuppressWarnings("unchecked")
	public void setRange(IDocument document, int offset, int length) {
		POCDocument doc = (POCDocument) document;

		try {
			// Mode ------------------------------------------------------------
			String mode = doc.getMode();
			mode = "editor"; // Just hard coding to make it work

			// Styles ----------------------------------------------------------
			this.getStylesheet(mode);

			// Tokens ----------------------------------------------------------

			RequestBean requestBean = new RequestBean();
			requestBean.setModule(mode);
			requestBean.setMethod(POCConstants.METHOD_EXEC);
			requestBean.getArgument().setService(POCConstants.SERVICE_HIGHLIGHT);
			requestBean.getArgument().setGuid("1");
			requestBean.getArgument().setSource(document.get());
			requestBean.getArgument().setStart(offset);
			requestBean.getArgument().setEnd(offset + length);
			tokens = (List<Map<String, Object>>) Backend.get().rpc(requestBean).get(POCConstants.TOKENS_KEY);
			if (null != tokens) {
				tokensIterator = tokens.iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Stylesheet update

	// Cache -------------------------------------------------------------------

	// TODO Create or find a generic cache utility. Integrate this cache utility
	// in the Backend class? It would make sense!! This is not optmize
	// communication with the backend, which should implement a generic cache
	// system
	@SuppressWarnings("unchecked")
	private void getStylesheet(String mode) throws IOException, CustomException {
		Map<String, Object> stylesheet = null;
		RequestBean requestBean = new RequestBean();
		requestBean.setModule(POCConstants.MODULE_EDITOR);
		requestBean.setMethod(POCConstants.METHOD_EXEC);
		requestBean.getArgument().setGuid("1");
		requestBean.getArgument().setService(POCConstants.SERVICE_STYLESHEET);

		// Cache ---------------------------------------------------------------
		if (stylesheets.containsKey(mode)) {
			if (safeCache) {
				Map<String, Object> options = new HashMap<String, Object>();
				options.put("checkCache", true);
				options.put("sendIfObsolete", true);

				// Map<String, Object> response = Backend.get().rpc(mode,
				// STYLESHEET_KEY, options);
				Map<String, Object> response = Backend.get().rpc(requestBean);
				if ((Boolean) response.get("obsolete")) {
					stylesheet = (Map<String, Object>) response.get(POCConstants.SERVICE_STYLESHEET);
					stylesheets.put(mode, stylesheet);
				} else {
					stylesheet = (Map<String, Object>) stylesheets.get(mode);
				}
			} else {
				stylesheet = (Map<String, Object>) stylesheets.get(mode);
			}
		} else {
			stylesheet = (Map<String, Object>) Backend.get().rpc(requestBean);

			stylesheets.put(mode, stylesheet);
		}

		// Stylesheet extractions ----------------------------------------------
		defaultStyle = (Map<String, Object>) stylesheet.get(POCConstants.KEY_STYLE);
		styles = (Map<String, Object>) stylesheet.get(POCConstants.TOKENS_KEY);
	}

	// Tokens management

	@Override
	public IToken nextToken() {
		if (this.tokensIterator == null || !this.tokensIterator.hasNext()) {
			return Token.EOF;
		}

		this.currentToken = this.tokensIterator.next();
		String type = (String) this.currentToken.get(POCConstants.TOKEN_TYPE_KEY);

		if (type.equals(POCConstants.WS_TOKEN_TYPE_NAME)) {
			return Token.WHITESPACE;
		}

		return new Token(this.getAttribute(type));
	}

	// TODO Caching?
	@SuppressWarnings("unchecked")
	private TextAttribute getAttribute(String type) {
		Map<String, Object> style = (Map<String, Object>) this.styles.get(type);
		if (style == null) {
			style = this.defaultStyle;
		}

		Map<String, Object> rgb = (Map<String, Object>) style.get(POCConstants.COLOR_KEY);
		if (rgb == null) {
			rgb = (Map<String, Object>) defaultStyle.get(POCConstants.COLOR_KEY);
		}
		return new TextAttribute(new Color(Display.getCurrent(), ((Number) rgb.get(POCConstants.RED_KEY)).intValue(),
				((Number) rgb.get(POCConstants.GREEN_KEY)).intValue(),
				((Number) rgb.get(POCConstants.BLUE_KEY)).intValue()));
	}

	// Locations ---------------------------------------------------------------

	@Override
	public int getTokenOffset() {
		return ((Number) currentToken.get(POCConstants.START_KEY)).intValue();
	}

	@Override
	public int getTokenLength() {
		return (((Number) currentToken.get(POCConstants.END_KEY)).intValue()) - this.getTokenOffset();
	}

}
