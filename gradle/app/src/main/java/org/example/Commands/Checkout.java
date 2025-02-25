package org.example.Commands;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.example.CommitObjects.AbstractJitObject;
import org.example.CommitObjects.Blob;
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

        if (this.commitObjString == null) throw new Error("commit hash does not exist");
        String objectType = this.commitObjString.split("\n")[CommitObject.commitHeader];
        if (!objectType.equals("commit")) throw new Error("hash given was not a commit"); 

        String commitTreeHash = getTreeHash(commitObjString);
        Tree.setJitFolder(jitFolder); // This is needed for testing, in the actual application the main function just needs to do this
        this.commitRoot = Tree.createTreeFromHash(commitTreeHash);
    }

    public File checkout() {
        assert(clearHomeDir(jitFolder.getParentFile()));
        return createDir(this.commitRoot, this.jitFolder.getParentFile());
    }

    public File checkoutCopy() {
        File copyDir = new File(this.jitFolder.getParentFile().getPath() + "/copy");
        copyDir.mkdir();
        File builtCopy = createDir(this.commitRoot, copyDir);
        return builtCopy;
    }

    /**
     * Preps the home directory for checkout. Everything is deleted to ensure the folder is returned
     * to the same state it was in when the commit that is being checked out was committed.
     * 
     * Slightly scary to do this to be honest, alternatively could build a copy of the folder to checkout first
     * and then replace main folder with that one.
     * 
     * @param currFolder - The current folder that we need to recursively "clear" or delete
     * @return boolean indicating whether everything was cleaned out correctly
     */
    private boolean clearHomeDir(File currFolder) {
        if (!currFolder.isDirectory() || !currFolder.getName().equals(".jit")) return false;
        boolean success = true;
        File[] children = currFolder.listFiles();
        for (File child : children) {
            if (child.isFile()) {
                success &= child.delete();
            }
            else if (child.isDirectory()) {
                success &= clearHomeDir(child);
            }
        }
        return success;
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
                        String fileContent = AbstractJitObject.readFileFromObjects(child.hash, jitFolder);
                        int endOfFirstLine = fileContent.indexOf("\n");
                        fileContent = fileContent.substring(endOfFirstLine + 1, fileContent.length() - 1); // I think something is adding an extra new line to the end
                        fw.write(fileContent); 
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
