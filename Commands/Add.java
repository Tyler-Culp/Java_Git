package JavaGit.Commands;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;

import JavaGit.Commands.Status;
import JavaGit.CommitObjects.Blob;
import JavaGit.CommitObjects.CommitObject;
import JavaGit.Helpers.*;

public class Add {
    private Status statusTracker;
    private File JitDirectory;
    public Add() {
        this.statusTracker = new Status();
        FindJit finder = new FindJit();
        this.JitDirectory = finder.find();
    }

    // Will try to implement this later, bit of an issue because .jit directory needs to be located
    private boolean add(String fileName) { // Get users current path and try to add file from there
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
        File fileToAdd = new File(homeDir + "/" + fileName);
        Blob blob = new Blob(fileToAdd);
        return false;
    }


    // Make one add which just adds all files with changes to staging to just make this easy for now
    public boolean add() {
        /*
         * Index file need to have form line
         * fileName | size | hash | timestamp(?)
         */
        ArrayList<Blob> changedObjs = this.statusTracker.getChangedFiles();
        if (this.JitDirectory == null) {
            System.out.println("Unable to find .jit folder, have you run init?");
            return false;
        }
        File indexFile = new File(this.JitDirectory.getPath() + "/index");
        File objectsFolder = new File(this.JitDirectory.getPath() + "/objects");

        for (Blob obj : changedObjs) {
            if (!obj.addToIndex(indexFile)) System.out.println("Couldn't add " + obj.file.getName() + " to index file");
            if (!obj.addToObjectsFolder(objectsFolder, obj.objectString)) System.out.println("Couldn't add " + obj.file.getName() + " to objects folder");
        }
        return true;
    }
}
