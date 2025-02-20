package org.example.Commands;

import java.io.File;

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
     * Does this by:
     * 1. Building a folder inside of the root folder of the project which contains all files that were tracked when commit happened
     * 2. Removing all files (not named .jit) from the root folder 
     * 3. Turning the copy folder into the root folder
     * 
     * @param hashTree - This is the tree representation of what the tracked project looked like at the time of snapshotting this commit
     * @return - Currently will return the copy folder for testing purposes
     */
    private File createDir(Tree hashTree) {
        return new File("");
    }

    private String getTreeHash(String commitObjString) {
        String[] lines = commitObjString.split("\n");
        return lines[CommitObject.commitTreeHashIndex];
    }
}
