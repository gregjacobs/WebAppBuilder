package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.*;


/**
 * Represents a recursive directory and subdirectories include.
 */
public class TreeInclude extends DirectoryInclude {
	
	public TreeInclude( String directory, String fileExtension ) {
		super( directory, fileExtension );
	}
	
	
	@Override
	public Collection<File> getFiles() {
		return FileHelper.listDirectoryFiles( directory, new ExtensionFilenameFilter(), /* recurse */ true );
	}
	
}
