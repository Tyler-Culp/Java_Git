package org.example.JitFolder;

import org.example.Commands.Init;
import org.example.Helpers.FindJit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;

class CreateJitFolderTest {
    File resourcesFolder = new File("src/test/resources");
    String homeDir = "src/test/resources";
    @Test void createNewFolder() {
        File jitFolder = new File(resourcesFolder.getPath() + "/.jit");
        assert(resourcesFolder.exists());
        assert(!jitFolder.exists());
    }
    @Test void findJitFolder() {
        File jitFolder = new FindJit().find(homeDir);
        assertNull(jitFolder);
    }
    @Test void createJitFolder() {
        assert(resourcesFolder.exists());
        File jitFolder = new FindJit().find(homeDir);
        Init init = new Init(resourcesFolder.getPath(), jitFolder);
        boolean initialized = init.createDirStructure();

        assert(initialized);
        File jit = new File("src/test/resources/.jit");
        assert(jit.exists());

        jit.deleteOnExit();
    }
}
