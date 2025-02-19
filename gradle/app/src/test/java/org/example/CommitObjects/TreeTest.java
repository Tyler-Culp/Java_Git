package org.example.CommitObjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

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
public class TreeTest {
    static String homeDir = "src/test/resources/ObjectTests/TreeTests";
    static File homeFolder = new File(homeDir);
    static Init init = new Init(homeDir);

    File jitFolder = new File(homeDir + "/.jit");
    File indexFile = new File(homeDir + "/.jit/index");
    File objectsFolder = new File(homeDir + "/.jit/objects");

    Add add;
    
    @BeforeAll
    static void setUp() {
        CleanUp.cleanFolder(homeFolder);
        init.createDirStructure();
    }
    @Test
    @Order(1)
    void makeATreeWithOneFile() {
        add = new Add(jitFolder);
        File file1 = new File(homeDir + "/file1.txt");

        try {
            file1.createNewFile();
            try (
                FileWriter fw = new FileWriter(file1);
            ) {
                fw.write("This is a test");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assert(indexFile.exists());

        add.add(homeDir);

        assert(indexFile.length() > 0);

        Tree.setJitFolder(jitFolder);

        Tree tree = Tree.createTree();

        assertEquals(1, tree.getSize());

        String treeHash = Tree.getHash(tree);
        assertNotNull(treeHash);
        assert(objectsFolder.listFiles().length == 2);
    }

    @Test
    @Order(2)
    void testGetIndexFileOneEntry() {
        add = new Add(jitFolder);
        File file1 = new File(homeDir + "/file1.txt");

        try {
            file1.createNewFile();
            try (
                FileWriter fw = new FileWriter(file1);
            ) {
                fw.write("This is a new test to ensure index set is working right");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assert(indexFile.exists());
        add.add(homeDir);

        try (FileWriter fw = new FileWriter(file1)) {
            fw.write("This is a second write just to make extra sure there is only 1 entry");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        add.add(homeDir);

        Map<String, String> m = Tree.getIndexFiles(indexFile);

        assert(m.size() == 1);

        assert(m.containsKey("file1.txt"));
    }

    @Test
    @Order(3)
    void testGetIndexFileMultipleEntries() {
        add = new Add(jitFolder);
        File file1 = new File(homeDir + "/file1.txt");
        File file2 = new File(homeDir + "/file2.txt");

        try {
            file1.createNewFile();
            file2.createNewFile();
            try (
                FileWriter fw1 = new FileWriter(file1);
                FileWriter fw2 = new FileWriter(file2);
            ) {
                fw1.write("testing123");
                fw2.write("testing456");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assert(indexFile.exists());

        add = new Add(jitFolder);
        add.add(homeFolder);

        Map<String, String> m = Tree.getIndexFiles(indexFile);

        assert(m.size() == 2);

        assert(m.containsKey("file1.txt"));
        assert(m.containsKey("file2.txt"));
    }

    @Test
    @Order(4)
    void makeTreeWithSubFolders() {
        add = new Add(jitFolder);
        File file1 = new File(homeDir + "/file1.txt");
        File file2 = new File(homeDir + "/file2.txt");

        File folder1 = new File(homeDir + "/folder1");
        File folder2 = new File(homeDir + "/folder2");

        File nested1a = new File(folder1.getPath() + "/nested1a.txt");
        File nested1b = new File(folder1.getPath() + "/nested1b.txt");

        File nested2a = new File(folder2.getPath() + "/nested2a.txt");
        File nested2b = new File(folder2.getPath() + "/nested2b.txt");

        try {
            file1.createNewFile();
            file2.createNewFile();

            folder1.mkdir();
            folder2.mkdir();

            nested1a.createNewFile();
            nested1b.createNewFile();

            nested2a.createNewFile();
            nested2b.createNewFile();
            try (
                FileWriter fw1 = new FileWriter(file1);
                FileWriter fw2 = new FileWriter(file2);

                FileWriter fwNested1a = new FileWriter(nested1a);
                FileWriter fwNested2b = new FileWriter(nested2b);
            ) {
                fw1.write("testing123");
                fw2.write("testing456");

                fwNested1a.write("I'm a nested file uwu");
                fwNested2b.write("I'm a different nested file UwU");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        add.add(homeFolder);

        Tree.setJitFolder(jitFolder);

        Tree tree = Tree.createTree();

        assertEquals(8, tree.getSize());
    }
}
