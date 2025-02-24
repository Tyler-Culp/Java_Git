package org.example.Commands;
import java.io.File;

import org.example.CommitObjects.CommitObject;

public class Commit {
    private File jitFolder;
    private String message;
    private String commitHash;
    public Commit(File jitFolder, String message) {
        this.jitFolder = jitFolder;
        this.message = message;
    }

    public boolean commit() {
        CommitObject cmt = new CommitObject(this.jitFolder, this.message);
        this.commitHash = cmt.getHash();
        return cmt.commit();
    }

    public String getCommitHash() {
        return this.commitHash;
    }
}
