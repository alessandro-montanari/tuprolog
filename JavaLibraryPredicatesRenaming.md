# Predicates renaming #
In a future release of tuProlog, there will be naming changes in the predicates of the [JavaLibrary](http://code.google.com/p/tuprolog/source/browse/2p/trunk/src/alice/tuprolog/lib/JavaLibrary.java).
The purpose of these changes is to obtain a uniform access to the Object Oriented world from the Prolog side independently of the language that is being used (Java, C#, F# and VB.NET).

The changes are listed below:
| **Old Name** | **New Name** | **Notes** |
|:-------------|:-------------|:----------|
| java\_object | new\_object |  |
| java\_object\_bt | new\_object\_bt |  |
| java\_call | method\_call | This predicate has the new name in the .NET version of the OOLibrary but it still has the old name in the Java version of the OOLibrary |
| java\_array\_set | array\_set |  |
| java\_array\_get | array\_get |  |
| java\_array\_lenght | array\_length |  |
| java\_array\_get\_primitive | array\_get\_primitive | This predicate has to be renamed in the .NET and Java version of the OOLibrary |
| java\_array\_set\_primitive | array\_set\_primitive | This predicate has to be renamed in the .NET and Java version of the OOLibrary |
| java\_catch | java\_catch | This predicate is not yet available on the .NET platform |
| java\_class | new\_class | This predicate is not yet available on the .NET platform |
| set\_classpath | set\_classpath|  |
| get\_classpath | get\_classpath|  |
| register | register|  |
| unregister | unregister|  |

# Private methods Renaming #
These methods are used internally and are not available for the user on the prolog side.
| **Old Name** | **New Name** | **Notes** |
|:-------------|:-------------|:----------|
| java\_set/java\_get | property\_get/property\_set | This method has to be renamed in the .NET and Java version of the OOLibrary |
| java\_array | create\_array | This method has to be renamed in the .NET and Java version of the OOLibrary |

Note: after the renaming, the old names will be kept for backward compatibility for at least one version of tuProlog.