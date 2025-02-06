package org.example.Commands;

import java.io.File;
import java.io.IOException;

import org.example.Helpers.*;

/**
 * To create the necessary folder system necessary for a git command tool
 */

public class Init {
    String homeDir = System.getProperty("user.dir");

    public void createDirStructure() {
        File jitFolder = new FindJit().find();

        if (jitFolder.exists()) {
            System.out.println("This project alread has a jit folder");
            return;
        }
        String currDir = this.homeDir;
        File hidderDir = new File(currDir + "/.jit");
        
        boolean cretedHidden = hidderDir.mkdir();
        if (!cretedHidden) {
            System.out.println("Failed to create .jit");
            return;
        }
        
        System.out.println("Created .jit");

        currDir = currDir + "/.jit";

        File indexFile = new File(currDir + "/index");

        File headFile = new File(currDir + "/HEAD");
        File objectsDir = new File(currDir + "/objects");
        File refsDir = new File(currDir + "/refs");

        try {
            boolean createdIndex = indexFile.createNewFile();
            if (createdIndex) {
                System.out.println("index file created.");
            }
            else {
                System.out.println("index file failed to create.");
            }
            boolean createdHead = headFile.createNewFile();
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
    }
    public static void main(String[] args) {
        Init init = new Init();
        init.createDirStructure();   
    }
}