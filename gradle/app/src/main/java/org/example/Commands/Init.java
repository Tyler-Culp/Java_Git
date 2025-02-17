package org.example.Commands;

import java.io.File;
import java.io.IOException;

import org.example.Helpers.*;

/**
 * To create the necessary folder system necessary for a git command tool
 */

public class Init {
    String homeDir;
    File homeDirFile;

    public Init(String homeDir) {
        this.homeDir = homeDir;
        this.homeDirFile = new File(homeDir);
    }

    public boolean createDirStructure() {
        if (!this.homeDirFile.exists()) {
            System.out.println("Error: home directory init called from did not exist (some kind of bug)");
            return false;
        }

        File[] childrenFiles = this.homeDirFile.listFiles();
        for (File child : childrenFiles) {
            if (child.getName() == ".jit") {
                System.out.println("This project alread has a jit folder");
                return false;
            }
        }
        String currDir = this.homeDir;
        File hidderDir = new File(currDir + "/.jit");
        
        boolean cretedHidden = hidderDir.mkdir();
        if (!cretedHidden) {
            System.out.println("Failed to create .jit");
            return false;
        }
        
        System.out.println("Created .jit");

        currDir = currDir + "/.jit";

        File indexFile = new File(currDir + "/index");

        File headFile = new File(currDir + "/HEAD");
        File objectsDir = new File(currDir + "/objects");
        File refsDir = new File(currDir + "/refs");

        boolean createdIndex = false;
        boolean createdHead = false;
        try {
            createdIndex = indexFile.createNewFile();
            createdHead = headFile.createNewFile();
            if (createdIndex) {
                System.out.println("index file created.");
            }
            else {
                System.out.println("index file failed to create.");
            }
            if (createdHead) {
                System.out.println("HEAD file created.");
            }
            else {
                System.out.println("HEAD file failed to create.");
            }
        } catch (IOException e) {
            System.out.println("index file failed to be created due to an IO Exception");
            System.out.println(e);
        }
        boolean createdObjects = objectsDir.mkdir();
        boolean createdRefs = refsDir.mkdir();

        if (createdObjects) {
            System.out.println("objects directory created.");
        }
        else {
            System.out.println("Failed to create objects directory.");
        }

        if (createdRefs) {
            System.out.println("refs directory created.");
        }
        else{
            System.out.println("Failed to create refs directory.");
        }
        return createdIndex && createdHead && createdObjects && createdRefs;
    }
}