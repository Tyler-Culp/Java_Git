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
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatusTest {
    static String homeDir = "src/test/resources/CommandTests/StatusTest";
    static File homeFolder = new File(homeDir);
    static Init init = new Init(homeDir);
    File jitFolder = new File(homeDir + "/.jit");
    Status status;

    @BeforeAll
    static void setUp() {
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
    void addFilesThenChangeTest() {
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

        Add adder = new Add(jitFolder);
        adder.add(homeFolder);
        ArrayList<Blob> lst = status.getChangedFiles(homeFolder);
        List<Blob> expected = new ArrayList<>();

        assertEquals(expected, lst);

        try (
            FileWriter fw1 = new FileWriter(sports);
            FileWriter fw2 = new FileWriter(groceries);
        ) {
            fw1.write("baseball\nbasketball\nfootball");
            fw2.write("bread\neggs\nmilk");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        lst = status.getChangedFiles(homeFolder);

        expected.add(new Blob(sports));
        expected.add(new Blob(groceries));

        assertEquals(expected, lst);
    }
    @Test void deletedFilesToReport() {

    }
}
