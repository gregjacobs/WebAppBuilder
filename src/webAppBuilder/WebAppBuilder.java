package webAppBuilder;

import java.io.*;
import jargs.gnu.CmdLineParser;


public class WebAppBuilder {
	private static String version = "1.0.0";

	private static File projectFile;
	private static BuildOptions buildOptions;

	public static void main( String[] args ) throws Exception {
		if( parseArgs( args ) == true ) {
			Project project = Project.fromFile( projectFile, buildOptions );
			project.writeOutput();
		}
	}


	/**
	 * Prints the WebAppBuilder's command line usage.
	 */
	private static void printUsage() {
		System.out.println( "WebAppBuilder version " + version );
		System.out.println( "https://github.com/gregjacobs/WebAppBuilder\n" );

		System.out.println( "WebAppBuilder is a JavaScript and CSS project build tool. It combines and minifies JS and CSS files.\n" );
		System.out.println( "Running the jar file with no arguments will search for a build.json in the current directory, and use that as the project file.\n" );

		System.out.println( "Available arguments (all optional):" );
		System.out.println( "    --projectFile -p   Location of the build.json (or other named *.json)" );
		System.out.println( "                       project file. Defaults to the build.json file in the" );
		System.out.println( "                       current directory\n" );
		System.out.println( "    --outputDir -o     The directory to build the project to. Defaults to the" );
		System.out.println( "                       current directory\n" );

		System.out.println( "    --debugOnly -d     If true, only creates a 'debug' build (i.e. concatenated" );
		System.out.println( "                       but not minified)\n" );
		System.out.println( "    --debugSuffix      Suffix to append to JS and CSS 'debug' targets, defaults" );
		System.out.println( "                       to '' (empty string)\n" );
		System.out.println( "    --minifyOnly -m    If true, only creates a 'minified' build (i.e." );
		System.out.println( "                       concatenated and minified)\n" );
		System.out.println( "    --minifySuffix -s  Suffix to append to JS and CSS 'minified' targets," );
		System.out.println( "                       defaults to '.min'\n" );

		System.out.println( "    --verbose -v       Output detailed information about what is being built" );
		System.out.println( "    --help -h          Prints this help display" );

		System.out.println( "\nExample Usage:" );
		System.out.println( "Windows:" );
		System.out.println( "    java -jar WebAppBuilder.jar -p myProject\\build.json -o build\\" );
		System.out.println( "Linux and OS X:" );
		System.out.println( "    java -jar WebAppBuilder.jar -p myProject/build.json -o build/" );
	}


	/**
	 * Parses the arguments provided to the command line, and creates a {@link Config} object out of them.
	 *
	 * @param args
	 * @return
	 */
	private static boolean parseArgs( String[] args ) {
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option projectFileOpt = parser.addStringOption( 'p', "projectFile" );
		CmdLineParser.Option outputDirOpt = parser.addStringOption( 'o', "outputDir" );
		CmdLineParser.Option verboseOpt = parser.addBooleanOption( 'v', "verbose" );
		CmdLineParser.Option debugOnlyOpt = parser.addBooleanOption( 'd', "debugOnly" );
		CmdLineParser.Option debugSuffixOpt = parser.addBooleanOption( "debugSuffix" );
		CmdLineParser.Option minifyOnlyOpt = parser.addBooleanOption( 'm', "minifyOnly" );
		CmdLineParser.Option minifySuffixOpt = parser.addStringOption( 's', "minifySuffix" );
		CmdLineParser.Option helpOpt = parser.addBooleanOption( 'h', "help" );

		try {
			parser.parse( args );
		}
		catch ( CmdLineParser.OptionException e ) {
			System.err.println( e.getMessage() );
			System.exit( 2 );
		}

		// if help, print the usage, and don't proceed
		Boolean help = (Boolean) parser.getOptionValue( helpOpt, false );
		if( help ) {
			printUsage();
			return false;
		}

		String projectFilename = (String) parser.getOptionValue( projectFileOpt, "build.json" );
		projectFile = new File( projectFilename );
		if( !projectFile.exists() ) {
			System.err.format( "The project file %s was not found. Use the --help switch for more information.\n", projectFile.getAbsolutePath() );
			return false;
		}

		buildOptions = new BuildOptions();
		buildOptions.setBuildFileDir( projectFile.getParent() );
		buildOptions.setOutputDir( (String) parser.getOptionValue( outputDirOpt, System.getProperty( "user.dir" ) ) );
		buildOptions.setVerbose( (Boolean) parser.getOptionValue( verboseOpt, false ) );
		buildOptions.setDebugOnly( (Boolean) parser.getOptionValue( debugOnlyOpt, false ) );
		buildOptions.setDebugSuffix( (String) parser.getOptionValue( debugSuffixOpt, "" ) );
		buildOptions.setMinifyOnly( (Boolean) parser.getOptionValue( minifyOnlyOpt, false ) );
		buildOptions.setMinifySuffix( (String) parser.getOptionValue( minifySuffixOpt, ".min" ) );

		return true;
	}

}