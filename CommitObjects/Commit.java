package JavaGit.CommitObjects;

import java.io.File;

public class Commit extends CommitObject{
    public File file;
    public long fileSize;
    public String blob;
    public String hash;
    @Override
    public String hash(String toHash) {
        return "";
    }
}