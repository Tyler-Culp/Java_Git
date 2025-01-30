package JavaGit.CommitObjects;

import java.io.*;
import java.util.Scanner;


public class Blob extends CommitObject{
    public File file;
    public String objectString;
    public String hash;
    public Blob(File file) {
        this.file = file;
        this.objectString = createFileString(this.file);
        this.hash = hash(this.objectString);
    }

    private String createFileString(File file)  {
        String fileText = "blob " + Long.toString(this.file.length()) + "\n"; //Gonna make this a new line so the first line of blob is metadata
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
}
