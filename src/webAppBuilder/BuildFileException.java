package webAppBuilder;

/**
 * An exception thrown when the input build.json file has a problem.
 * This can be that an expected property is not found or cannot be read,
 * or that a file / directory specified by the build.json file cannot be found.
 */
public class BuildFileException extends RuntimeException {

	public BuildFileException( String message ) {
		super( message );
	}

	public BuildFileException( String message, Throwable cause ) {
		super( message, cause );
	}

}
