package org.example.Helpers;
import java.io.File;

public class FindJit {
    private static final int MAX_LEVELS = 10;
    
    public File find(String homeDir) {
        File homeFile = new File(homeDir);

        while (homeFile.exists()) {
            File[] childFiles = homeFile.listFiles();
            for (File file : childFiles) {
                if (file.isDirectory() && file.getName().equals(".jit")) {
                    return file;
                } 
            }
            String[] paths = homeDir.split("/");  // First values is empty
            if (paths.length <= 5) break;
            homeDir = "";
            for (int i = 0; i < paths.length - 1; i++) {
                homeDir = homeDir + paths[i] + "/";
            }
            homeFile = new File(homeDir);
        }
        // System.out.println("Unable to find jit folder");
        return null;
    }
}
