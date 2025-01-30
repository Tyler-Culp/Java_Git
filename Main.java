package JavaGit;

import JavaGit.Helpers.FindJit;
import JavaGit.CommitObjects.*;
import JavaGit.Commands.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        if (args.length <= 0) return; 
        String givenCommand = args[0];
        switch (givenCommand) {
            case "init":
                Init initializer = new Init();
                initializer.createDirStructure();
            case "status":
                Status status = new Status();
                ArrayList<Blob> changedObjs = status.getChangedFiles();
                for (Blob obj : changedObjs) {
                    System.out.println(obj.file.getName());
                }
                break;
            case "add":
                Add adder = new Add();
                boolean success = adder.add();
                if (success) {
                    System.out.println("Successful added Blobs to staging");
                }
                else {
                    System.out.println("Error when adding things to staging");
                }
                break;
            case "FindJit":
                FindJit finder = new FindJit();
                System.out.println("Found .jit in " + finder.find().getName());
            default:
                break;
        }
    }
}