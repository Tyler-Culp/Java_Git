package org.example.Helpers;
import java.io.File;

public class FindJit {
    private static final int MAX_LEVELS = 10;
    public File find() {
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
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
        System.out.println("Unable to find jit folder");
        return null;
    }

    
    public static void main(String[] args) {
        FindJit finder = new FindJit();
        File jitFolder = finder.find();
        if (jitFolder.isDirectory()) {
            System.out.println("jit folder found");
            System.out.println(jitFolder.getPath());
        }
        else {
            System.out.println("jit folder not found");
        }
    }
}
