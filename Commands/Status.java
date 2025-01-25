package Commands;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

import CommitObjects.Blob;

public class Status {
    /**
     * @param topLevelDir - The top level directory (full path) of the project you want to track
     * @return A list of files which have changed
     */
    public ArrayList<File> getChangedFiles(File topLevelDir){
        ArrayList<File> changedFiles = new ArrayList<File>();
        File[] childFiles = topLevelDir.listFiles();
        if (childFiles != null && childFiles.length > 0) {
            for (File file : childFiles) {
                if (file.isDirectory()) {
                    changedFiles.addAll(getChangedFiles(file));
                }
                else {
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
        File index = new File("../.jit/index");
        File objectFolder = new File("../.jit/objects");

        // TODO: Reformat this to call something like checkIndex() and checkObjects() only
        // if there is nothing in index or object folder than this is a fresh repo

        if (index.length() == 0 && objectFolder.listFiles().length == 0) {
            return true;
        }

        Blob toCheck = new Blob(file);
        String hash = toCheck.hash();

        // TODO: Check if the hash matches anything in index or in the objectFolder

        return false;
    }
}
