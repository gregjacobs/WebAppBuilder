package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.BuildOptions;

/**
 * Represents a single file include.
 */
public class FileInclude extends Include {

	private File file;


	/**
	 * Creates a "file include" directive.
	 */
	public FileInclude( String file, BuildOptions buildOptions ) {
		this.file = new File( file );

		// If the file is a relative path, we must add the build.json file's directory,
		// as the file should be relative to the build.json, and not the current working directory
		if( !this.file.isAbsolute() ) {
			this.file = new File( buildOptions.getBuildFileDir() + file );
		}
	}


	/**
	 * Retrieves the collection of files that the FileInclude represents.
	 * Because of the nature of a FileInclude, this is only a single file
	 * (as opposed to other subclasses of Include, which may return more
	 * than one).
	 *
	 * @return
	 */
	@Override
	public Collection<File> getFiles() {
		List<File> list = new LinkedList<File>();
		list.add( file );

		return list;
	}

}
