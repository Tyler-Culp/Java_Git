abstract class Commit_Object {
    public String name;
    public String hash;
    public String previousCommit;
    public String type;

    abstract int hash();
}