package org.example.Commands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.*;

import org.example.Commands.*;
import org.example.CommitObjects.*;
import org.example.CleanUp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Execution(ExecutionMode.SAME_THREAD)  // Force single-thread execution
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatusTest {
    static String homeDir = "src/test/resources/CommandTests/StatusTest";
    static File homeFolder = new File(homeDir);
    static Init init = new Init(homeDir);
    File jitFolder = new File(homeDir + "/.jit");
    Status status;

    @BeforeEach
    void setUp() {
        CleanUp.cleanFolder(homeFolder);
        init.createDirStructure();
    }

    @Test 
    @Order(1) 
    void noChangesToReport() {
        status = new Status(jitFolder);
        ArrayList<Blob> lst = status.getChangedFiles(homeFolder);

        assertEquals(new ArrayList<Blob>(), lst);

    }
    @Order(2)
    @Test void addedFilesToReport() {
        status = new Status(jitFolder);
        File groceries = new File(homeDir + "/groceries.txt");
        try {
            groceries.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        ArrayList<Blob> lst = status.getChangedFiles(homeFolder);
        Blob blob = new Blob(groceries);
        List<Blob> expected = new ArrayList<>();
        expected.add(blob);

        System.out.println(expected);
        System.out.println(lst);

        assertEquals(expected, lst);
    }
    @Test
    @Order(3)
    void multipleFilesToReport() {
        status = new Status(jitFolder);
        File stars = new File(homeDir + "/stars.txt");
        File sports = new File(homeDir + "/sports.txt");
        File groceries = new File(homeDir + "/groceries.txt");

        try {
            stars.createNewFile();
            sports.createNewFile();
            groceries.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Blob> lst = status.getChangedFiles(homeFolder);
        List<Blob> expected = new ArrayList<>();
        expected.add(new Blob(groceries));
        expected.add(new Blob(stars));
        expected.add(new Blob(sports));

        assertEquals(expected, lst);
    }

    @Test
    @Order(4)
    void addTheChangeTest() {
        status = new Status(jitFolder);
        File f1 = new File(homeDir + "/f1.txt");
        File f2 = new File(homeDir + "/f2.txt");

        try {
            f1.createNewFile();
            f2.createNewFile();
        }
        catch (Exception e) {
            e.printStackTrace();
            assert(false);
        }

        Add adder = new Add(jitFolder);

        ArrayList<Blob> expected = new ArrayList<>();
        expected.add(new Blob(f1));
        expected.add(new Blob(f2));

        ArrayList<Blob> actual = status.getChangedFiles(homeFolder);

        assertEquals(expected, actual);

        adder.add(homeDir);

        actual = status.getChangedFiles(homeFolder);
        expected = new ArrayList<>();

        assertEquals(expected, actual);
    }

    // @Test
    // @Order(4)
    // void addFilesThenChangeTest() {
    //     status = new Status(jitFolder);
    //     File stars = new File(homeDir + "/stars.txt");
    //     File sports = new File(homeDir + "/sports.txt");
    //     File groceries = new File(homeDir + "/groceries.txt");

    //     try {
    //         stars.createNewFile();
    //         sports.createNewFile();
    //         groceries.createNewFile();
    //     }
    //     catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     Add adder = new Add(jitFolder);
    //     adder.add(homeFolder);
    //     ArrayList<Blob> lst = status.getChangedFiles(homeFolder);
    //     List<Blob> expected = new ArrayList<>();

    //     assertEquals(expected, lst);

    //     try (
    //         FileWriter fw1 = new FileWriter(sports);
    //         FileWriter fw2 = new FileWriter(groceries);
    //     ) {
    //         fw1.write("baseball\nbasketball\nfootball");
    //         fw2.write("bread\neggs\nmilk");
    //     }
    //     catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     lst = status.getChangedFiles(homeFolder);

    //     expected.add(new Blob(sports));
    //     expected.add(new Blob(groceries));

    //     assertEquals(expected, lst);
    // }
    @Test void deletedFilesToReport() {

    }
}
