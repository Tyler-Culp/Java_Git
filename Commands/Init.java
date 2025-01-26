package JavaGit.Commands;

import java.io.File;
import java.io.IOException;

/**
 * To create the necessary folder system necessary for a git command tool
 */

public class Init {
    String homeDir = System.getProperty("user.dir");

    public void createDirStructure() {
        String currDir = this.homeDir;
        File hidderDir = new File(currDir + "/.jit");
        
        boolean cretedHidden = hidderDir.mkdir();
        if (!cretedHidden) {
            System.out.println("Failed to create .jit");
            return;
        }
        
        System.out.println("Created .jit");

        currDir = currDir + "/.jit";

        File indexFile = new File(currDir + "/index");

        File headDir = new File(currDir + "/HEAD");
        File objectsDir = new File(currDir + "/objects");
        File refsDir = new File(currDir + "/refs");

        try {
            boolean createdIndex = indexFile.createNewFile();
            if (createdIndex) {
                System.out.println("index file created.");
            }
            else {
                
            }
        } catch (IOException e) {
            System.out.println("index file failed to be created due to an IO Exception");
            System.out.println(e);
        }
        boolean createdHead = headDir.mkdir();
        boolean createdObjects = objectsDir.mkdir();
        boolean createdRefs = refsDir.mkdir();

        if (createdHead) {
            System.out.println("HEAD directory created.");
        }
        else {
            System.out.println("Failed to create HEAD directory.");
        }

        if (createdObjects) {
            System.out.println("objects directory created.");
        }
        else {
            System.out.println("Failed to create objects directory.");
        }

        if (createdRefs) {
            System.out.println("refs directory created.");
        }
        else{
            System.out.println("Failed to create refs directory.");
        }
    }
    public static void main(String[] args) {
        Init init = new Init();
        init.createDirStructure();   
    }
}