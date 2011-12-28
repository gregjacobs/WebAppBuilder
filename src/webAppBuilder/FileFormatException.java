package webAppBuilder;

/**
 * An exception thrown when the input build.json file has a problem.
 */
public class FileFormatException extends Exception {
	
	public FileFormatException( String message ) {
		super( message );
	}
	
}
