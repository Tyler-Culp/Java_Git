package org.example.Commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.example.CleanUp;
import org.example.Commands.Add;
import org.example.Commands.Init;
import org.example.CommitObjects.AbstractJitObject;
import org.example.CommitObjects.Tree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)  // Force single-thread execution
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CheckoutTest {
    static String homeDir = "src/test/resources/CommandTests/CheckoutTests";
    static Init init = new Init(homeDir);
    static File homeFolder = new File(homeDir);
    static File jitFolder;
    @BeforeAll
    static void setUp() {
        homeFolder.mkdir();
        CleanUp.cleanFolder(homeFolder);
        init.createDirStructure(); 
        jitFolder = new File(homeDir + "/.jit"); 
    }
    @Test
    @Order(1)
    void checkoutEmptyDirectory() {
        Commit commit = new Commit(jitFolder, "nothing to commit");
        commit.commit();
        
        String commitHash = commit.getCommitHash();

        Checkout checkout = new Checkout(jitFolder, commitHash);

        File checkedOutFolder = checkout.checkoutCopy();

        assert(checkedOutFolder.exists() && checkedOutFolder.isDirectory());

        assert(checkedOutFolder.list().length == 0);
    }
    
    @Test
    @Order(2)
    void checkoutSingleFileDirectory() {
        File groceries = new File(homeDir + "/groceries.txt");

        try {
            groceries.createNewFile();
            try (FileWriter fw = new FileWriter(groceries)) {
                fw.write("I'm a grocery list\nLook at me yay");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            assert(false);
        }

        Add adder = new Add(jitFolder);
        adder.add(homeDir);

        Commit commit = new Commit(jitFolder, "groceries file created");
        boolean commitSuccess = commit.commit();
        assert(commitSuccess);

        String prevCommitHash = commit.getCommitHash();

        Checkout chkout = new Checkout(jitFolder, prevCommitHash);

        File copyDir = chkout.checkoutCopy();

        assert(copyDir.exists() && copyDir.isDirectory());

        assertEquals(1, copyDir.listFiles().length);

        File groceryCopy = new File(copyDir.getPath() + "/groceries.txt");
        
        try (
            Scanner sc1 = new Scanner(groceries);
            Scanner sc2 = new Scanner(groceryCopy);
        ) {
            while (sc1.hasNextLine() && sc2.hasNextLine()) {
                assertEquals(sc1.nextLine(), sc2.nextLine());
            }
            assert(!sc1.hasNextLine() && !sc2.hasNextLine());
        }
        catch (Exception e) {
            e.printStackTrace();
            assert(false);
        }
    }

    @Test
    @Order(3)
    void checkoutMultiFileDirectory() {
        File groceries = new File(homeDir + "/groceries.txt");
        File folder1 = new File(homeDir + "/folder1");
        File folder2 = new File(homeDir + "/folder2"); 
        File folder3 = new File(homeDir + "/folder2/folder3");

        folder1.mkdir();
        folder2.mkdir();
        folder3.mkdir();

        File sports = new File(folder3.getPath() + "/sports.txt");
        File people = new File(folder2.getPath() + "/people.txt");
        File food = new File(folder1.getPath() + "/food.txt");

        try {
            groceries.createNewFile();
            sports.createNewFile();
            people.createNewFile();
            food.createNewFile();

            try (
                FileWriter fw1 = new FileWriter(groceries);
                FileWriter fw2 = new FileWriter(sports);
                FileWriter fw3 = new FileWriter(people);
                FileWriter fw4 = new FileWriter(food);
            ) {
                fw1.write("I'm a grocery list\nLook at me yay");
                fw2.write("Look at me sports\nfootball nice");
                fw4.write("soup\nchicken\nsmoothie\ngranola");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            assert(false);
        }

        Add adder = new Add(jitFolder);
        adder.add(homeDir);

        Commit commit = new Commit(jitFolder, "groceries file created");
        boolean commitSuccess = commit.commit();
        assert(commitSuccess);

        String prevCommitHash = commit.getCommitHash();

        Checkout chkout = new Checkout(jitFolder, prevCommitHash);

        File copyDir = chkout.checkoutCopy();

        assert(copyDir.exists() && copyDir.isDirectory());

        assertEquals(3, copyDir.listFiles().length);

        File sportsCopy = new File(copyDir.getPath() + "/folder2/folder3/sports.txt");

        try (
            Scanner sc1 = new Scanner(sports);
            Scanner sc2 = new Scanner(sportsCopy);
        ) {
            while (sc1.hasNextLine() && sc2.hasNextLine()) {
                assertEquals(sc1.nextLine(), sc2.nextLine());
            }
            assert(!sc1.hasNextLine() && !sc2.hasNextLine());
        }
        catch (Exception e) {
            e.printStackTrace();
            assert(false);
        }
    }
}
