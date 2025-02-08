package org.example.CommitObjects;

import java.io.File;


public class CommitObject extends AbstractJitObject {
    private Tree root;
    public String hash;
    public String message;
    public String prevCommit;

    public CommitObject(File jitFolder) {
        File rootDir = new File(jitFolder.getParent());
        this.root = Tree.createTree(rootDir);
        this.prevCommit = getPrevCommit();
    }

    private String getPrevCommit() {
        return "";
    }

}