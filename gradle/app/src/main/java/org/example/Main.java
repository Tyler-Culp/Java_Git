package org.example;

import org.example.Commands.*;
import org.example.CommitObjects.*;
import org.example.Helpers.*;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        if (args.length <= 0) printHelp(); 

        String userDir = System.getProperty("user.dir");

        File jitFolder = new FindJit().find(userDir);
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
            case "commit":
                if (args.length >= 2) {
                    String message = args[1];
                    Commit commit = new Commit(jitFolder, message);

                    if (commit.commit()) {
                        System.out.println("Commit successful");
                    }
                    else {
                        System.out.println("Commit failed");
                    }
                }
                else {
                    System.out.println("Must include a message for the commit");
                }
                break;
            case "FindJit":
                FindJit finder = new FindJit();
                System.out.println("Found .jit in " + finder.find(userDir).getName());
                break;
            default:
                printHelp();
        }
    
    }
    private static void printHelp() {
        System.out.println("Command line options:");
        System.out.println("init - Create a new jit directory in current working directory");
        System.out.println("status - Check file changes in working directory");
        System.out.println("add - Add changed files to staging");
        System.out.println("commit - Commit staged changes");
        System.out.println("help - view options");
    }
}