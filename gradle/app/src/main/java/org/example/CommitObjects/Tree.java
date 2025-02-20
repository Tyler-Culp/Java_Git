package org.example.CommitObjects;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import java.util.Scanner;


public class Tree extends AbstractJitObject {
    /*
     * Tree should contain a list of files it refers to and their hashes, i.e.
     * blob main.txt 1234fdca
     * tree temp 5678ffff
     * 
     * When making a Tree object we always start at the root directory
     * If we see a blob then we just find its hash and add it to a string to write to a File
     * If we see a directory then we are in trouble because to get its hash we need to recursively do the same this on it
     */
    public List<AbstractJitObject> children;

    // Only create a tree when a commit is happening
    // Need to recursively go through a bunch of trees before getting to root
    // At the bottom we create a tree object, save its hash and use it to make
    // next level tree object

    // TODO: Figure out this jit folder thing

    private static File jitFolder;
    private static File objectsFolder;
    private static File indexFile;
    private static Map<String, String> stagedFiles = new HashMap<>();
    private int size;

    public Tree() {
        this.fileName = null;
        this.hash = null;
        this.objectString = null;
        this.file = null;
        this.size = 0;
        this.children = new ArrayList<>(); // All blobs will only include hash references
    }

    public Tree(String fileName, String hash) {
        this.fileName = fileName;
        this.hash = hash;
        this.objectString = null;
        this.file = null;
        this.size = 0;
        this.children = new ArrayList<>();
    }

    private void addToChildren(AbstractJitObject obj) {
        this.children.add(obj);
    }

    private void setObjectString(String s) {
        this.objectString = s;
    }

    private void setHash(String hash) {
        this.hash = hash;
    }

    public int getSize() {
        return this.size;
    }

    public static void setJitFolder(File folder) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid jit folder");
        }
        jitFolder = folder;
        objectsFolder = new File(jitFolder, "objects");
        indexFile = new File(jitFolder, "index");
        
        stagedFiles = getIndexFiles(indexFile);
    }


    /**
     * Creates a tree using the index file and previous committed tree (if needed)
     * 
     * @return A tree containing all info needed from root directory
     */
    public static Tree createTree() {
        File headFile = new File(jitFolder.getPath() + "/HEAD");
        assert(headFile.exists());

        Tree hashTree = null;
        Tree indexTree = null;

        indexTree = createTreeFromIndex(new File(jitFolder.getParent()));

        if (headFile.length() == 0) {
            return indexTree;
        }

        try (
            Scanner sc = new Scanner(headFile)
        )
        {
            String prevCommitHash = sc.nextLine().replace("\n", ""); // hacky way to just get the hash without \n
            String prevTreeHash = getTreeHashFromCommit(prevCommitHash);
            hashTree = createTreeFromHash(prevTreeHash);

            return mergeTrees(indexTree, hashTree);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getTreeHashFromCommit(String commitHash) {
        String frontHash = commitHash.substring(0, 2);
        String backHash = commitHash.substring(2);

        File commitFile = new File(jitFolder.getPath() + "/objects/" + frontHash + "/" + backHash);
        assert(commitFile.exists());

        String commitString = AbstractJitObject.readFileFromObjects(commitFile);
        String[] lines = commitString.split("\n");

        assert(lines.length >= 3);

        String treeHash = lines[3];

        return treeHash;
    }

    /**
     * Create hashes for trees recursively, adding tree objects to objects folder as we go.
     * Can be called after createTree() function when making a commit object. Will return root hash
     * to be used in commit object and will add needed trees to objects folder as we go.
     * 
     * TODO: Split this up into a few functions so that one builds the objects string
     * 
     * @param tree - the tree which we want to get a hash for
     * @return - hash of created tree added to objects folder
     */
    public static String getHash(Tree root) {
        StringBuilder treeString = new StringBuilder();
        assert(root.fileName != null);
        treeString.append(root.fileName + "\n");
        assert(root != null);
        List<AbstractJitObject> children = root.children;

        for (AbstractJitObject child : children) {
            if (child instanceof Blob) { // If we are dealing with a blob
                treeString.append("blob" + DELIMINATER + child.hash + DELIMINATER + child.fileName + "\n");
            }
            else if (child instanceof Tree) {
                // Two possibilities, tree already has a hash (previously committed)
                // Or tree has no hash and needs to be added to objects folder
                child.hash = child.hash == null ? getHash((Tree) child) : child.hash;
                treeString.append("tree" + DELIMINATER + child.hash + DELIMINATER + child.fileName + "\n"); 
            }
        }
        root.setObjectString(treeString.toString());
    
        String rootHash = root.hash(root.objectString);
        root.setHash(rootHash);

        root.addToObjectsFolder(objectsFolder, root.objectString);

        return root.hash;
    }

    /**
     * Tree should be created such that each line includes:
     * type|fileName|hash|timeChanged(?)
     * 
     * @param hash - hash of the tree object we need to build from
     * @return a Tree based on most recent commit that includes all the files from that commit
     * that were not staged for change
     */
    public static Tree createTreeFromHash(String hash) {    
        String frontHash = hash.substring(0, 2);
        String backHash = hash.substring(2);

        File toRead = new File(jitFolder.getPath() + "/objects/" + frontHash + "/" + backHash);
        assert(toRead.exists());

        String fileString = AbstractJitObject.readFileFromObjects(toRead);
        String[] lines = fileString.split("\n");

        String treeName = lines[0];

        Tree tree = new Tree(treeName, hash);

        for (int i = 1 ; i < lines.length ; i++) {
            String line = lines[i];
            String data[] = line.split(DELIMINATER_REGEX);
            String type = data[0];
            String currHash = data[1];
            String name = data[2];

            if (type.equals("blob")) {
                if (stagedFiles.containsKey(name)) continue; // No need to add a blob that's been staged
                else {
                    tree.size++;
                    tree.addToChildren(new Blob(name, currHash));
                }
            }
            else if (type.equals("tree")) {
                Tree childTree = createTreeFromHash(currHash);
                if (childTree.children.size() == 0) continue;
                // This is a recurse case, need add a new tree made by this function to curr Tree children
                tree.size += childTree.size + 1;
                tree.addToChildren(childTree);
            }
        }

        return tree;
    }

    /**
     * A recursive function starting from the .jit projects root folder and creating a Tree object along the way.
     * Trees have a List of children (referring to files within the directory heirarchy). These children can be
     * Blobs (normal files which act as leaf nodes) or other Trees. All children in this tree are those which were staged
     * to be committed in the index file.
     * 
     * New trees created from index file also need to be created in objects folder
     * 
     * @param rootFolder - The top level folder to create the tree from
     * @return A Tree object containing references only to folders and files which have been staged
     * 
     */
    public static Tree createTreeFromIndex(File rootFolder) {
        Tree tree = new Tree();
        tree.file = rootFolder;
        tree.fileName = rootFolder.getName();

        if (rootFolder.isFile()) {
            throw new Error("The createTree function must be given a directory as a parameter");
        }
        File[] children = rootFolder.listFiles();
        for (File child : children) {
            // If it's a file and ready for staging then get the blob of it and add it to the tree structure
            if (child.getName().equals(".jit")) continue;
            String fileName = child.getName();
            if (child.isFile() && stagedFiles.containsKey(fileName)) {
                tree.size++;
                tree.addToChildren(new Blob(fileName, stagedFiles.get(fileName)));
            }
            else if (child.isDirectory()) { // If it is a directory then recursively call the create tree method
                Tree childTree = createTreeFromIndex(child);
                if (childTree.children.size() > 0) { // The child tree actually has files to contribute from staging
                    tree.size += childTree.size + 1; // +1 to include the parent folder also tracking it
                    tree.addToChildren(childTree);
                }
            }
        }
        return tree; // The last return should be a Tree with references to all Blobs and subtrees
    }

    /**
     * When creating a new commit the plan is to create tree based on currently staged files
     * and to create a tree based on previous commit (if there is one).
     * This is to ensure that commit history remains stable (i.e. prev committed unchanged
     * files remain in new commit history)
     * 
     * Easily achieved as the two given trees (if done right) will have no overlap with each other
     * 
     * @param tree1 - first tree made of currently staged changes
     * @param tree2 - second tree made of previously committed hash
     * @return
     */
    public static Tree mergeTrees(Tree stagedChanges, Tree fromHash) {
        List<AbstractJitObject> lst = new ArrayList<>();
        lst.addAll(stagedChanges.children);
        lst.addAll(fromHash.children);

        Tree merged = new Tree();
        merged.fileName = "/"; //Set name to be just backslash representing top level dir
        merged.children = lst;
        merged.size = stagedChanges.size + fromHash.size;

        return merged;
    }

    /**
     * 
     * @return a hashmap of staged files, mapping a fileName to its hash
     */
    public static Map<String, String> getIndexFiles(File indexFile) {
        Map<String, String> stagedFiles = new HashMap<>();
        Scanner sc;
        try {
            sc = new Scanner(indexFile);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] lineList = line.split(DELIMINATER_REGEX);
                String fileName = lineList[0];
                String fileHash = lineList[2];

                stagedFiles.put(fileName, fileHash);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stagedFiles;
    }
}
