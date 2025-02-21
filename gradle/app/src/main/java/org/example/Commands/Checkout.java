package org.example.Commands;

import java.io.File;
import java.io.FileWriter;
import java.sql.Blob;
import java.util.List;

import org.example.CommitObjects.AbstractJitObject;
import org.example.CommitObjects.CommitObject;
import org.example.CommitObjects.Tree;

public class Checkout {
    private File jitFolder;
    private String commitRootHash;
    private String commitObjString;
    private Tree commitRoot;

    /**
     * Creates a checkout object that contains a reference to the Tree object (commitRoot) which is
     * referenced in the actual commit object which is obtained from commitRootHash
     * 
     * @param jitFolder
     * @param commitRootHash
     */
    public Checkout(File jitFolder, String commitRootHash) {
        this.jitFolder = jitFolder;
        this.commitRootHash = commitRootHash;
        this.commitObjString = AbstractJitObject.readFileFromObjects(this.commitRootHash, this.jitFolder);
        String commitTreeHash = getTreeHash(commitObjString);
        this.commitRoot = Tree.createTreeFromHash(commitTreeHash);
    }

    /**
     * Recursively creates the entire directory structure necessary for a checkout.
     * 
     * Currently it just creates a copy folder in root dir with recreated files
     * 
     * Does this (eventually) by:
     * 1. Building a folder inside of the root folder of the project which contains all files that were tracked when commit happened
     * 2. Removing all files (not named .jit) from the root folder 
     * 3. Turning the copy folder into the root folder
     * 
     * @param hashTree - This is the tree representation of what the tracked project looked like at the time of snapshotting this commit
     * @param currFolder - This is the current folder that we are building up. Should always be a directory
     * @return - Currently will return the copy folder for testing purposes
     */
    private File createDir(Tree hashTree, File currFolder) {
        assert(currFolder.exists() && currFolder.isDirectory());
        List<AbstractJitObject> treeChildren = hashTree.children;
        for (AbstractJitObject child : treeChildren) {
            String fileName = child.fileName;
            File rebuildingFile = new File(currFolder.getPath() + "/" + fileName);
            if (child instanceof Blob) {
                try {
                    rebuildingFile.createNewFile();
                    try (FileWriter fw = new FileWriter(rebuildingFile)) {
                        // Extra step to read from objects, pretty sure I can just skip first line of objectString
                        // fw.write(AbstractJitObject.readFileFromObjects(child.hash, jitFolder)); 
                        String blobStringWithoutFirstLine = child.objectString.substring(child.objectString.indexOf('\n') + 1);
                        fw.write(blobStringWithoutFirstLine);

                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (child instanceof Tree) {
                rebuildingFile.mkdir();
                createDir((Tree) child, rebuildingFile); // Should just recursively build the thing now
            }
        }
        return currFolder;
    }

    private String getTreeHash(String commitObjString) {
        String[] lines = commitObjString.split("\n");
        return lines[CommitObject.commitTreeHashIndex];
    }
}
