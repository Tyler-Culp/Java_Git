package JavaGit.CommitObjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class Blob {
    public File file;
    public long fileSize;
    public String blob;
    public String hash;
    public Blob(File file) {
        this.file = file;
        this.fileSize = file.length();

        this.blob = createFileString(this.file, this.fileSize);
        this.hash = hash(this.blob);
    }

    private String createFileString(File file, long fileSize)  {
        String fileText = "blob " + Long.toString(fileSize) + "\n"; //Gonna make this a new line so the first line of blob is metadata
        Scanner sc;
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                fileText = fileText + sc.nextLine() + "\n";
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
            System.out.println("Could not find file in Blob.java");
        }

        return fileText;
    }

    private String hash(String blobString) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // Turn the string into an array of bytes
            byte[] byteDigest = md.digest(blobString.getBytes());
            
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
    
}
