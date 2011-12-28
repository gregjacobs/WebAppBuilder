package webAppBuilder.include;

import java.util.*;
import java.io.*;

/**
 * Represents a single file include.
 */
public class FileInclude extends Include {
	
	private File file;
	
	public FileInclude( String file ) {
		this.file = new File( file );
	}
	
	
	@Override
	public Collection<File> getFiles() {
		List<File> list = new LinkedList<File>();
		list.add( file );
		
		return list;
	}
	
}
