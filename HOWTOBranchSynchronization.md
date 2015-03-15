<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/SynchronizationGuide/merge-dialog.png' width='50%' />

# Steps #
  1. Switch to a working copy of the branch in which you want to receive the changes
  1. Commit all the modification in the branch. There must be no pending modification.
  1. Left click on the project -> Team -> Merge
  1. In the Dialog:
    * In the **From:** field enter the full folder URL of the trunk. This depends on the platform (Java, Android, Eclipse or .NET).
    * In the **From Revision** field, enter the revision number of the creation of the branch.
> > For example:
```
Rev Comments
39. Working on MyBranch 
38. Working on trunk 
37. Working on MyBranch 
36. Create branch MyBranch 
35. Working on trunk 
34. Working on trunk 
...
```
> > In this case, if you want to port the changes made to the trunk into the MyBranch branch, you have to select the [revision 36](https://code.google.com/p/tuprolog/source/detail?r=36).
    * Check the option **Use "From:" URL**
    * Check the option **Merge to HEAD revision**
    * Click on **Merge**

At the completion of the merge, in the Eclipse Console View can be seen a summary of the operation with the number of files that have been modified/deleted/added and eventually the number of conflicts. The conflicts are identified by a little icon with two arrows applied to folders and files. To edit the conflicts: right click on the element -> Team -> Edit conflicts, here it is possible to choose which version to keep, the local or the one that comes form the repository.
After the merge, the working copy has some changes to commit but before committing it is important to test the code to verify that it is working as expected.

# Reference #
http://help.collab.net/index.jsp?topic=/org.tigris.subclipse.doc/topics/toc.html