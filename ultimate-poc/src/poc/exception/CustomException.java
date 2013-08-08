package poc.exception;

public class CustomException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Custom exception class exception
	 * 
	 * @param message
	 */
	public CustomException(String message) {
		super(message);
	}

}
