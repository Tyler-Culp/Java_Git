package org.example.CommitObjects;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;


public class CommitObject extends AbstractJitObject {
    public static final int userNameIndex = 1;
    public static final int commitTreeHashIndex = 2;
    public static final int prevCommitHashIndex = 3;

    private File jitFolder;
    private Tree root;
    public String message;
    public String prevCommit;

    public CommitObject(File jitFolder, String message) {
        this.jitFolder = jitFolder;
        this.file = new File(jitFolder.getParent());
        this.message = message;
        this.prevCommit = getPrevCommitHash();
        Tree.setJitFolder(jitFolder);
        this.root = Tree.createTree();
        this.objectString = makeObjectString();
        this.hash = hash(this.objectString);
    }

    public boolean commit() {
        if (!resetIndexAndHEAD()) return false;
        File objectsFolder = new File(this.jitFolder.getPath() + "/objects");
        return addToObjectsFolder(objectsFolder, this.objectString);
    }

    public Tree getRoot() {
        return this.root;
    }

    private boolean resetIndexAndHEAD() {
        File headFile = new File(this.jitFolder.getPath() + "/HEAD");
        File indexFile = new File(this.jitFolder.getPath() + "/index");
        try (
            FileWriter headFW = new FileWriter(headFile, false);
            FileWriter indexFW = new FileWriter(indexFile, false);
            ) {
            headFW.write(this.hash);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String getPrevCommitHash() {
        String prevCommitHash = "";

        File headFile = new File(this.jitFolder.getPath() + "/HEAD");
        if (headFile.length() > 0) { //previous commit does exist
            try (Scanner sc = new Scanner(headFile)) {
                prevCommitHash = sc.nextLine().replace("\n", "");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } 
        return prevCommitHash;
    }

    /**
     * Commit objects should take the form of:
     * <ul>
     *  <li>Committer name</li>
     *  <li>Comitter message</li>
     *  <li>commit-tree hash</li>
     *  <li>previous commit-tree hash</li>
     * </ul>
     * 
     * @return the string form of a commit object.
     * 
     */
    private String makeObjectString() {
        String objString = "commit\n";
        String username  = System.getProperty("user.name") + "\n";
        String commitTreeHash = Tree.getHash(this.root) + "\n";
        String prevCommitHash = getPrevCommitHash();
        prevCommitHash = prevCommitHash.equals("") ? "\n" : prevCommitHash;
        objString += username + this.message + "\n" + commitTreeHash + prevCommitHash; 
        return objString;
    }
}