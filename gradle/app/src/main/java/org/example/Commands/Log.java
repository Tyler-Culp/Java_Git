package org.example.Commands;

import java.io.File;
import java.util.Scanner;

import org.example.CommitObjects.AbstractJitObject;
import org.example.CommitObjects.CommitObject;

public class Log {
    private File jitFolder;

    public Log(File jitFolder) {
        this.jitFolder = jitFolder;
    }

    /**
     * Used to see up to the last n commits
     * 
     * @param n - number of previous commits to print out
     */
    public void getLastNCommits(int n) {
        File headFile = new File(this.jitFolder.getPath() + "/HEAD");
        assert(headFile.exists() && headFile.isFile());

        if (headFile.length() == 0) {
            System.out.println("Nothing has been committed yet");
        }

        try (Scanner sc = new Scanner(headFile)) {
            String hash = sc.nextLine().replace("\n", "");
            getLastNCommits(n, hash);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when trying to get log of commits");
        }
    }

    private void getLastNCommits(int commitsLeft, String hash) {
        if (commitsLeft <= 0) return; // base case

        String commitString = AbstractJitObject.readFileFromObjects(hash, jitFolder);
        System.out.println(commitString);
        try {
            String prevCommitHash = commitString.split("\n")[CommitObject.prevCommitHashIndex];
            getLastNCommits(commitsLeft--, prevCommitHash);
        }
        catch (Exception e) {
            return;
        }
    }
}
