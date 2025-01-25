package Commands;
import java.util.ArrayList;
import java.io.File;

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

    private boolean hasFileChanged(File file) {
        return false;
    }
}
