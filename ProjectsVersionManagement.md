# Introduction #

Below are detailed the procedures that have to be followed in order to modify the version number of a project.

The numbering scheme adopted is aimed at providing a clearer cross-platform view of the version being used.

The first two digits represent the engine version. These two digits are used to mark changes in the engine both visible from the outside (i.e. new predicates, new libraries, bug fixes, etc.) and not visible from the outside (i.e. internal refactoring, better architecture design, etc.). As long as you see the same two digits on different platforms, you can count that the inner behavior of the engine will be the same, too.

The subsequent digits (usually, just the third one, possibly followed by some build indication) is platform specific and will be
used to distinguish between different versions of platform-specific items (such as IDEs, plug-in IDEs, Android UIs, etc.)

# Java and .NET #
Modify the values of the private fields (`ENGINE_VERSION`, `JAVA_SPECIFIC_VERSION` and `NET_SPECIFIC_VERSION`) inside the class [alice.util.VersionInfo](http://code.google.com/p/tuprolog/source/browse/2p/trunk/src/alice/util/VersionInfo.java).

---


# Plugin Eclipse #
http://code.google.com/p/tuprolog/wiki/GuidaPubblicazioneNuoveVersioniPluginEclipse

---


# Android #
Modify the value of the attributes `android:versionCode` and `android:versionName` inside the file [AndroidManifest.xml](http://code.google.com/p/tuprolog/source/browse/2p-android/trunk/AndroidManifest.xml)

The versionName must be the complete xx.yy.nn version number, and the versionCode is instead obtained by concatenating xx+yy+nn.

Note: in this way the specific number for the Adroid platform ("nn" part in the number) is independent from the Java platform and so it can be changed without modify and recompile the tuProlog engine.

---
