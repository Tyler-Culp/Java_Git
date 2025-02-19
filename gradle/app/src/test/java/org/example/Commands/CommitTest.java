package org.example.Commands;
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
public class CommitTest {
    static String homeDir = "src/test/resources/CommandTests/CommitTests";
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
    void oneCommit() {
        File groceries = new File(homeFolder.getPath() + "/groceries.txt");
        File sports = new File(homeDir + "/sports.txt");

        try {
            groceries.createNewFile();
            sports.createNewFile();
            try (
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

        String message = "Created two files and wrote sports into sports.txt";

        Commit commit = new Commit(jitFolder, message);

        boolean success = commit.commit();

        assert(success);
    }
}
