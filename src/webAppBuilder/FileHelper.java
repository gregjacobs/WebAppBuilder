package webAppBuilder;

import java.util.*;
import java.io.*;

public class FileHelper {

	/**
	 * Fetch the entire contents of a text file, and return it in a String.
	 * This style of implementation throws Exceptions to the caller.
	 *
	 * @param aFile is a file which already exists and can be read.
	 */
	static public String getContents( File aFile ) throws FileNotFoundException, IOException {
		//...checks on aFile are elided
		StringBuilder contents = new StringBuilder();
		String lineSeparator = System.getProperty( "line.separator" );

		//use buffering, reading one line at a time
		//FileReader always assumes default encoding is OK!
		BufferedReader input = new BufferedReader( new FileReader( aFile ) );
		try {
			String line = null; //not declared within while loop
			/*
			 * readLine is a bit quirky :
			 * it returns the content of a line MINUS the newline.
			 * it returns null only for the END of the stream.
			 * it returns an empty String if two newlines appear in a row.
			 */
			while( (line = input.readLine()) != null ) {
				contents.append( line );
				contents.append( lineSeparator );
			}
		} finally {
			input.close();
		}

		return contents.toString();
	}


	/**
	 * Change the contents of text file in its entirety, overwriting any
	 * existing text.
	 *
	 * This style of implementation throws all exceptions to the caller.
	 *
	 * @param aFile is an existing file which can be written to.
	 * @throws IllegalArgumentException if param does not comply.
	 * @throws IOException if problem encountered during write.
	 */
	static public void setContents( File aFile, String aContents ) throws IOException {
		if( aFile == null ) {
			throw new IllegalArgumentException( "File should not be null." );
		}

		Writer output = new BufferedWriter( new FileWriter( aFile ) );
		try {
			output.write( aContents ); // FileWriter always assumes default encoding is OK!
		} finally {
			output.close();
		}
	}


	/**
	 * Given a filename or pathname string, adds the given suffix before the file extension,
	 * and returns the new string.
	 *
	 * @param filename
	 * @param suffix
	 * @return
	 */
	public static String insertFileSuffix( String filename, String suffix ) {
		int endOfDot = filename.lastIndexOf( "." );
		if( endOfDot == -1 ) {
			throw new RuntimeException( "No period in the target file output." );
		}
		return filename.substring( 0, endOfDot ) + suffix + filename.substring( endOfDot );
	}


	/**
	 * Lists the files in a given directory, with an optional filter, and the option of recursing into
	 * the subdirectories.
	 *
	 * @param directory The directory to list files from.
	 * @param filter A filter to use to only accept certain files. Set to null for no filter.
	 * @param recurse True to recurse the directory's subdirectories.
	 * @return The Collection of files.
	 */
	public static Collection<File> listDirectoryFiles( File directory, FilenameFilter filter, boolean recurse ) {
		List<File> files = new ArrayList<File>();

		// Get files / directories in the directory
		File[] entries = directory.listFiles();

		// Go over entries
		for( File entry : entries ) {
			// If there is no filter or the filter accepts the
			// file / directory, add it to the list
			if( filter == null || filter.accept( directory, entry.getName() ) ) {
				files.add( entry );
			}

			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if( recurse && entry.isDirectory() ) {
				files.addAll( listDirectoryFiles( entry, filter, recurse ) );
			}
		}

		// Return collection of files
		return files;
	}

}
