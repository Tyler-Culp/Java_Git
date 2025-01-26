package JavaGit.Commands;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import JavaGit.CommitObjects.Blob;

public class Status {
    // TODO: Optimization to be done, get all files and hashes first, put into hashmap, and then scan index and object folders and remove unchanged ones
    // should bring amortorized time down by a factor of N.
    public ArrayList<File> getChangedFiles() {
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
        File file = new File(homeDir);
        try {
            return getChangedFiles(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
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
    private ArrayList<File> getChangedFiles(File topLevelDir) throws FileNotFoundException {
        ArrayList<File> changedFiles = new ArrayList<File>(); // List to store all the changed files
        File[] childFiles = topLevelDir.listFiles();

        if (childFiles != null && childFiles.length > 0) {
            for (File file : childFiles) {
                if (file.isDirectory()) {
                    changedFiles.addAll(getChangedFiles(file));
                }
                else if (hasFileChanged(file)) {
                    changedFiles.add(file);
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
    private boolean hasFileChanged(File file) throws FileNotFoundException {
        Blob toCheck = new Blob(file);
        String hash = toCheck.hash();

        boolean hasChangedInObjects = hasFileChangedInObjects(file, hash);

        if (!hasChangedInObjects) return false;

        return hasFileChangedInIndex(file, hash);
    }

    private boolean hasFileChangedInIndex(File file, String hash) {
        /*
         * Index file need to have form line
         * fileName | size | hash | timestamp(?)
         */
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
        File index = new File(homeDir + "/.jit/index");
        Scanner sc;
        try {
            sc = new Scanner(index);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains(file.getName())) { // currently tracking in index file
                    sc.close();
                    if (line.contains(hash)) { // Hash is same so file is unchanged
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

    private boolean hasFileChangedInObjects(File file, String hash) {
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
        File objectFolder = new File(homeDir + "/.jit/objects");
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
        ArrayList<File> changedFiles = status.getChangedFiles();

        System.out.println("Untracked changes in:");
        for (File changedFile : changedFiles) {
            System.out.println(changedFile.getName());
        }
    }
}
