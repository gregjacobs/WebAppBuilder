package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.FileHelper;
import webAppBuilder.BuildOptions;
import webAppBuilder.BuildFileException;
import webAppBuilder.pkg.Package;


/**
 * Represents a recursive directory and subdirectories include.
 */
public class TreeInclude extends DirectoryInclude {

	/**
	 * Creates a "tree include" directive.
	 */
	public TreeInclude( Package pkg, String directory, BuildOptions buildOptions, String fileExtension ) {
		super( pkg, directory, buildOptions, fileExtension );
	}


	/**
	 * Retrieves the collection of files that the TreeInclude represents.
	 *
	 * @return The Collection of files in the tree.
	 * @throws BuildFileException If the directory that is referred to by this include does not exist.
	 */
	@Override
	public Collection<File> getFiles() {
		try {
			return FileHelper.listDirectoryFiles( directory, new ExtensionFilenameFilter(), /* recurse */ true );

		} catch( FileNotFoundException ex ) {
			throw new BuildFileException( "The directory referred to by a 'tree' include in the package '" + pkg.getName() + "' was not found. See cause.", ex );
		}
	}

}
