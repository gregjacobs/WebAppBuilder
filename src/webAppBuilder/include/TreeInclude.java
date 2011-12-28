package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.*;


/**
 * Represents a recursive directory and subdirectories include.
 */
public class TreeInclude extends DirectoryInclude {
	
	/**
	 * Creates a "tree include" directive.
	 */
	public TreeInclude( String directory, String fileExtension ) {
		super( directory, fileExtension );
	}
	
	
	/**
	 * Retrieves the collection of files that the TreeInclude represents.
	 */
	@Override
	public Collection<File> getFiles() {
		return FileHelper.listDirectoryFiles( directory, new ExtensionFilenameFilter(), /* recurse */ true );
	}
	
}
