# ToMe25s-Java-Utilities
A collection of common Utilities i use in a lot of Projects, moved into a library to simplify keeping them up to date everywhere.

![Maven Package](https://github.com/ToMe25/ToMe25s-Java-Utilities/workflows/Maven%20Package/badge.svg)

The ToMe25s-Java-Utilities.jar file should always be the latest build, as it gets build on commit by the git hooks.

### This Library Currently contains:
 * A Json Object
 * A Json Array/List
 * A Json Parser
 * A simple Config Handler
 * A Print Stream that can write to multiple Output Streams
 * A Print Stream that can write to multiple Output Streams and has some logging/tracing capabilities
 * A Print Stream that writes to a Logger
 * A simple Logging Handler
 * A Version Control class that can give you the currently used version of this library as long as the ToMe25s-Java-Utilities-Version attribute is in the MANIFEST.MF file inside its Jar
 * A simple Jar Extraction tool
 * A simple File/Library downloader
 * A tool to add Libraries to the classpath

To add the git-hooks to your active git hooks just execute git-hooks/post-merge.py(`python git-hooks/post-merge.py`).(they are not tested on Windows, but according to the internet they should work.)

### How to add this library to your Jar file:
If you want it to get downloaded on startup: <details><summary>click here to see</summary>

 1. copy the LibraryDownloader class and the LibraryLoader class into your project.(if you copy the sources not the compiled classes you can move them to any package, but they need to all be in the same package)
 2. add something like
 ```java
 LibraryLoader loader = new LibraryLoader(args);
 LibraryDownloader.downloadThis();
 loader.addThisToClasspath();
 ```
 to the start of your main method.
 
Note that you can't import any of this libraries classes in your main class if you do this, or else java will crash on startup,
also the LibraryLoader will probably restart your software once to add the Premain-Class Attribute to the MANIFEST.MF and add a vm argument to the start command.
</details>

If you want it to be packaged into your main jar and get extracted on startup: <details><summary>click here to see</summary>

 1. add the ToMe25s-Java-Utilites jar to your project in a way that gets it added to the finished jar.(e.g. copy it into your src directory)
 2. copy the JarExtractor class and the LibraryLoader class into your project.(if you copy the sources not the compiled classes you can move them to any package, but they need to all be in the same package)
 3. add something like
 ```java
 LibraryLoader loader = new LibraryLoader(args);
 File codeSource = new File(MainClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
 JarExtractor.extractThis(codeSource);
 loader.addThisToClasspath();
 ```
 to the start of your main method.
 
Note that you can't import any of this libraries classes in your main class if you do this, or else java will crash on startup,
also the LibraryLoader will probably restart your software once to add the Premain-Class Attribute to the MANIFEST.MF and add a vm argument to the start command.
</details>

If you want it to first try to download this library on startup,
if that doesn't work extracts it from your jar,
adds it to the classpath,
and sets the System Outputs to TracingMultiPrintStreams
copying all the output to a log file: <details><summary>click here to see</summary>

 1. add the ToMe25s-Java-Utilites jar to your project in a way that gets it added to the finished jar.(e.g. copy it into your src directory)
 2. copy the JarExtractor, LibraryDownloader and LibraryLoader classes to your project.(if you copy the sources not the compiled classes you can move them to any package, but they need to all be in the same package)
 3. add something like
 ```java
 LibraryLoader.init(args, new File("LogFile.log"));
 ```
 to the start of your main method.
 
Note that you can't import any of this libraries classes in your main class if you do this, or else java will crash on startup,
also the LibraryLoader will probably restart your software once to add the Premain-Class Attribute to the MANIFEST.MF and add a vm argument to the start command.
</details>
