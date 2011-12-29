package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.FileHelper;
import webAppBuilder.BuildOptions;


/**
 * Represents a recursive directory and subdirectories include.
 */
public class TreeInclude extends DirectoryInclude {

	/**
	 * Creates a "tree include" directive.
	 */
	public TreeInclude( String directory, BuildOptions buildOptions, String fileExtension ) {
		super( directory, buildOptions, fileExtension );
	}


	/**
	 * Retrieves the collection of files that the TreeInclude represents.
	 */
	@Override
	public Collection<File> getFiles() {
		return FileHelper.listDirectoryFiles( directory, new ExtensionFilenameFilter(), /* recurse */ true );
	}

}
