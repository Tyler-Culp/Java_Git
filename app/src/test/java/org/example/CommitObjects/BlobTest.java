package org.example.CommitObjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class BlobTest {
    File file = new File("src/test/resources/ObjectTests/BlobTests/testFile.txt");
    @Test void canCreateBlob() {
        Blob blob = new Blob(file);
        assertEquals(file, blob.file);
    }
    @Test void canCreateObjectString() {
        Blob blob = new Blob(file);
        String expectedObjectString = "blob " + Long.toString(file.length()) + "\n";
        expectedObjectString += "apple\nbanana\npear\ngrape\nwatermelon\n";
        assertEquals(expectedObjectString, blob.objectString);
    }
    @Test void canCreateHash(){
        Blob blob = new Blob(file);
        String expectedHash = "78f458115efd9dd009986210704c2074622b11db";
        assertEquals(expectedHash, blob.hash);
    }
}
