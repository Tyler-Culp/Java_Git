package JavaGit.Commands;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import JavaGit.CommitObjects.*;
import JavaGit.Helpers.*;

public class Status {
    private File JitDirectory;
    public Status() {
        FindJit finder = new FindJit();
        this.JitDirectory = finder.find();
    }


    // TODO: Optimization to be done, get all files and hashes first, put into hashmap, and then scan index and object folders and remove unchanged ones
    // should bring amortorized time down by a factor of N.
    public ArrayList<Blob> getChangedFiles() {
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
        File file = new File(homeDir);
        try {
            return getChangedFiles(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param topLevelDir - The top level directory (full path) of the project you want to track
     * @return A list of files which have changed
     * @throws FileNotFoundException 
     * 
     * A recursive function starting at the top level of project it is tracking
     */
    private ArrayList<Blob> getChangedFiles(File topLevelDir) throws FileNotFoundException {
        ArrayList<Blob> changedFiles = new ArrayList<>(); // List to store all the changed files
        File[] childFiles = topLevelDir.listFiles();

        if (childFiles != null && childFiles.length > 0) {
            for (File file : childFiles) {
                // Can check gitignore here maybe to see what should be skipped
                // TODO: Create some object or function or something with a list of names to be ignored
                if (file.getName().equals(".jit") || file.getName().equals(".git") || file.getName().equals("JavaGit")) continue; // don't want to track the actual .jit folder (this would be a loop)
                else if (file.isDirectory()) { // Need to also check if the directory itself is a new things I think, should track with a tree object
                    changedFiles.addAll(getChangedFiles(file));
                }
                else {
                    Blob blob = new Blob(file);
                    if (hasFileChanged(blob)) {
                        changedFiles.add(blob);
                    }
                }
            }
        }
        return changedFiles;
    }

    /*
     * Need to check if file has been tracked yet
     * Need to check if file has been added to index file yet
     * 
     * Check if it is added by looking in the index file and seeing if the file sha and file name is there yet
     * Check if it is tracked by looking at commit sha in HEAD and tracing things backwards
     */
    private boolean hasFileChanged(Blob blob) throws FileNotFoundException {
        // Need to change this because blob should include metadata including file length I think
        String hash = blob.hash;

        boolean hasChangedInObjects = hasFileChangedInObjects(hash);

        if (!hasChangedInObjects) return false;

        return hasFileChangedInIndex(blob);
    }

    private boolean hasFileChangedInIndex(Blob blob) {
        /*
         * Index file need to have form line
         * fileName | size | hash | timestamp(?)
         */
        if (this.JitDirectory == null) {
            System.out.println("Can not find jit folder to check index file");
            return false;
        }
        File index = new File(this.JitDirectory.getPath() + "/index");
        Scanner sc;
        try {
            sc = new Scanner(index);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains(blob.file.getName())) { // currently tracking in index file
                    sc.close();
                    if (line.contains(blob.hash)) { // Hash is same so file is unchanged
                        return false;
                    }
                    else { // Hash is different so file is different
                        return true;
                    }
                }
            }
            sc.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasFileChangedInObjects(String hash) {
        if (this.JitDirectory == null) {
            System.out.println("Can not find jit folder to check objects folder");
            return false;
        }
        File objectFolder = new File(this.JitDirectory.getPath() + "/objects");
        String topOfHash = hash.substring(0, 2);
        String bottomOfHash = hash.substring(2);

        File[] objectFolderFiles = objectFolder.listFiles();

        // Need to check if file hash matches any we currently are tracking in objects
        // Because of the way git tracks object with front two part of hash being parent folder means nested loop is needed
        // Ends up actually only being O(n) I'm pretty sure though since the number of top two hash digits is constant
        for (File objectFile : objectFolderFiles) {
            if (objectFile.getName().startsWith(topOfHash)) { // Found a folder with same top level hash
                File[] nestedObjectFiles = objectFile.listFiles();
                for (File nestedFile : nestedObjectFiles) {
                    if (nestedFile.getName().contains(bottomOfHash)) { // Found the same exact hash meaning file has not changed
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public static void main(String[] args) {
        Status status = new Status();
        ArrayList<Blob> changedFiles = status.getChangedFiles();

        System.out.println("Untracked changes in:");
        for (Blob changedFile : changedFiles) {
            System.out.println(changedFile.file.getName());
        }
    }
}
