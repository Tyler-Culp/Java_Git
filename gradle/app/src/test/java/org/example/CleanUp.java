package org.example;

import java.io.File;

public class CleanUp {
    public static boolean cleanFolder(File file) {
        File[] children = file.listFiles();
        boolean success = true;
        for (File child : children) {
            if (child.isDirectory()) {
                success &= cleanFolder(child);
            }
            success &= child.delete();
        }
        return success;
    }
}
