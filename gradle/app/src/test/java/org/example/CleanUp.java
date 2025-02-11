package org.example;

import java.io.File;

public class CleanUp {
    public static boolean cleanUpJit(File file) {
        File[] children = file.listFiles();
        boolean success = true;
        for (File child : children) {
            if (child.isDirectory()) {
                success &= cleanUpJit(child);
            }
            success &= child.delete();
        }
        return file.delete() && success;
    }
}
