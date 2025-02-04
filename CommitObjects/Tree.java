package JavaGit.CommitObjects;

import java.io.File;
import JavaGit.Helpers.*;
public class Tree extends CommitObject {
    /*
     * Tree should contain a list of files it refers to and their hashes, i.e.
     * blob main.txt 1234fdca
     * tree temp 5678ffff
     * 
     * When making a Tree object we always start at the root directory
     * If we see a blob then we just find its hash and add it to a string to write to a File
     * If we see a directory then we are in trouble because to get its hash we need to recursively do the same this on it
     */
    public File jitFolder;
    public File objectsFolder;

    // Only create a tree when a commit is happening
    // Need to recursively go through a bunch of trees before getting to root
    // At the bottom we create a tree object, save its hash and use it to make
    // next level tree object
    public Tree(File file) {
        this.file = file;
        this.jitFolder = new FindJit().find();
        this.objectsFolder = new File(this.jitFolder.getPath() + "/objects");
        this.hash = createTreeHash(file);
    }

    public String getRootHash() {
        String jitFolderPath = this.jitFolder.getPath();
        int LENGTH_OF_WORD_JIT = 4;
        File rootFolder = new File(jitFolderPath.substring(0, jitFolderPath.length() - LENGTH_OF_WORD_JIT)); // IDK if this is gonna work tbh
        return createTreeHash(rootFolder);
    }

    private String createTreeHash(File file)  {
        // String fileText = "tree " + Long.toString(fileSize) + "\n";
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
                    treeString = treeString + "tree" + file.getName() + " " + createTreeHash(child) + "\n";
                }
            }

            addToObjectsFolder(this.objectsFolder, treeString); // Need to add this tree to objects folder so it gets tracked

            return hash(hash); // return the hash up the next level for the next tree up to use (if it needs it)
        }
        else {
            System.out.println("Somehow ran into a non file or directory in createTreeHash");
            return "-1";
        }
    }
}
