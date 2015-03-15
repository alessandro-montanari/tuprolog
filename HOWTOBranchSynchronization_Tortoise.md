# Steps #
1. Switch to a working copy of the branch in which you want to receive the changes.

2. Commit all the modification in the branch. There must be no pending modification.

3. Right-click on the directory containing the branch and then select TortoiseSVN -> Merge.

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/SynchronizationGuide/tortoise1.png' width='50%' />

4. Select: "Merge a range of revisions".

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/SynchronizationGuide/tortoise2.png' width='50%' />

5. Then specify the url of the trunk with which you want to synchronize your branch and also, if you want, the range of revisions as shown in figure.

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/SynchronizationGuide/tortoise3.png' width='50%' />

6. Finally click on "Merge".

<img src='http://tuprolog.googlecode.com/svn/wiki/screenshots/SynchronizationGuide/tortoise4.png' width='50%' />

After the merge, the working copy has some changes to commit but before committing it is important to test the code to verify that it is working as expected.


Courtesy of Matteo Librenti.