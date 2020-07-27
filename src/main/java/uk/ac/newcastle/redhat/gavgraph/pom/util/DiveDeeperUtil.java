package uk.ac.newcastle.redhat.gavgraph.pom.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiveDeeperUtil {

    public static List<File> getDeeper3LevelFiles(String rootPath) {
        File file = new File(rootPath);
        File[] files = file.listFiles();
        List<File> l3SubFiles = new ArrayList<>();
        for (File fi : files) {
            if (fi.isDirectory()) {
                File[] fs = fi.listFiles();
                if (fs != null) {
                    for (File f : fs) {
                        if (f.isDirectory()) {
                            File[] fs1 = f.listFiles();
                            if (fs1 != null) {
                                for (File f1 : fs1) {
                                    if (f1.isDirectory()) {
                                        l3SubFiles.add(f1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return l3SubFiles;
    }
}
