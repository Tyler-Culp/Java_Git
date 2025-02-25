package org.example.Commands;
import java.io.File;

import org.example.CommitObjects.CommitObject;
import org.example.CommitObjects.Tree;

public class Commit {
    private File jitFolder;
    private String message;
    private String commitHash;
    private Tree root;
    public Commit(File jitFolder, String message) {
        this.jitFolder = jitFolder;
        this.message = message;
    }

    public boolean commit() {
        CommitObject cmt = new CommitObject(this.jitFolder, this.message);
        this.commitHash = cmt.getHash();
        this.root = cmt.getRoot();
        return cmt.commit();
    }

    public String getCommitHash() {
        return this.commitHash;
    }

    public Tree getRoot() {
        return this.root;
    }
}
