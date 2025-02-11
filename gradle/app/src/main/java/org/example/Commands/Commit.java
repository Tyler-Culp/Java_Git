package org.example.Commands;
import org.example.Commands.Status;

public class Commit {
    Status statusTracker;
    File jitFolder;
    public Commit(File jitFolder) {
        this.jitFolder = jitFolder;
        this.statusTracker = new Status(jitFolder);
    }
}
