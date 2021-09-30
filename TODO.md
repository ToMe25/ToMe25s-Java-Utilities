# TODO
This file contains a list of potential things that are planned to be added later.
All these things are planned to be added **eventually**, but most aren't high priority so its likely that some will never be implemented.

## Config
Some todos for the config system module.
 * Make the config system able to handle arrays or lists somehow.

## Json
The todos for the json module of this library.
 * Make JsonArray deduplication able to find changed values, instead of just finding removed and added values.
 * Add a equals method to JsonArray that compares the content by order, or one that doesn't, whichever it currently doesn't have.
 * Add an interface called IJsonSerializable<? extends JsonElement> for serializing objects to json. This interface should extend Externalizable.
 * Optimize writeExternal and readExternal by manually handling the value type instead of using writeObject/readObject? This might not be worth it because of incompatibilities and/or the effort of changing alot of the internal structures.
 * Consider making JsonObject an actual custom map implementation based on HashMap or LinkedHashMap storing cloning info(is JsonElement supporting clone, or is Cloneable).
 * Make JsonArray changes method able to handle the same element being in the list twice
 * Add fancy printing(newline after commas and brackets, indenting, space after colons)

## Library Loader
Todos for the library downloading and including module of this library.
 * Add a version json to github containing the version number of the latest release, as well as potentially some other info like what the last changed module was.
 * Add an update checker using the version json from above to check for new releases, ideally extendible to allow other software to use this as well.
 * Make the startup library loading only download libraries if there is a newer version available.
 * Consider using maven for the library downloads. This might also be able to handle version checking.

## Logging
Things to change in the logging module.
 * Clean TracingFormatter Thread name cache, either by deleting the entry that wasn't used for the longest time when it gets too big, or by cleaning up no longer running threads.
 * Cache named loggers?

## General
Todos for the General utility class.
 * Remove the longest unused entries from the CLONE_METHODS cache.

## Junit Tests
Improvemets planned for the junit tests.
 * Add a junit test to test log file writing.

## JMH Benchmarks
Additions for the JMH benchmarks
 * Add Config Benchmarks
 * Split JsonObject/JsonArray serializing and derserializing into different benchmarks.
