package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.BuildOptions;
import webAppBuilder.BuildFileException;
import webAppBuilder.pkg.Package;

/**
 * Represents a single file include.
 */
public class FileInclude extends Include {

	private File file;


	/**
	 * Creates a "file include" directive.
	 */
	public FileInclude( Package pkg, String file, BuildOptions buildOptions ) {
		super( pkg );

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
	 * @return The file represented by the FileInclude, in a Collection.
	 * @throws BuildFileException If the file that is referred to by this include does not exist.
	 */
	@Override
	public Collection<File> getFiles() {
		if( !file.exists() ) {
			throw new BuildFileException( "The file '" + file.getAbsolutePath() + "' referred to by a 'file' include in the package '" + pkg.getName() + "' was not found." );
		}

		List<File> list = new LinkedList<File>();
		list.add( file );
		return list;
	}

}
