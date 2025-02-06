package org.example.Commands;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;

import org.example.Commands.Status;
import org.example.CommitObjects.Blob;
import org.example.Helpers.*;

public class Add {
    private Status statusTracker;
    private File JitDirectory;
    public Add() {
        this.statusTracker = new Status();
        FindJit finder = new FindJit();
        this.JitDirectory = finder.find();
    }

    // Will try to implement this later, bit of an issue because .jit directory needs to be located
    public boolean add(String fileName) { // Get users current path and try to add file from there
        String homeDir = System.getProperty("user.dir"); // This is the directory user is running the commands from
        // TODO: Possible edge case to look into for if user gives a fileName like "../../../.." that goes outside .jit folder
        File fileToAdd = new File(homeDir + "/" + fileName);
        return add(fileToAdd);
    }

    /*
     * Recursive function for adding files. If given a directory traverse the changed child files
     * If it is not a directory then just add the file (if it has actually been changed)
     */
    private boolean add(File file) {
        ArrayList<Blob> changedObjs = this.statusTracker.getChangedFiles(file);
        File indexFile = new File(this.JitDirectory.getPath() + "/index");
        File objectsFolder = new File(this.JitDirectory.getPath() + "/objects");
        for (Blob obj : changedObjs) {
            if (!obj.addToIndex(indexFile)) System.out.println("Couldn't add " + obj.file.getName() + " to index file");
            if (!obj.addToObjectsFolder(objectsFolder, obj.objectString)) System.out.println("Couldn't add " + obj.file.getName() + " to objects folder");
        }
        return true;
    }


    // Make one add which just adds all files with changes to staging to just make this easy for now
    public boolean add() {
        /*
         * Index file need to have form line
         * fileName | size | hash | timestamp(?)
         */
        File homeDir = new File(System.getProperty("user.dir"));
        ArrayList<Blob> changedObjs = this.statusTracker.getChangedFiles(homeDir);
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
