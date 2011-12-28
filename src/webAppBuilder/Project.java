package webAppBuilder;

import java.util.*;
import java.io.*;
import org.json.*;
import webAppBuilder.pkg.Package;

/**
 * Represents a project, loaded from the build.json file.
 */
public class Project {
	
	private String name;
	private String licenseText;
	private List<Package> pkgs = new ArrayList<Package>();
	private BuildOptions buildOptions;
	
	private String licenseHeader;
	
	
	/**
	 * Creates a Project based on a build.json configuration file on the system.
	 * 
	 * @param projectFile
	 * @param buildOptions
	 * @return
	 * @throws Exception 
	 */
	public static Project fromFile( File projectFile, BuildOptions buildOptions ) throws Exception {
		JSONObject projJSON = new JSONObject( FileHelper.getContents( projectFile ) );
		
		String name = projJSON.optString( "projectName" );
		String licenseText = projJSON.optString( "licenseText" );
		List<Package> pkgs = new ArrayList<Package>();
		
		// Create the Project's Packages
		JSONArray pkgsArr = projJSON.getJSONArray( "pkgs" );
		for( int i = 0, len = pkgsArr.length(); i < len; i++ ) {
			Package pkg = Package.fromJSON( pkgsArr.getJSONObject( i ), buildOptions );
			pkgs.add( pkg );
		}
		
		return new Project( name, licenseText, pkgs, buildOptions );
	}
	
	
	/**
	 * Creates a new Project. A Project encapsulates {@link Package Packages}, and {@link Package Packages}
	 * encapsulate {@link Include Include Directives}.
	 * 
	 * @param name
	 * @param licenseText
	 * @param pkgs
	 * @param buildOptions 
	 */
	public Project( String name, String licenseText, List<Package> pkgs, BuildOptions buildOptions ) {
		this.name = name;
		this.licenseText = licenseText;
		this.pkgs = pkgs;
		this.buildOptions = buildOptions;
		
		licenseHeader = createLicenseHeader();
		
		System.out.println( "Loaded project: '" + name + "', with " + pkgs.size() + " package(s)" );
	}
	
	
	/**
	 * Compiles and outputs the project's concatenated and minified files.
	 */
	public void writeOutput() throws IOException {
		// Make the output directory if it doesn't yet exist
		File deployDir = new File( buildOptions.getOutputDir() );
		deployDir.mkdirs();
		
		// Write out the contents of each package
		for( Package pkg : pkgs ) {
			pkg.writeOutput( licenseHeader );
		}
	}
	
	
	/**
	 * Creates the license header from the licenseText provided in the build.json file.
	 * 
	 * @return 
	 */
	private String createLicenseHeader() {
		StringBuilder headerBuilder = new StringBuilder();
		String[] licTextArray = licenseText.split( "\n" );

		headerBuilder.append( "/*!\n" );
		for( String line : licTextArray ) {
			headerBuilder.append( " * " );
			headerBuilder.append( line );
			headerBuilder.append( "\n" );
		}
		headerBuilder.append( " */\n" );
		
		return headerBuilder.toString();
	}
	
}
