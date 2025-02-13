package org.example.Commands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import org.example.CommitObjects.*;
import org.example.Helpers.*;

public class Status {
    private File jitFolder;
    private File indexFile;
    private File objectsFolder;
    private Map<String, String> indexFileChanges = getIndexChanges();
    public Status(File jitFolder) {
        this.jitFolder = jitFolder;
        if (this.jitFolder.exists()) {
            this.indexFile = new File(jitFolder.getPath() + "/index");
            this.objectsFolder = new File(jitFolder.getPath() + "/objects");
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
        // TODO: Consider running it from jit directory location instead
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

    private Map<String, String> getIndexChanges() {
        Map<String, String> fileNameToHash = new HashMap<>(); //Hash map to track most recent changes in index file
        try (
            Scanner sc = new Scanner(this.indexFile);
        ) {
            String line = sc.nextLine();
            String[] data = line.split("\\|");
            String fileName = data[0];
            String fileHash = data[2];

            fileNameToHash.put(fileName, fileHash);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return fileNameToHash;
    }
}


// package org.example.Commands;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Scanner;
// import java.io.File;
// import java.io.FileNotFoundException;

// import org.example.CommitObjects.*;

// public class Status {
//     private File jitFolder;
//     private File indexFile;
//     private File objectsFolder;
// //     private Map<String, String> indexFileChanges = getIndexChanges();
//         public Status(File jitFolder) {
//             this.jitFolder = jitFolder;
//             if (this.jitFolder.exists()) {
//                 this.indexFile = new File(jitFolder.getPath() + "/index");
//                 this.objectsFolder = new File(jitFolder.getPath() + "/objects");
//             }
//         }
//         public void printChangedFiles(ArrayList<Blob> files) {
//             if (files.size() <= 0) {
//                 System.out.println("Clean working directory");
//                 return;
//             }
//             String currWorkingDir = System.getProperty("user.dir");
//             int currWorkDirLength = currWorkingDir.length() + 1; // Add 1 to remove leading / in dir structure
//             System.out.println("Changed files:");
//             for (Blob blob : files) {
//                 String filePath = blob.file.getPath().substring(currWorkDirLength, blob.file.getPath().length());
//                 System.out.println(filePath);
//             }
//         }
//         // TODO: Optimization to be done, get all files and hashes first, put into hashmap, and then scan index and object folders and remove unchanged ones
//         // should bring amortorized time down by a factor of N.
//         public ArrayList<Blob> getChangedFiles() {
//             String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
//             // TODO: Consider running it from jit directory location instead
//             File file = new File(homeDir);
//             return getChangedFiles(file);
//         }
    
//         /**
//          * @param currFile - The top level directory (full path) of the project you want to track
//          * @return A list of files which have changed
//          * @throws FileNotFoundException 
//          * 
//          * A recursive function starting at the top level of project it is tracking
//          * TODO: There is a problem where if they stage a file for commit, change it and commit, and then revert and commit the revert won't be seen
//          * TODO: Need to check for things like deleted files as well and remove them from index if they are deleted
//          */
//         public ArrayList<Blob> getChangedFiles(File currFile) {
//             // TODO: Create some object or function or something with a list of names to be ignored
//             if (currFile.getName().equals(".jit") || currFile.getName().equals(".git") || currFile.getName().equals("JavaGit")) return null;
//             ArrayList<Blob> changedFiles = new ArrayList<>(); // List to store all the changed files
//             if (currFile.isDirectory()) {
//                 File[] childFiles = currFile.listFiles();
//                 for (File child : childFiles) {
//                     changedFiles.addAll(getChangedFiles(child));
//                 }
//             }
//             else {
//                 Blob blob = new Blob(currFile);
//                 if (hasFileChanged(blob)) {
//                     changedFiles.add(blob);
//                 }
//             }
//             return changedFiles;
//         }
    
//         /**
//          * Need to check if changes are most up to date in index file
//          * If not need to also check if hash already exists in object
//          * 
//          * If hash already exists in objects but not most updated index file then only the index file should be update
//          * If hash is in both index and objects already the file has not been changed yet
//          * 
//          * Check if it is added by looking in the index file and seeing if the file sha and file name is there yet
//          * Check if it is tracked by looking at commit sha in HEAD and tracing things backwards
//          * 
//          * @param blob - The file to check if it has changed
//          * @return true if the file has been changed
//          */
//         private boolean hasFileChanged(Blob blob) {
//             // TODO: Redundant adds don't work currently because the hash file in objects is the first thing checked
    
//             // First check if file already has changed in index to account for redundant changes
//             // If most recent file change does not match this blobs hash, then it has changed
    
//             // If there are no changes to report in index then check if file is already tracked in objects
//             // if not then it is a NEW file to be added 
//             // this is probably where I can start adding things to status like if it's a changed or new file 

//             // I think this function may have to change to include prev committed trees because otherwise it will be impossible
//             // to know if a commited file in object folder belongs to your branch or not

//             // Case: Two programmers commit same hash object (possibly different file names). Currently the second person
//             // to commit will not have their file included in commit history because when we check if blob has changed in
//             // objects we see it has not and thus do not mark the file as changed and do not add it to index file
    
//             String hash = blob.hash;

//             boolean hasChangedInObjects = hasFileChangedInObjects(hash);

//             if (!hasChangedInObjects) return false;

//             return hasFileChangedInIndex(blob);

//             /*
//              * Truth Table
//              * chO   |chI
//              * ------|-------
//              *  0     | 0   => has not changed in either ==> file in objects and index is the same
//              *  1     | 0   => Impossible case, can not only change in objects because to be added to index implies new object created
//              *  0     | 1   => Reverted to a previously added object
//              *  1     | 1   => Does not exist in objects or in index
//              */
//         }

//         private boolean hasFileChangedInIndex(Blob blob) {
//             /*
//              * Index file need to have form line
//              * fileName | size | hash | timestamp(?)
//              */
//             if (!this.indexFile.exists()) {
//                 System.out.println("Can not find index file");
//                 return false;
//             }
//             Scanner sc;
//             try {
//                 sc = new Scanner(this.indexFile);
//                 while (sc.hasNextLine()) {
//                     String line = sc.nextLine();
//                     if (line.contains(blob.file.getName())) { // currently tracking in index file
//                         sc.close();
//                         if (line.contains(blob.hash)) { // Hash is same so file is unchanged
//                             return false;
//                         }
//                         else { // Hash is different so file is different
//                             return true;
//                         }
//                     }
//                 }
//                 sc.close();
//                 return true;
//             } catch (FileNotFoundException e) {
//                 e.printStackTrace();
//                 return false;
//             }
//         }
    
//         /**
//          * This function has an issue, breaks early if it sees the same hash found
//          * Function should
//          * 
//          * @param blob - The blob (file) we are interested in whether it has changed or not
//          * @return try if the blob (file) has chaned
//          */
//     //     private boolean hasFileChangedInIndex(Blob blob) {
//     //         /*
//     //          * Index file need to have form line
//     //          * fileName | size | hash | timestamp(?)
//     //          */
//     //         if (!this.indexFile.exists()) {
//     //             System.out.println("Can not find index file");
//     //             return false;
//     //         }

//     //         // if map has fileName and hash return false (file has not changed in index)
//     //         return !this.indexFileChanges.get(blob.fileName).equals(blob.hash);
//     //     }
    
//     //     private Map<String, String> getIndexChanges() {
//     //     Map<String, String> fileNameToHash = new HashMap<>(); //Hash map to track most recent changes in index file
//     //     try (
//     //         Scanner sc = new Scanner(this.indexFile);
//     //     ) {
//     //         String line = sc.nextLine();
//     //         String[] data = line.split("\\|");
//     //         String fileName = data[0];
//     //         String fileHash = data[2];

//     //         fileNameToHash.put(fileName, fileHash);
//     //     }
//     //     catch (Exception e) {
//     //         e.printStackTrace();
//     //     }
//     //     return fileNameToHash;
//     // }

//     private boolean hasFileChangedInObjects(String hash) {
//         if (!this.objectsFolder.exists()) {
//             System.out.println("Can not find objects folder");
//             return false;
//         }
//         String topOfHash = hash.substring(0, 2);
//         String bottomOfHash = hash.substring(2);

//         File[] objectFolderFiles = this.objectsFolder.listFiles();

//         // Need to check if file hash matches any we currently are tracking in objects
//         // Because of the way git tracks object with front two part of hash being parent folder means nested loop is needed
//         // Ends up actually only being O(n) I'm pretty sure though since the number of top two hash digits is constant
//         for (File objectFile : objectFolderFiles) {
//             if (objectFile.getName().startsWith(topOfHash)) { // Found a folder with same top level hash
//                 File[] nestedObjectFiles = objectFile.listFiles();
//                 for (File nestedFile : nestedObjectFiles) {
//                     if (nestedFile.getName().contains(bottomOfHash)) { // Found the same exact hash meaning file has not changed
//                         return false;
//                     }
//                 }
//             }
//         }
//         return true;
//     }
// }
