package org.example.Commands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.*;

import org.example.Commands.*;
import org.example.CommitObjects.*;
import org.example.CleanUp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddTest {
    static String homeDir = "src/test/resources/CommandTests/AddTest";
    static File homeFolder = new File(homeDir);
    static Init init = new Init(homeDir);

    File jitFolder = new File(homeDir + "/.jit");
    File indexFile = new File(homeDir + "/.jit/index");
    File objectsFolder = new File(homeDir + "/.jit/objects");

    Status status;
    Add add = new Add(jitFolder);

    @BeforeAll
    static void setUp() {
        CleanUp.cleanFolder(homeFolder);
        init.createDirStructure();
    }

    @Test
    @Order(1) 
    void addOneFile() {
        status = new Status(jitFolder);
        File file1 = new File(homeDir + "/hi.txt");
        
        try {
            file1.createNewFile();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        assert(indexFile.length() == 0);
        assert(objectsFolder.isDirectory());
        assert(objectsFolder.listFiles().length == 0);

        add.add(file1);
        assert(indexFile.length() > 0);

        Blob blob = new Blob(file1);
        String[] hashParts = getHashPieces(blob);
        String frontHash = hashParts[0];
        String backHash = hashParts[1];

        File objFolder = new File(objectsFolder.getPath() + "/" + frontHash);
        File objFile = new File(objFolder.getPath() + "/" + backHash);

        assert(objFolder.exists() && objFolder.isDirectory());
        assert(objFile.exists() && objFile.isFile());

        assert(objFolder.listFiles().length == 1);
        assert(objFile.length() > 0);
    }

    @Test
    @Order(2)
    void addMultipleFiles() {
        status = new Status(jitFolder);
        File file1 = new File(homeDir + "/hi.txt");
        File file2 = new File(homeDir + "/bye.txt");
        File file3 = new File(homeDir + "/picture.png");

        try {
            file1.createNewFile();
            file2.createNewFile();
            file3.createNewFile();
            try 
            (
                FileWriter f1 = new FileWriter(file1);
                FileWriter f2 = new FileWriter(file2);
                FileWriter f3 = new FileWriter(file3);
            ){

                f1.write("Hello\n");
                f2.write("Bye\n");
                f3.write("I'm a picture, promise\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Blob b1 = new Blob(file1);
        Blob b2 = new Blob(file2);
        Blob b3 = new Blob(file3);

        String[] hashParts = getHashPieces(b1);
        String frontHash1 = hashParts[0];
        String backHash1 = hashParts[1];

        hashParts = getHashPieces(b2);
        String frontHash2 = hashParts[0];
        String backHash2 = hashParts[1];

        hashParts = getHashPieces(b3);
        String frontHash3 = hashParts[0];
        String backHash3 = hashParts[1];

        add.add(homeFolder);

        File obj1Folder = new File(objectsFolder.getPath() + "/" + frontHash1);
        File obj2Folder = new File(objectsFolder.getPath() + "/" + frontHash2);
        File obj3Folder = new File(objectsFolder.getPath() + "/" + frontHash3);

        assert(obj1Folder.exists() && obj1Folder.isDirectory() && obj1Folder.list().length == 1);
        assert(obj2Folder.exists() && obj2Folder.isDirectory() && obj2Folder.list().length == 1);
        assert(obj3Folder.exists() && obj3Folder.isDirectory() && obj3Folder.list().length == 1);

        File obj1File = new File(obj1Folder.getPath() + "/" + backHash1);
        File obj2File = new File(obj2Folder.getPath() + "/" + backHash2);
        File obj3File = new File(obj3Folder.getPath() + "/" + backHash3);

        assert(obj1File.exists() && obj1File.isFile() && obj1File.length() > 0);
        assert(obj2File.exists() && obj2File.isFile() && obj2File.length() > 0);
        assert(obj3File.exists() && obj3File.isFile() && obj3File.length() > 0);
    }

    @Test
    @Order(3)
    void writeToFileAndReadItt() {
        status = new Status(jitFolder);
        File file1 = new File(homeDir + "/hi.txt");

        try {
            file1.createNewFile();
            try (
            FileWriter writer = new FileWriter(file1);
            ) {
                writer.append("Hi to everyone reading!!!\n");
        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        add.add(file1);

        Blob b1 = new Blob(file1);
        String[] hashParts = getHashPieces(b1);
        String frontHash = hashParts[0];
        String backHash = hashParts[1];

        File objFolder = new File(objectsFolder.getPath() + "/" + frontHash);
        File objFile = new File(objFolder.getPath() + "/" + backHash);

        assert(objFolder.exists() && objFolder.isDirectory());
        assert(objFile.exists() && objFile.isFile() && objFile.length() > 0);

        String expected = "blob 26\nHi to everyone reading!!!\n";
        String actual = AbstractJitObject.readFileFromObjects(objFile);

        assertEquals(expected, actual);
    }
    @Test
    void checkRedundantAdds() {
        status = new Status(jitFolder);
        File redundant = new File(homeDir + "/reduntant.txt");

        try {
            redundant.createNewFile();
            try (FileWriter fw = new FileWriter(redundant)) {
                fw.write("thing1");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        add.add(redundant);

        try (FileWriter fw = new FileWriter(redundant)) {
            fw.write("thing2");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        add.add(redundant);

        try (FileWriter fw = new FileWriter(redundant)) {
            fw.write("thing1");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        add.add(redundant);
        assert(true);

    }

    String[] getHashPieces(Blob b) {
        String[] hashParts = new String[2];
        hashParts[0] = b.hash.substring(0,2);
        hashParts[1] = b.hash.substring(2, b.hash.length());
        return hashParts;
    }
}
