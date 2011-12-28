package webAppBuilder.include;

import java.util.*;
import java.io.*;
import org.json.*;

/**
 * Represents an include directive that the user can provide.
 */
public abstract class Include {
	
	/**
	 * Factory method to create the correct {@link Include} directive class for the given
	 * JSON data.
	 * 
	 * @param includeJSON The JSON representation of the given include.
	 * @param fileExtension The file extension for the package output file, which determines the file
	 *   filter for the {@link Include}.
	 * @return The proper {@link Include} subclass, or null if one was not found for the given JSON.
	 * @throws FileNotFoundException 
	 */
	public static Include fromJSON( JSONObject includeJSON, String fileExtension ) throws JSONException {
		if( includeJSON.has( "file" ) ) {
			return new FileInclude( includeJSON.getString( "file" ) );
			
		} else if( includeJSON.has( "directory" ) ) {
			return new DirectoryInclude( includeJSON.getString( "directory" ), fileExtension );
			
		} else if( includeJSON.has( "tree" ) ) {
			return new TreeInclude( includeJSON.getString( "tree" ), fileExtension );
			
		} else {
			return null;
		}
	}
	
	
	/**
	 * Retrieves the list of files for the include directive.
	 * 
	 * @return 
	 */
	public abstract Collection<File> getFiles();
	
}
