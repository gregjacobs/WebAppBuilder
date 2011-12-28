package webAppBuilder.pkg;

import java.util.*;
import java.io.*;
import webAppBuilder.BuildOptions;
import webAppBuilder.include.Include;
import org.mozilla.javascript.*;
import com.yahoo.platform.yui.compressor.*;

/**
 * A package of JavaScript files to be concatenated and minified.
 */
public class JavaScriptPackage extends Package {
	
	/**
	 * Creates a JavaScriptPackage.
	 */
	public JavaScriptPackage( String name, String filename, List<Include> includes, BuildOptions buildOptions ) {
		super( name, filename, includes, buildOptions );
	}
	
	/**
	 * Compresses the output files
	 * 
	 * @param combinedContents The combined (unminified) contents of the package's files)
	 * @throws IOException
	 */
	@Override
	public String createMinifiedContents( String combinedContents ) throws IOException {
		Reader inputReader = new StringReader( combinedContents );
		Writer outputWriter = new StringWriter();
		
		JavaScriptCompressor compressor = new JavaScriptCompressor( inputReader, new ErrorReporter() {
			@Override
			public void warning( String message, String sourceName, int line, String lineSource, int lineOffset ) {
				if( line < 0 ) {
					System.err.println( "\n    [COMPRESSOR WARNING] " + message );
				} else {
					System.err.println( "\n    [COMPRESSOR WARNING] " + line + ':' + lineOffset + ':' + message );
				}
			}

			@Override
			public void error( String message, String sourceName, int line, String lineSource, int lineOffset ) {
				if( line < 0 ) {
					System.err.println( "\n    [COMPRESSOR ERROR] " + message );
				} else {
					System.err.println( "\n    [COMPRESSOR ERROR] " + line + ':' + lineOffset + ':' + message );
				}
			}
			
			@Override
			public EvaluatorException runtimeError( String message, String sourceName, int line, String lineSource, int lineOffset ) {
				error( message, sourceName, line, lineSource, lineOffset );
				return new EvaluatorException( message );
			}
		} );
		
		inputReader.close();
		
		
		// Compress the content
		boolean munge = true;
		boolean verbose = false;
		boolean preserveAllSemiColons = false;
		boolean disableOptimizations = false;
		int linebreakpos = -1;  // no line breaking
		compressor.compress( outputWriter, linebreakpos, munge, verbose, preserveAllSemiColons, disableOptimizations );
		
		
		return outputWriter.toString();
	}
	
}
