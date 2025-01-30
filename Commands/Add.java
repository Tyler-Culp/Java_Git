package JavaGit.Commands;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;

import JavaGit.Commands.Status;
import JavaGit.CommitObjects.Blob;
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
        ArrayList<Blob> changedBlobs = this.statusTracker.getChangedFiles();
        if (this.JitDirectory == null) {
            System.out.println("Unable to find .jit folder, have you run init?");
            return false;
        }
        File index = new File(this.JitDirectory.getPath() + "/index");
        try (BufferedWriter outBuffer = new BufferedWriter(new FileWriter(index.getPath(), true));) {
            for (Blob blob : changedBlobs) {
                outBuffer.write(blob.file.getName() + " | " + Long.toString(blob.fileSize) + " | " + blob.hash + "\n"); // add changed blob file to index
                // Get (and possibly create) the top two hash character directory
                File hashFolder = new File(this.JitDirectory.getPath() + "/objects/" + blob.hash.substring(0, 2));
                if (!hashFolder.isDirectory()) hashFolder.mkdirs(); // if top two of hash doesn't have a folder yet, make it
                // Create and open the soon to be compressed file with the bottom 38 of the hash as its name
                File compressedFile = new File(hashFolder.getPath() + "/" + blob.hash.substring(2, blob.hash.length()));
                compressedFile.createNewFile();

                // Doing try block like this *should* close the streams once it ends
                try 
                (
                    FileInputStream fis = new FileInputStream(blob.file);
                    FileOutputStream fos = new FileOutputStream(compressedFile);
                    DeflaterOutputStream dos = new DeflaterOutputStream(fos);
                ) {
                    int data = fis.read();
                    while (data != -1) {
                        dos.write(data);
                        data = fis.read();
                    }
                }
                catch (IOException e) {
                    System.out.println("Error occured when creating compressed file in objects");
                    System.out.println(e);
                    return false;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error occured when trying to write to index file");
            System.out.println(e);
            return false;
        }

        // Two things need to be checked for, if the file needs to be overwritten in index
        // or if it is a completely new object
        return true;
    }
}
