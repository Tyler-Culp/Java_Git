package org.example.CommitObjects;

import java.io.File;

import org.example.Helpers.*;

public class CommitObject extends AbstractJitObject {
    private File jitFolder;
    private Tree root;
    public String hash;
    public String message;
    public String prevCommit;

    public CommitObject() {
        this.jitFolder = new FindJit().find();
        File rootDir = new File(jitFolder.getParent());
        this.root = Tree.createTree(rootDir);
        this.prevCommit = getPrevCommit();
    }

    private String getPrevCommit() {
        return "";
    }

}