package poc.bean;

public class RequestBean {

	private String module;
	private String method;
	private POCDocument argument;

	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the argument
	 */
	public POCDocument getArgument() {
		if (null == argument) {
			argument = new POCDocument();
		}
		return argument;
	}

	/**
	 * @param argument
	 *            the argument to set
	 */
	public void setArgument(POCDocument argument) {
		if (null == this.argument) {
			this.argument = new POCDocument();
			this.argument = argument;
		} else {
			this.argument = argument;
		}
	}
}
