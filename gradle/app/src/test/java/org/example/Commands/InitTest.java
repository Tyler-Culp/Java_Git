package org.example.Commands;

import org.example.Commands.Init;
import org.example.Helpers.FindJit;
import org.example.CleanUp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class InitTest {
    String homeDir = "src/test/resources/CommandTests/InitTest/";
    File homeFolder = new File(homeDir);
    @Test void createNewFolder() {
        File jitFolder = new File(homeDir + "/.jit");
        assert(homeFolder.exists());
        assert(!jitFolder.exists());

        Init init = new Init(homeDir);
        boolean initialized = init.createDirStructure();

        assert(initialized);

        File foundJitFolder = new FindJit().find(homeDir);
        assertNotNull(foundJitFolder);

        File headFile = new File(homeDir + "/.jit/HEAD");
        File indexFile = new File(homeDir + "/.jit/index");
        File refsFolder = new File(homeDir + "/.jit/refs");
        File objectsFolder = new File(homeDir + "/.jit/objects");

        assert(headFile.exists());
        assert(indexFile.exists());
        assert(refsFolder.exists());
        assert(objectsFolder.exists());

        CleanUp.cleanFolder(foundJitFolder);

        assert(!new File(homeDir + "/.jit").exists());
    }
}