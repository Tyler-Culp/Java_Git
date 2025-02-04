package JavaGit.Commands;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import JavaGit.CommitObjects.*;
import JavaGit.Helpers.*;

public class Status {
    private File JitDirectory;
    private File indexFile;
    private File objectsFolder;
    public Status() {
        FindJit finder = new FindJit();
        this.JitDirectory = finder.find();
        if (this.JitDirectory.exists()) {
            String jitPath = this.JitDirectory.getPath();
            this.indexFile = new File(jitPath + "/index");
            this.objectsFolder = new File(jitPath + "/objects");
        }
    }
    public void printChangedFiles(ArrayList<Blob> files) {
        if (files.size() <= 0) {
            System.out.println("Clean working directory");
            return;
        }
        String currWorkingDir = System.getProperty("user.dir");
        int currWorkDirLength = currWorkingDir.length() + 1; // Add 1 to remove leading / in dir structure
        System.out.println("Changed files:");
        for (Blob blob : files) {
            String filePath = blob.file.getPath().substring(currWorkDirLength, blob.file.getPath().length());
            System.out.println(filePath);
        }
    }
    // TODO: Optimization to be done, get all files and hashes first, put into hashmap, and then scan index and object folders and remove unchanged ones
    // should bring amortorized time down by a factor of N.
    public ArrayList<Blob> getChangedFiles() {
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
        File file = new File(homeDir);
        return getChangedFiles(file);
    }

    /**
     * @param currFile - The top level directory (full path) of the project you want to track
     * @return A list of files which have changed
     * @throws FileNotFoundException 
     * 
     * A recursive function starting at the top level of project it is tracking
     * TODO: Need to check for things like deleted files as well and remove them from index if they are deleted
     */
    public ArrayList<Blob> getChangedFiles(File currFile) {
        // TODO: Create some object or function or something with a list of names to be ignored
        if (currFile.getName().equals(".jit") || currFile.getName().equals(".git") || currFile.getName().equals("JavaGit")) return new ArrayList<Blob>();
        ArrayList<Blob> changedFiles = new ArrayList<>(); // List to store all the changed files
        if (currFile.isDirectory()) {
            File[] childFiles = currFile.listFiles();
            for (File child : childFiles) {
                changedFiles.addAll(getChangedFiles(child));
            }
        }
        else {
            Blob blob = new Blob(currFile);
            try {
                if (hasFileChanged(blob)) {
                    changedFiles.add(blob);
                }
            }
            catch (FileNotFoundException e) {
                System.out.println("File not found exception for " + blob.file.getName());
                System.out.println(e);
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
        if (!this.indexFile.exists()) {
            System.out.println("Can not find index file");
            return false;
        }
        Scanner sc;
        try {
            sc = new Scanner(this.indexFile);
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
        if (!this.objectsFolder.exists()) {
            System.out.println("Can not find objects folder");
            return false;
        }
        String topOfHash = hash.substring(0, 2);
        String bottomOfHash = hash.substring(2);

        File[] objectFolderFiles = this.objectsFolder.listFiles();

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
