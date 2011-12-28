package webAppBuilder.include;

import java.util.*;
import java.io.*;
import webAppBuilder.*;


/**
 * Represents a single, non-recursive, directory include.
 */
public class DirectoryInclude extends Include {
	
	protected File directory;
	protected String fileExtension;
	
	public DirectoryInclude( String directory, String fileExtension ) {
		this.directory = new File( directory );
		this.fileExtension = fileExtension;
	}
	
	
	@Override
	public Collection<File> getFiles() {
		return FileHelper.listDirectoryFiles( directory, new ExtensionFilenameFilter(), /* recurse */ false );
	}
	
	
	protected class ExtensionFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept( File dir, String name ) { 
			return name.endsWith( fileExtension );
		}
	}

}
