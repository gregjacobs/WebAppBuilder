package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.FileHelper;
import webAppBuilder.BuildOptions;
import webAppBuilder.BuildFileException;
import webAppBuilder.pkg.Package;


/**
 * Represents a single, non-recursive, directory include.
 */
public class DirectoryInclude extends Include {

	protected File directory;
	protected BuildOptions buildOptions;
	protected String fileExtension;


	/**
	 * Creates a "directory include" directive.
	 */
	public DirectoryInclude( Package pkg, String directory, BuildOptions buildOptions, String fileExtension ) {
		super( pkg );

		this.directory = new File( directory );

		// If the directory is a relative path, we must add the build.json file's directory,
		// as the directory should be relative to the build.json, and not the current working directory
		if( !this.directory.isAbsolute() ) {
			this.directory = new File( buildOptions.getBuildFileDir() + directory );
		}

		this.buildOptions = buildOptions;
		this.fileExtension = fileExtension;
	}


	/**
	 * Retrieves the collection of files that the DirectoryInclude represents.
	 *
	 * @return The Collection of files in the directory.
	 * @throws BuildFileException If the directory that is referred to by this include does not exist.
	 */
	@Override
	public Collection<File> getFiles() {
		try {
			return FileHelper.listDirectoryFiles( directory, new ExtensionFilenameFilter(), /* recurse */ false );

		} catch( FileNotFoundException ex ) {
			throw new BuildFileException( "The directory referred to by a 'directory' include in the package '" + pkg.getName() + "' was not found. See cause.", ex );
		}
	}

	/**
	 * The FilenameFilter for only accepting files with the fileExtension provided
	 * to the DirectoryInclude.
	 */
	protected class ExtensionFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept( File dir, String name ) {
			return name.endsWith( fileExtension );
		}
	}

}
