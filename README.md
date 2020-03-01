# ToMe25s-Java-Utilities
A collection of common Utilities i use in a lot of Projects, moved into a library to simplify keeping them up to date everywhere.

This Library Currently contains:
 * A simple Json Handler
 * A simple Config Handler
 * A Print Stream That can write to multiple Output Streams
 * A Print Stream That can write to multiple Output Streams and has some logging/tracing capabilities
 * A Version Control class that can give you the currently used version of this library as long as the ToMe25s-Java-Utilities-Version attribute is in the MANIFEST.MF file inside its Jar
 * A simple Jar Extraction tool

To add the git-hooks to your active git hooks just execute git-hooks/post-merge.py(`python git-hooks/post-merge.py`).(they are not tested on Windows, but according to the internet they should work.)

In case you are looking for a build: the ToMe25-Java-Utilities.jar file gets build automatically on commit, so it should always be up to date.
