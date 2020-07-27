package uk.ac.newcastle.redhat.gavgraph.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSearchTest {

    public static void main(String[] args) throws IOException {
        Path start = Paths.get("/Users/xuzhijie/Library/Application Support/Neo4j Desktop/Application/neo4jDatabases/" +
                "database-733ce966-df60-4f6e-aead-d63e6dcf7bf8/installation-4.1.0");
        String fileToSearch = "release_all.csv";
        FileSearchExample fileSearchExample = new FileSearchExample(fileToSearch,start);
        Path path = Files.walkFileTree(start, fileSearchExample);
        System.out.println(path);
    }

}
