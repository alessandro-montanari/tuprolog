NOTE ABOUT THE USE OF .NET TYPES FROM TUPROLOG.NET
The .NET types can be loaded using Prolog from that assemblies that are
reachable using the standard steps adopted by the CLR to locate
assemblies (http://msdn.microsoft.com/en-us/library/yx7xezcf.aspx).

In this distribution, the assemblies contained in CStudent.dll, VBStudent.dll
and FStudent.dll can be loaded without problems because in the application
configuration file (2p.exe.config) is inserted the "examples\.NET from Prolog"
directory into the privatePath, so the CLR can reach and load them.


NOTE ABOUT THE USE OF JAVA TYPES FROM TUPROLOG.NET
From Prolog is possible to use almost all java types that are present in the OpenJDK
because they are provided by IKVM.NET (rember that those types are implemented in
.NET by IKVM.NET).

For the user types defined in .class or .jar files, the only location that is 
reachable by tuProlog is the root directory of the application.