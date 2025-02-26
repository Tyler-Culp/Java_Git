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

        if (!jitFolder.exists() && !givenCommand.equals("init")) {
            System.out.println("It seems your working dir is not a jit directory");
            System.out.println("Please run init first before other jit commands");
            return;
        }

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
            case "log":
                if (args.length < 2) {
                    System.out.println("Need to give a number of previous commits to log");
                }
                try {
                    int numberOfPrevCommitsToLog = Integer.parseInt(args[1]);
                    Log logger = new Log(jitFolder);

                    logger.getLastNCommits(numberOfPrevCommitsToLog);
                }
                catch (NumberFormatException e) {
                    System.out.println("Second arument must be an integer");
                }
                break;
            case "checkout":
                if (args.length < 2) {
                    System.out.println("Need to give a commit hash to check out");
                }
                String commitHash = args[1];
                try {
                    Checkout chkout = new Checkout(jitFolder, commitHash);
                    chkout.checkout();
                }
                catch (Exception e) {
                    System.out.println(e);
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
        System.out.println("commit <mesg>- Commit staged changes");
        System.out.println("log <n> - Prints out a log of the last n commits");
        System.out.println("checkout <hash> - Returns folder to state it was in what commit being checked out was committed. Hash given must be full hash of the commit");
        System.out.println("help - view options");
    }
}