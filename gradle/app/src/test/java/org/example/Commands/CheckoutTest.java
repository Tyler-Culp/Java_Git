package org.example.Commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    
    // @Test
    // @Order(2)
    // void checkoutSingleFileDirectory() {
    //     File groceries = new File(homeDir + "/groceries.txt");

    //     try {
    //         groceries.createNewFile();
    //         try (FileWriter fw = new FileWriter(groceries)) {
    //             fw.write("I'm a grocery list\nLook at me yay");
    //         }
    //     }
    //     catch (Exception e) {
    //         e.printStackTrace();
    //         assert(false);
    //     }

    //     Commit commit = new Commit(jitFolder, "groceries file created");
    //     assert(commit.commit());

    //     String prevCommitHash = commit.getCommitHash();

    //     Checkout chkout = new Checkout(jitFolder, prevCommitHash);

    //     File copyDir = chkout.checkoutCopy();

    //     assert(copyDir.exists() && copyDir.isDirectory());

    //     System.out.println(copyDir.listFiles().length);

    //     assertEquals(1, copyDir.listFiles().length);
    // }
}
