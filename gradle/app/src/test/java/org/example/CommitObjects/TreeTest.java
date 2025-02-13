package org.example.CommitObjects;

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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TreeTest {
    static String homeDir = "src/test/resources/ObjectTests/TreeTests";
    static File homeFolder = new File(homeDir);
    static Init init = new Init(homeDir);

    File jitFolder = new File(homeDir + "/.jit");
    File indexFile = new File(homeDir + "/.jit/index");
    File objectsFolder = new File(homeDir + "/.jit/objects");

    Add add = new Add(jitFolder);
    
    @BeforeAll
    static void setUp() {
        CleanUp.cleanFolder(homeFolder);
        init.createDirStructure();
    }
    @Test
    @Order(1)
    void makeATreeWithOneFile() {
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

        assert(tree.children.size() == 1);

        String treeHash = Tree.getHash(tree);
        assertNotNull(treeHash);
        assert(objectsFolder.listFiles().length == 2);
    }

    @Test
    @Order(2)
    void testGetIndexFileOneEntry() {
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
        // This is a bit flakey, doesn't work right for some reason when I don't reset homefolder beforehand
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
}
