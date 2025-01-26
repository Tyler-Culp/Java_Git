package JavaGit.CommitObjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class Blob {
    File file;
    public Blob(File file) {
        this.file = file;
    }
    public String hash() throws FileNotFoundException {
        String fileText = "";
        try (Scanner sc = new Scanner(this.file)) {

            while (sc.hasNextLine()) {
                fileText = fileText + sc.nextLine() + "\n";
            }
        }

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");

                String blobString = "blob:" + Long.toString(this.file.length()) + "\n" + fileText;
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
