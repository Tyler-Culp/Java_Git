package org.example.Commands;

import org.example.Commands.Status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.Commands.*;

import java.io.File;

public class StatusTest {
    String homeDir = "src/test/resources/CommandTests/StatusTest/";
    File homeFolder = new File(homeDir);
    Init init = new Init();
    File jitFolder = new File(homeDir + "/.jit");
    init.createDirStructure(homeDir);
    @Test @Order1 void noChangesToReport() {
        Status status = new Status(jitFolder);
        status.

    }
    @Test @Order2 void addedFilesToReport() {

    }
    @Test @Order3 void modifiedFilesToReport() {

    }
    @Test @Order4 void deletedFilesToReport() {

    }
}
