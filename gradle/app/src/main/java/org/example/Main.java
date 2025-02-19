package org.example;

import org.example.Commands.*;
import org.example.CommitObjects.*;
import org.example.Helpers.*;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        if (args.length <= 0) return; 

        String userDir = System.getProperty("user.dir");

        File jitFolder = new FindJit().find(userDir);
        File indexFile = new File(jitFolder.getPath() + "/index");
        File objectsFolder = new File(jitFolder.getPath() + "/objects");

        String givenCommand = args[0];

        switch (givenCommand) {
            case "init":
                Init initializer = new Init(userDir);
                initializer.createDirStructure();
                break;
            case "status":
                Status status = new Status(jitFolder);
                ArrayList<Blob> changedObjs = status.getChangedFiles();
                status.printChangedFiles(changedObjs);
                break;
            case "add":
                Add adder = new Add(jitFolder);
                boolean success;
                if (args.length == 2) {
                    String fileName = args[1];
                    success = adder.add(fileName);
                }
                else {
                    success = adder.add();
                }
                if (success) {
                    System.out.println("Successful added Blobs to staging");
                }
                else {
                    System.out.println("Error when adding things to staging");
                }
                break;
            case "FindJit":
                FindJit finder = new FindJit();
                System.out.println("Found .jit in " + finder.find(userDir).getName());
                break;
            default:
                System.out.println("Command line options:");
                System.out.println("init - Create a new jit directory in current working directory");
                System.out.println("status - Check file changes in working directory");
                System.out.println("add - Add changed files to staging");
                break;
        }
    }
}