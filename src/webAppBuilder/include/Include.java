package webAppBuilder.include;

import java.util.*;
import java.io.*;
import org.json.*;
import webAppBuilder.BuildOptions;
import webAppBuilder.pkg.Package;

/**
 * Represents an include directive that the user can provide.
 */
public abstract class Include {
	
	protected Package pkg;  // the Package that this Include belongs to

	/**
	 * Factory method to create the correct {@link Include} directive class for the given
	 * JSON data.
	 *
	 * @param pkg The Package that this Include belongs to.
	 * @param includeJSON The JSON representation of the given include.
	 * @param buildOptions The options for the build.
	 * @param fileExtension The file extension for the package output file, which determines the file
	 *   filter for the {@link Include}.
	 * @return The proper {@link Include} subclass, or null if one was not found for the given JSON.
	 * @throws FileNotFoundException
	 */
	public static Include fromJSON( Package pkg, JSONObject includeJSON, BuildOptions buildOptions, String fileExtension ) throws JSONException {
		if( includeJSON.has( "file" ) ) {
			return new FileInclude( pkg, includeJSON.getString( "file" ), buildOptions );

		} else if( includeJSON.has( "directory" ) ) {
			return new DirectoryInclude( pkg, includeJSON.getString( "directory" ), buildOptions, fileExtension );

		} else if( includeJSON.has( "tree" ) ) {
			return new TreeInclude( pkg, includeJSON.getString( "tree" ), buildOptions, fileExtension );

		} else {
			return null;
		}
	}


	/**
	 * Constructs an Include directive.
	 *
	 * @param pkg The parent Package of this Include directive.
	 */
	public Include( Package pkg ) {
		this.pkg = pkg;
	}


	/**
	 * Retrieves the list of files for the include directive.
	 *
	 * @return The Collection of file(s) that the Include directive represents.
	 */
	public abstract Collection<File> getFiles();

}
