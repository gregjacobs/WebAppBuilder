WebAppBuilder is a JavaScript and CSS build tool, which can combine (concatenate) and minify JavaScript and CSS files for deployment. It also manages dependency builds, where the dependencies can be stated, and then the rest of the files from a directory or tree of directories can be included automatically without re-including the dependencies.


## History

This project was originally based on Sencha's JSBuilder2 tool (http://extjs.com/products/jsbuilder/). The original JSBuilder2 required that each and every JavaScript and CSS file be placed into the manifest (a .jsb2 file, similar to this project's build.json), and everytime a new file was added, the manifest had to be edited. 

I had originally intended to simply add the feature for managing dependent files with directory includes into JSBuilder2, but after going over the original code, I decided to rewrite it entirely top to bottom. 

Because of this full rewrite, I decided to make a new project entirely which is based on the original idea, but includes complete reorganization, simplification, and added features. It is also much faster than the JSBuilder2 implementation, which did a lot of unnecessary intermediate file operations. The original JSBuilder2 project was also fairly specific for the ExtJS build process, whereas this project aims to be generalized. And because JSBuilder2 seems to be replaced by JSBuilder3 for ExtJS, there is very little chance that the JSBuilder2 line will receive any updates from Sencha, which is another reason for starting a new project. The original source for JSBuilder2 is still included in this project however, under lib/com/extjs/.

The main reason for this project in general though was that I could not find any static build tool that provided dependency management, while also allowing the inclusion of a directory. It seemed you either could include all .js files in a directory (in whichever order the directory listing would provide), or you had to include them each manually, one at a time to be in the correct order. I personally liked the approach that the Ruby project [Sprockets](https://github.com/sstephenson/sprockets) had taken for JavaScript dependency management, using include/require "directives", so I decided to create something similar that didn't need to be served by a Rails project, or a server at all. Just a simple, Java based build tool that could build the files. 

Oh, I also named it "Web App Builder", because it does not only build JavaScript, it builds CSS as well. I also plan on adding support for SASS in the future, probably using JRuby.


## Installation

There is no real "installation" per se; all you need to do is run the WebAppBuilder.jar file from the command line. However, I would recommend copying the WebAppBuilder.jar file into the directory of your project, for both ease of use, and to allow others to build your project if you share it as well.

You will need Java installed though to run the jar. You most likely already have it, but in case you don't, you can get it at [http://java.com/en/download/manual.jsp](http://java.com/en/download/manual.jsp). 

If you get a "command not found" error when you type `java` in the command prompt, you need to set up your PATH variable. Here is a nice blog post about how to do that on Windows: [http://bharatsoft.blogspot.com/2010/08/setting-path-and-classpath-in-javahow.html](http://bharatsoft.blogspot.com/2010/08/setting-path-and-classpath-in-javahow.html). (Note: you shouldn't need to worry about setting the CLASSPATH variable.) After you set the PATH variable, you may need to close and reopen any command prompt windows that you currently have open.


## Usage

Running the jar file with no arguments will search for a build.json in the current directory, and use that as the project file. If there is no build.json in the current directory, it can be specified with the --projectFile argument. Other arguments control different behavior.

Available arguments (all optional):

    --projectFile  -p  Location of the build.json (or other named *.json) project file.
                       Defaults to the build.json file in the current directory
                       
    --outputDir    -o  The directory to output the built files to. Defaults to the current directory

    --debugOnly    -d  If true, only creates a 'debug' build (i.e. concatenated, but not minified)
    --debugSuffix      Suffix to append to JS and CSS 'debug' targets, defaults to '' (empty string)
    --minifyOnly   -m  If true, only creates a 'minified' build (i.e. concatenated and minified)
    --minifySuffix -s  Suffix to append to JS and CSS 'minified' targets, defaults to '.min'

    --verbose      -v  Output detailed information about what is being built
    --help         -h  Prints the help display



### Example Usage:

Windows

    :: assumes build.json present in current directory
    java -jar WebAppBuilder.jar   

    :: specifying project file and output directories
    java -jar WebAppBuilder.jar --projectFile myProject\build.json --outputDir myProject\build\

Linux and OS X

    # assumes build.json present in current directory
    java -jar WebAppBuilder.jar

    # specifying project file and output directories
    java -jar WebAppBuilder.jar --projectFile myProject/build.json --outputDir myProject/build/


To try the overly simple example build, check out this project, and then do:

    :: Windows
    java -jar WebAppBuilder.jar --projectFile example\build.json   

    # Linux / OS X
    java -jar WebAppBuilder.jar --projectFile example/build.json



## The build.json File
The build.json file format is a JSON encoded configuration file which describes
which files should be built.

#### The top-level keys are:

- projectName:  String describing the project
- licenseText:  String specifying the header of all .js and .css, use \n for
                newlines.
- pkgs:         An array of **Package Descriptors**


#### Package Descriptors:

- name:         String describing the package 
- filename:     String specifying the file name to create. Can be a full path
                from the output directory specified on the command line.
                Ex: "build.js"
- includes:     An array of **Include Directives** which need to be included in this
                package.


#### Include Directives:

Can be one of 3 types:

1) File include. Keys:

   - **file**:      A relative path to a particular file that should be included.

2) Directory include, which is a single, non-recursed directory. (For a recursed
directory, use Tree). Keys:

   - **directory**: A relative path to a directory of files that should be included.
                The file extension of the files that are included from the directory 
                will match the package's file extension.

3) Tree include, which is a directory, and all of its subdirectories. Keys:

   - **tree**:      A relative path to a directory of files which itself, and all of its
                subdirectories should be included. The file extension of the files 
                that are included will match the package's file extension.



### Example build.json, which lists a few files that are dependencies before including an entire tree of files:
	{
		projectName: "Example",
		licenseText: "Example Library\nCopyright(c) 2011 Example Creator.\nMIT Licensed. http://www.opensource.org/licenses/mit-license.php",
		pkgs: [
			{
				name: "Example JS",
				filename: "example.js",
				includes: [
					{ file: "example/file2.js" },
					{ file: "example/nested/nested2.js" },
					{ tree: "example/" }
				]
			},

			{
				name: "Example CSS",
				filename: "example.css",
				includes: [
					{ tree: "example/" }
				]
			}
		]
	}

Note that all paths are forward slash (/) separated in the build.json file, regardless of operating system.

To include the files that the build.json file resides in using a directory/tree directive, use `"./"`. For Example:

    { tree: "./" }


WebAppBuilder uses the following libraries
------------------------------------------
YUI Compressor licensed under BSD License
http://developer.yahoo.com/yui/compressor/
http://developer.yahoo.com/yui/license.html

Mozilla's Rhino Project licensed under Mozilla's MPL
http://www.mozilla.org/rhino/
http://www.mozilla.org/MPL/

JArgs licensed under BSD License
http://jargs.sourceforge.net/

JSON in Java licensed under the JSON License
http://www.json.org/java/index.html
http://www.json.org/license.html
