package CommitObjects;
import java.io.File;

abstract class CommitObject {
    public File file;

    abstract String hash();
}