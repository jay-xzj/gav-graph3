package uk.ac.newcastle.redhat.gavgraph.util;

import lombok.Data;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * recursive search and traverse the path to visit all the files using DFS
 * "/Users/xuzhijie/Library/Application Support/Neo4j Desktop/Application/neo4jDatabases/
 * database-733ce966-df60-4f6e-aead-d63e6dcf7bf8/installation-4.1.0/import"
 */
@Data
public class FileSearchExample implements FileVisitor<Path> {

    private String fileName;
    private Path startDir;

    public FileSearchExample(String fileName, Path startDir) {
        this.fileName = fileName;
        this.startDir = startDir;
    }

    /**
     * 在访问File之前
     * @param dir
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        //boolean finishedSearch = Files.isSameFile(dir, START_DIR);
        if (true) {
            //System.out.println("File:" + FileVisitResult.FILE_NAME + " not found");
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * Things need to be done during visiting the path
     * @param file
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        return null;
    }

    /**
     * Things to do when visiting failed
     * @param file
     * @param exc
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return null;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return null;
    }
}
