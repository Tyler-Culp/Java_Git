package org.example.CommitObjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.example.Helpers.*;
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
    static File jitFolder = new FindJit().find(System.getProperty("user.dir"));
    static File objectsFolder = new File(jitFolder.getPath() + "/objects");
    static File indexFile = new File(jitFolder.getPath() + "index");
    static Map<String, String> stagedFiles = getIndexFiles();
    public Tree(File file) {
        this.file = file;
        this.children = new ArrayList<>();
        // File objectsFolder = new File(new FindJit().find().getPath() + "/objects");
        // this.hash = createTreeHash(file, objectsFolder);
    }

    /*
     * Tree should be created such that each line includes:
     * type|fileName|fileSize|hash|timeChanged(?)
     */
    public static Tree createTree(String hash) {
        return new Tree(new File(""));
    }

    /**
     * A recursive function starting from the .jit projects root folder and creating a Tree object along the way.
     * Trees have a List of children (referring to files within the directory heirarchy). These children can be
     * Blobs (normal files which act as leaf nodes) or other Trees. All children in this tree are those which were staged
     * to be committed in the index file.
     * 
     * TODO: Need to be able to merge two tree objects so that newly created trees have the same unchanged references as the prev committed Tree
     * 
     * @param rootFolder - The top level folder to create the tree from
     * @return A Tree object containing references only to folders and files which have been staged
     * 
     */
    public static Tree createTree(File rootFolder) {
        Tree tree = new Tree(rootFolder);
        if (rootFolder.isFile()) {
            throw new Error("The createTree function must be given a directory as a parameter");
        }
        File[] children = rootFolder.listFiles();
        for (File child : children) {
            // If it's a file and ready for staging then get the blob of it and add it to the tree structure
            if (child.isFile() && stagedFiles.containsKey(child.getName())) {
                tree.children.add(new Blob(child));
            }
            else { // If it is a directory then recursively call the create tree method
                Tree childTree = createTree(child);
                if (childTree.children.size() > 0) { // The child tree actually has files to contribute from staging
                    tree.children.add(childTree);
                }
            }
        }
        return tree; // The last return should be a Tree with references to all Blobs and subtrees
    }

    /*
     * Return a hash map of files staged
     */
    private static Map<String, String> getIndexFiles() {
        Map<String, String> stagedFiles = new HashMap<>();
        Scanner sc;
        try {
            sc = new Scanner(indexFile);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] lineList = line.split(" | ");
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

    // Refactoring this so that I create a tree object, and then get the hash based on that
    public String getRootHash(File jitFolder, File objectsFolder) {
        String jitFolderPath = jitFolder.getPath();
        int LENGTH_OF_WORD_JIT = 4;
        File rootFolder = new File(jitFolderPath.substring(0, jitFolderPath.length() - LENGTH_OF_WORD_JIT)); // IDK if this is gonna work tbh
        return createTreeHash(rootFolder, objectsFolder);
    }

    private String createTreeHash(File file, File objectsFolder)  {
        if (file.isFile()) {
            Blob blob = new Blob(file);
            return "blob " + file.getName() + " " + blob.hash + "\n";
        }
        else if (file.isDirectory()) {
            File[] children = file.listFiles();
            String treeString = "";
            for (File child : children) {
                if (child.isFile()) {
                    Blob blob = new Blob(child);
                    treeString = treeString + "blob " + file.getName() + " " + blob.hash + "\n";
                }
                else {
                    treeString = treeString + "tree" + file.getName() + " " + createTreeHash(child, objectsFolder) + "\n";
                }
            }

            addToObjectsFolder(objectsFolder, treeString); // Need to add this tree to objects folder so it gets tracked

            return hash(hash); // return the hash up the next level for the next tree up to use (if it needs it)
        }
        else {
            throw new Error("Somehow ran into a non file or directory in createTreeHash");
        }
    }
}
