package org.example.Commands;
import java.io.File;

import org.example.CommitObjects.CommitObject;

public class Commit {
    private File jitFolder;
    private String message;
    public Commit(File jitFolder, String message) {
        this.jitFolder = jitFolder;
        this.message = message;
    }

    public boolean commit() {
        CommitObject cmt = new CommitObject(this.jitFolder, this.message);
        return cmt.commit();
    }
}
