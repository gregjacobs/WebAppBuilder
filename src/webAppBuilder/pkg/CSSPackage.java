package webAppBuilder.pkg;

import java.util.*;
import java.io.*;
import webAppBuilder.BuildOptions;
import webAppBuilder.include.Include;
import org.mozilla.javascript.*;
import com.yahoo.platform.yui.compressor.*;

/**
 * Represents a package of CSS files to be concatenated and minified.
 */
public class CSSPackage extends Package {

	/**
	 * Creates a CssPackage.
	 */
	public CSSPackage( String name, String filename, BuildOptions buildOptions ) {
		super( name, filename, buildOptions );
	}

	/**
	 * Creates a CssPackage.
	 */
	public CSSPackage( String name, String filename, List<Include> includes, BuildOptions buildOptions ) {
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
		
		CssCompressor compressor = new CssCompressor( inputReader );
		inputReader.close();
		
		// Compress the content
		int linebreakpos = -1;  // no line breaking
		compressor.compress( outputWriter, linebreakpos );
		
		return outputWriter.toString();
	}
	
}
