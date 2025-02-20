package org.example.CommitObjects;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public abstract class AbstractJitObject {
    public File file;
    public String fileName;
    public String objectString;
    public String hash;

    final static String DELIMINATER = "|";
    final static String DELIMINATER_REGEX = "\\|";
    String hash(String toHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // Turn the string into an array of bytes
            byte[] byteDigest = md.digest(toHash.getBytes());
            
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, byteDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 40 digits long
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Important: Objects hash variable must be set before calling this function (TODO: Make it so you just include the has when calling function)
     * Creates a new folder and file based on the first 2 and last 38 characters of Objects hash respectively. These uses a deflater stream
     * to compress objectString and write it to new file in objects folder
     * 
     * @param objectsFolder - Reference to .jit's objects folder
     * @param objectString - The string which we will be compressing and adding to the objects folder
     * @return true if file was successfully created and written to, false otherwise
     */
    public boolean addToObjectsFolder(File objectsFolder, String objectString) {
            /*
             * Index file need to have form line
             * fileName | size | hash | timestamp(?)
             */
            File hashFolder = new File(objectsFolder.getPath() + "/" + this.hash.substring(0, 2));
            if (!hashFolder.isDirectory()) hashFolder.mkdirs(); // if top two of hash doesn't have a folder yet, make it
            // Create and open the soon to be compressed file with the bottom 38 of the hash as its name
            File compressedFile = new File(hashFolder.getPath() + "/" + this.hash.substring(2, this.hash.length()));
            try {
                compressedFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create a file for  " + this.file.getName() + " in objects folder.");
                e.printStackTrace();
                return false;
            }

            // Doing try block like this *should* close the streams once it ends
            try 
            (
                ByteArrayInputStream bis = new ByteArrayInputStream(objectString.getBytes());
                FileOutputStream fos = new FileOutputStream(compressedFile);
                DeflaterOutputStream dos = new DeflaterOutputStream(fos);
            ) {
                int data = bis.read();
                while (data != -1) {
                    dos.write(data);
                    data = bis.read();
                }
            }
            catch (IOException e) {
                System.out.println("Error occured when creating compressed file in objects");
                e.printStackTrace();
                return false;
            }
            return true;
    }

    /**
     * Important: Objects hash variable must be set before calling this function (TODO: Make it so you just include the has when calling function)
     * 
     * @param indexFile - A reference to .jit's index file
     * @return true if new entry was successfully added to index file, false otherwise
     */
    public boolean addToIndex(File indexFile) {
        try (BufferedWriter outBuffer = new BufferedWriter(new FileWriter(indexFile.getPath(), true));) {
            outBuffer.write(this.file.getName() + DELIMINATER + Long.toString(this.file.length()) + DELIMINATER + this.hash + DELIMINATER + java.time.Instant.now().toString() + "\n"); // add changed blob file to index
        }
        catch (IOException e) {
            System.out.println("Error occured when trying to write " + this.file.getName() + " to index file");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String readFileFromObjects(File toRead) {
        try 
        (
            FileInputStream fis = new FileInputStream(toRead);
            InflaterInputStream iis = new InflaterInputStream(fis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ) {
            int data = iis.read();
            while (data != -1) {
                bos.write(data);
                data = iis.read();
            }
            return bos.toString();
        }
        catch (IOException e) {
            System.out.println("Error occured when reading compressed file " + toRead.getName() + " in objects");
            e.printStackTrace();
            return null;
        }
    }

    public static String readFileFromObjects(String fileHash, File jitFolder) {
        String topHash = fileHash.substring(0, 2);
        String bottomHash = fileHash.substring(2);

        File objectToRead = new File(jitFolder.getPath() + "/objects/" + topHash + "/" + bottomHash);

        assert(objectToRead.exists());

        return readFileFromObjects(objectToRead);
    }

    @Override
    public String toString() {
        return "hash = " + this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbstractJitObject aobj = (AbstractJitObject) obj;
        return Objects.equals(this.hash, aobj.hash);
    }
}