package JavaGit.CommitObjects;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;

public abstract class CommitObject {
    public File file;
    public String hash;
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
                System.out.println(e);
                return false;
            }
            return true;
    }

    public boolean addToIndex(File indexFile) {
        try (BufferedWriter outBuffer = new BufferedWriter(new FileWriter(indexFile.getPath(), true));) {
            outBuffer.write(this.file.getName() + " | " + Long.toString(this.file.length()) + " | " + this.hash + " | " + java.time.Instant.now().toString() + "\n"); // add changed blob file to index
        }
        catch (IOException e) {
            System.out.println("Error occured when trying to write " + this.file.getName() + " to index file");
            System.out.println(e);
            return false;
        }
        return true;
    }
}