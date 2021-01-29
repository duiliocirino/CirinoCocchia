package src.main.java.exceptions;

/**
 * This class defines the exception used in the server side
 * in order to communicate to the client that there has been
 * an exception. This exceptions brings with itself a message
 * that can be later retrieved by java.lang.Throwable.getMessage()
 */
public class CLupException extends Exception {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Constructs a new exception with the specified detail 
	 * message
	 * @param message the detail message. The detail message is saved for 
	 * later retrieval by the getMessage() method
	 */
	public CLupException(String message) {
		super(message);
	}
}
