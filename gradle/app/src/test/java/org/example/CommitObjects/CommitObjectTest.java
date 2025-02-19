package org.example.CommitObjects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileWriter;

import org.example.CleanUp;
import org.example.Commands.Add;
import org.example.Commands.Init;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)  // Force single-thread execution
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommitObjectTest {
    static String homeDir = "src/test/resources/ObjectTests/CommitTests";
    static Init init = new Init(homeDir);
    static File homeFolder = new File(homeDir);
    File jitFolder;
    @BeforeAll
    static void setUp() {
        homeFolder.mkdir();
        CleanUp.cleanFolder(homeFolder);
        init.createDirStructure();  
    } 

    @Test
    @Order(1)
    void emptyCommitObject() {
        jitFolder = new File(homeDir + "/.jit");
        String message = "A commit with nothing staged";
        CommitObject commitObject = new CommitObject(jitFolder, message);

        assertNotNull(commitObject);
        assertEquals("", commitObject.prevCommit);

        String username  = System.getProperty("user.name") + "\n";
        String expected = username + message + "\n" + commitObject.getRoot().hash + "\n\n";
        assertEquals(expected, commitObject.objectString);
    }

    @Test
    @Order(2)
    void commitFromIndex() {
        File groceries = new File(homeFolder.getPath() + "/groceries.txt");

        try {
            groceries.createNewFile();
            try (
                FileWriter fw = new FileWriter(groceries);
            ) {
                fw.write("Bananas, Apples, Bread, Onions, Milk\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        jitFolder = new File(homeDir + "/.jit");
        Add adder = new Add(jitFolder);
        adder.add(homeFolder);

        String message = "Created groceries file";

        CommitObject commitObject = new CommitObject(jitFolder, message);

        assertEquals(1, commitObject.getRoot().getSize());

        boolean successfulCommit = commitObject.commit();

        assert(successfulCommit);

        String topCommitHash = commitObject.hash.substring(0, 2);
        String bottomCommitHash = commitObject.hash.substring(2);

        File objectsFolder = new File(jitFolder.getPath() + "/objects");
        File topCommitHashFolder = new File(objectsFolder.getPath() + "/" + topCommitHash);
        File bottomCommitHashFile = new File(topCommitHashFolder.getPath() + "/" + bottomCommitHash);

        assert(topCommitHashFolder.exists());
        assertEquals(1, topCommitHashFolder.list().length);

        assert(bottomCommitHashFile.exists());
        assert(bottomCommitHashFile.length() > 0);

        String committedHashString = AbstractJitObject.readFileFromObjects(bottomCommitHashFile);

        assertEquals(commitObject.objectString, committedHashString);

    }
    @Test
    @Order(3)
    void commitFromHashAndIndex() {
        File groceries = new File(homeFolder.getPath() + "/groceries.txt");
        File sports = new File(homeDir + "/sports.txt");

        try {
            groceries.createNewFile();
            sports.createNewFile();
            try (
                FileWriter fwGrocers = new FileWriter(groceries);
                FileWriter fwSports = new FileWriter(sports);
            ) {
                fwSports.write("Basketball, Football, Soccer, Baseball, Hockey\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        jitFolder = new File(homeDir + "/.jit");
        Add adder = new Add(jitFolder);
        adder.add(homeFolder);

        String message = "Created sports file";

        CommitObject commitObject = new CommitObject(jitFolder, message);

        assertEquals(2, commitObject.getRoot().getSize());

        boolean successfulCommit = commitObject.commit();

        assert(successfulCommit);

        String topCommitHash = commitObject.hash.substring(0, 2);
        String bottomCommitHash = commitObject.hash.substring(2);

        File objectsFolder = new File(jitFolder.getPath() + "/objects");
        File topCommitHashFolder = new File(objectsFolder.getPath() + "/" + topCommitHash);
        File bottomCommitHashFile = new File(topCommitHashFolder.getPath() + "/" + bottomCommitHash);

        assert(topCommitHashFolder.exists());
        assertEquals(1, topCommitHashFolder.list().length);

        assert(bottomCommitHashFile.exists());
        assert(bottomCommitHashFile.length() > 0);

        String committedHashString = AbstractJitObject.readFileFromObjects(bottomCommitHashFile);

        assertEquals(commitObject.objectString, committedHashString);
    }
    @Test
    @Order(4)
    void commitWithNestedFolders() {
        File nestedFolder1 = new File(homeDir + "/folder1");
        File nestedFolder2 = new File(homeDir + "/folder1/folder2");

        nestedFolder2.mkdirs();

        File nestedFile1 = new File(nestedFolder1.getPath() + "/nsted1.txt");
        File nestedFile2 = new File(nestedFolder2.getPath() + "/nsted2.txt");

        try {
            nestedFile1.createNewFile();
            nestedFile2.createNewFile();
            try (
                FileWriter fw1 = new FileWriter(nestedFile1);
                FileWriter fw2 = new FileWriter(nestedFile2);
            ){
                fw1.write("The first nested file");
                fw2.write("Second nested file");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        jitFolder = new File(homeDir + "/.jit");
        Add add = new Add(jitFolder);
        add.add(homeFolder);

        String message = "Created some nested folders";

        CommitObject commitObject = new CommitObject(jitFolder, message);

        assertEquals(6, commitObject.getRoot().getSize());

        boolean successfulCommit = commitObject.commit();

        assert(successfulCommit);

        String topCommitHash = commitObject.hash.substring(0, 2);
        String bottomCommitHash = commitObject.hash.substring(2);

        File objectsFolder = new File(jitFolder.getPath() + "/objects");
        File topCommitHashFolder = new File(objectsFolder.getPath() + "/" + topCommitHash);
        File bottomCommitHashFile = new File(topCommitHashFolder.getPath() + "/" + bottomCommitHash);

        assert(topCommitHashFolder.exists());
        assertEquals(1, topCommitHashFolder.list().length);

        assert(bottomCommitHashFile.exists());
        assert(bottomCommitHashFile.length() > 0);

        String committedHashString = AbstractJitObject.readFileFromObjects(bottomCommitHashFile);

        assertEquals(commitObject.objectString, committedHashString);
    }
}
