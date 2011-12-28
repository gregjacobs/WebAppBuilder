package com.extjs;
import jargs.gnu.CmdLineParser;
import java.io.File;
import java.io.*;
import java.io.FilenameFilter;
import java.util.regex.*;
import java.util.*;
import org.json.*;
import java.util.ArrayList;
import com.yahoo.platform.yui.compressor.*;
import org.mozilla.javascript.*;

public class JSBuilder2 {
    private static String version = "2.0.0";
    private static ArrayList<File> outputFiles = new ArrayList<File>();
    
    private static String homeDir;
    private static String projectFile;
    private static String debugSuffix;
    
    
    private static JSONObject projCfg;
    private static JSONArray pkgs;
    
    private static Boolean verbose;
    
    private static File deployDir;
    
    private static File headerFile;    
    
    private static String projectHome;
    
    public static void main(String[] args) {
        if (parseArgs(args) != true) {
            printUsage();
        } else {
            openProjectFile(projectFile);
            createTempHeaderFile();
            loadPackages();
            mkDeployDir();
            createTargetsWithFileIncludes();
            createTargetsWithDeps();
            copyResources();
            writeHeadersToTargets();
            compressOutputFiles();
        }
    }
    
    private static void printUsage() {        
        System.out.println("JSBuilder version " + version);
        System.out.println("Ext JS, LLC.");
        System.out.println("\nAvailable arguments:");
        System.out.println("    --projectFile -p   (REQUIRED) Location of a jsb2 project file");
        System.out.println("    --homeDir -d       (REQUIRED) Home directory to build the project to");
        System.out.println("    --verbose -v       (OPTIONAL) Output detailed information about what is being built");
        System.out.println("    --debugSuffix -s   (OPTIONAL) Suffix to append to JS debug targets, defaults to \'debug\'");
        System.out.println("    --help -h          (OPTIONAL) Prints this help display.");        
        
        System.out.println("\nExample Usage:");
        System.out.println("Windows:");
        System.out.println("java -jar JSBuilder2.jar --projectFile C:\\Apps\\www\\ext3svn\\ext.jsb2 --homeDir C:\\Apps\\www\\deploy\\");
        System.out.println("Linux and OS X:");
        System.out.println("java -jar JSBuilder2.jar --projectFile /home/aaron/www/trunk/ext.jsb2 --homeDir /home/aaron/www/deploy/");
        System.out.println("\nJSBuilder2 is a JavaScript and CSS project build tool.");
        System.out.println("For additional information, see http://extjs.com/products/jsbuilder/");
    }
    
    private static boolean parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option projectFileOpt = parser.addStringOption('p', "projectFile");
        CmdLineParser.Option homeDirOpt = parser.addStringOption('d', "homeDir");
        CmdLineParser.Option verboseOpt = parser.addBooleanOption('v', "verbose");
        CmdLineParser.Option helpOpt = parser.addBooleanOption('h', "help");        
        CmdLineParser.Option debugSuffixOpt = parser.addStringOption('s', "debugSuffix");        

        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

        homeDir = (String)parser.getOptionValue(homeDirOpt, "");
        projectFile = (String)parser.getOptionValue(projectFileOpt, "");
        debugSuffix = (String)parser.getOptionValue(debugSuffixOpt, "-debug");
        verbose = (Boolean)parser.getOptionValue(verboseOpt, false);
        Boolean help = (Boolean)parser.getOptionValue(helpOpt, false);
        // if help dont proceed
        if (help) {
            return false;
        }
        if (homeDir == "") {
            System.err.println("The --homeDir or -d argument is required and was not included in the commandline arguments.");
        }
        if (projectFile == "") {
            System.err.println("The --projectFile or -p argument is required and was not included in the commandline arguments.");
        }
        if (homeDir == "" || projectFile == "") {
            return false;
        } else {
            return true;
        }
        
    }
    
    private static void openProjectFile(String projectFileName) {
        try {
            File inputFile = new File(projectFileName);
            projectHome = inputFile.getParent();
    
            /* read the file into a string */
            String s = FileHelper.readFileToString(inputFile);
    
            /* create json obj from string */
            projCfg = new JSONObject(s);
            System.out.format("Loading the '%s' Project%n", projCfg.get("projectName"));            
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Failed to open project file.");
        }
    }
    
    private static void loadPackages() {
        try {
            pkgs = projCfg.getJSONArray("pkgs");            
            System.out.format("Loaded %d Packages%n", pkgs.length());            
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Failed to find \'pkgs\' configuration.");            
        }
    }
    
    private static void createTempHeaderFile() {
        try {
            StringBuilder headerBuilder = new StringBuilder();
            headerFile = File.createTempFile("header",".hd");
            headerFile.deleteOnExit();
            String licText = projCfg.getString("licenseText");
            String[] licTextArray = licText.split("\n");
            headerBuilder.append("/*!\n");
            for (int i = 0, licTextLn = licTextArray.length; i < licTextLn; i++) {                
                headerBuilder.append(" * " + licTextArray[i] + "\n");
            }
            headerBuilder.append(" */\n");            
            FileHelper.writeStringToFile(headerBuilder.toString(), headerFile, false);        
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create temporary header file.");
        }
    }
    
    private static void mkDeployDir() {
        try {
            deployDir = new File(homeDir + File.separatorChar + projCfg.getString("deployDir"));
            deployDir.mkdirs();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Failed to create deploy directory.");            
        }        
    }
    
    private static void createTargetsWithFileIncludes() {
        try {
            int len = pkgs.length();
            /* loop over packages for fileIncludes */
            for (int i = 0; i < len; i++) {
                /* Build pkg and include file deps */
                JSONObject pkg = pkgs.getJSONObject(i);	
                /* if we don't include dependencies, it must be fileIncludes */
                if (!pkg.optBoolean("includeDeps", false)) {
                    String targFileName = pkg.getString("file");                    
                    if (targFileName.indexOf(".js") != -1) {
                        targFileName = FileHelper.insertFileSuffix(pkg.getString("file"), debugSuffix);
                    }
                    if (verbose) {
                        System.out.format("Building the '%s' package as '%s'%n", pkg.getString("name"), targFileName);    
                    }
                
                    /* create file and write out header */						
                    File targetFile = new File(deployDir.getCanonicalPath() + File.separatorChar + targFileName);
                    outputFiles.add(targetFile);
                    targetFile.getParentFile().mkdirs();
                    FileHelper.writeStringToFile("", targetFile, false);

                    /* get necessary file includes for this specific package */
                    JSONArray fileIncludes = pkg.getJSONArray("fileIncludes");
                    int fileIncludesLen = fileIncludes.length();
                    if (verbose) {
                        System.out.format("- There are %d file include(s).%n", fileIncludesLen);    
                    }                    

                    /* loop over file includes */
                    for (int j = 0; j < fileIncludesLen; j++) {
                        /* open each file, read into string and append to target */
                        JSONObject fileCfg = fileIncludes.getJSONObject(j);

                        String subFileName = projectHome + File.separatorChar + fileCfg.getString("path") + fileCfg.getString("text");
                        if (verbose) {
                            System.out.format("- - %s%s%n", fileCfg.getString("path"), fileCfg.getString("text"));    
                        }                        
                        File subFile = new File(subFileName); 
                        String tempString = FileHelper.readFileToString(subFile);
                        FileHelper.writeStringToFile(tempString, targetFile, true);
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Failed to create targets with fileIncludes.");
        }
    }
    
    private static void createTargetsWithDeps() {
        try {
            int len = pkgs.length();
            for (int i = 0; i < len; i++) {
                /* Build pkg and include file deps */
                JSONObject pkg = pkgs.getJSONObject(i);	
                /* if we need to includeDeps, they shoudl already be built. */
                if (pkg.optBoolean("includeDeps", false)) {
                    String targFileName = pkg.getString("file");
                    if (targFileName.indexOf(".js") != -1) {
                        targFileName = FileHelper.insertFileSuffix(pkg.getString("file"), debugSuffix);
                    }
                    if (verbose) {
                        System.out.format("Building the '%s' package as '%s'%n", pkg.getString("name"), targFileName);
                        System.out.println("This package is built by included dependencies.");                        
                    }

                    /* create file and write out header */						
                    File targetFile = new File(deployDir.getCanonicalPath() + File.separatorChar + targFileName);
                    outputFiles.add(targetFile);
                    targetFile.getParentFile().mkdirs();
                    FileHelper.writeStringToFile("", targetFile, false);

                    /* get necessary pkg includes for this specific package */
                    JSONArray pkgDeps = pkg.getJSONArray("pkgDeps");
                    int pkgDepsLen = pkgDeps.length();
                    if (verbose) {
                        System.out.format("- There are %d package include(s).%n", pkgDepsLen);    
                    }
                    
                    /* loop over file includes */
                    for (int j = 0; j < pkgDepsLen; j++) {
                        /* open each file, read into string and append to target */
                        String pkgDep = pkgDeps.getString(j);
                        if (verbose) {
                            System.out.format("- - %s%n", pkgDep);
                        }
                        String nameWithorWithoutSuffix = pkgDep;
                        if (pkgDep.indexOf(".js") != -1) {
                            nameWithorWithoutSuffix = FileHelper.insertFileSuffix(pkgDep, debugSuffix);
                        }

                        String subFileName = deployDir.getCanonicalPath() + File.separatorChar + nameWithorWithoutSuffix;
                        File subFile = new File(subFileName); 
                        String tempString = FileHelper.readFileToString(subFile);
                        FileHelper.writeStringToFile(tempString, targetFile, true);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create target with package dependencies.");
        }
    }
    
    public static void writeHeadersToTargets() {
        Collection<File> outFiles = FileHelper.listFiles(deployDir, new FilenameFilter() {
            private Pattern pattern = Pattern.compile(".*[\\.js|\\.css]");
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches() && !(new File(dir.getAbsolutePath() + File.separatorChar + name).isDirectory());
            }                            
        }, true);

        for (File f : outFiles) {
            String headerContents = FileHelper.readFileToString(headerFile);
            String codeContents = FileHelper.readFileToString(f);
            FileHelper.writeStringToFile(headerContents, f, false);
            FileHelper.writeStringToFile(codeContents, f, true);
        }
    }
    
    public static void compressOutputFiles() {
        Reader in = null;
        Writer out = null;
        System.out.println("Compressing output files...");
        for (File f : outputFiles) {
            try {
                if (f.getName().indexOf(".js") != -1) {
                    if (verbose) {
                        System.out.println("- - " + f.getName() + " -> " + f.getName().replace(debugSuffix, ""));
                    }
                    in = new InputStreamReader(new FileInputStream(f));                
                    JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {
        
                        public void warning(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                            if (line < 0) {
                                System.err.println("\n[WARNING] " + message);
                            } else {
                                System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
                            }
                        }
        
                        public void error(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                            if (line < 0) {
                                System.err.println("\n[ERROR] " + message);
                            } else {
                                System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
                            }
                        }
        
                        public EvaluatorException runtimeError(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                            error(message, sourceName, line, lineSource, lineOffset);
                            return new EvaluatorException(message);
                        }
                    });
        
                    // Close the input stream first, and then open the output stream,
                    // in case the output file should override the input file.
                    in.close(); in = null;
        
                    out = new OutputStreamWriter(new FileOutputStream(f.getAbsolutePath().replace(debugSuffix, "")));
                    
        
                    boolean munge = true;
                    boolean preserveAllSemiColons = false;
                    boolean disableOptimizations = false;
                    int linebreakpos = -1;
                    
                    compressor.compress(out, linebreakpos, munge, false, preserveAllSemiColons, disableOptimizations);
                }
            } catch (EvaluatorException e) {
    
                e.printStackTrace();
                // Return a special error code used specifically by the web front-end.
                System.exit(2);
    
            } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);

        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
            

        }        
    }
    
    public static void copyResources() {
        try {
            JSONArray resources = projCfg.getJSONArray("resources");
            int resourceLen = resources.length();
            
            for (int z = 0; z < resourceLen; z++) {
                JSONObject resourceCfg = resources.getJSONObject(z);
                String filters = resourceCfg.getString("filters");
                File srcDir = new File(projectHome + File.separatorChar + resourceCfg.getString("src"));
                File destDir = new File(deployDir.getCanonicalPath() + File.separatorChar + resourceCfg.getString("dest"));
                FileHelper.copyDirectory(srcDir, destDir, filters);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}