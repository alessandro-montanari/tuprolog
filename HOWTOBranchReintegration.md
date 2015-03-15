<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/SynchronizationGuide/reintegration-dialog.png' width='50%' />

# Steps #
  1. Switch to a working copy of the trunk
  1. Commit all the modification in the trunk. There must be no pending modification.
  1. Left click on the project -> Team -> Merge
  1. In the Dialog:
    * In the **From:** field enter the full folder URL of the trunk. This depends on the platform (Java, Android, Eclipse or .NET).
    * Check the option **Merge from HEAD revision**
    * Uncheck the option **Use "From:" URL**
      * In the filed right below enter the full folder URL of the branch
    * Check the option **Merge to HEAD revision**
    * Click on **Merge**

At the completion of the merge, in the Eclipse Console View can be seen a summary of the operation with the number of files that have been modified/deleted/added and eventually the number of conflicts. The conflicts are identified by a little icon with two arrows applied to folders and files. To edit the conflicts: right click on the element -> Team -> Edit conflicts, here it is possible to choose which version to keep, the local or the one that comes form the repository.
After the merge, the working copy has some changes to commit but before committing it is important to test the code to verify that it is working as expected.
# Reference #
http://help.collab.net/index.jsp?topic=/org.tigris.subclipse.doc/topics/toc.html