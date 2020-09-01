package uk.ac.newcastle.redhat.gavgraph.pom;


import uk.ac.newcastle.redhat.gavgraph.pom.util.DiveDeeperUtil;
import uk.ac.newcastle.redhat.gavgraph.pom.util.PomUtil2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main2 {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition empty = lock.newCondition();
    public static Condition full = lock.newCondition();
    public static Boolean lastBatch = Boolean.FALSE;

    public static void main(String[] args) throws InterruptedException {
        ///Thread.sleep(2000);

        //final List<File> files1 = DiveDeeperUtil.getDeeper3LevelFiles("C:\\Users\\jayxu\\Desktop\\test");
        //final List<File> files2 = DiveDeeperUtil.getDeeper3LevelFiles(Constant.ROOT_PATH_2);
        final List<File> files1 = DiveDeeperUtil.getDeeper3LevelFiles("E:\\poms1");
        generate(files1,1);
       // generate(files2,2);
        //System.gc();
        //generate(files1,1);
        /*System.gc();*/
        //final List<File> files = DiveDeeperUtil.getDeeper3LevelFiles("E:\\small_poms");
        //final List<File> files = DiveDeeperUtil.getDeeper3LevelFiles("C:\\Users\\jayxu\\Desktop\\test");
        //final List<File> files = DiveDeeperUtil.getDeeper3LevelFiles("/Users/xuzhijie/Documents/small_poms");

    }

    public static void generate(List<File> files,int folderNum) throws InterruptedException {
        List<File> allPomFiles = new ArrayList<>();
        for (File file : files) {
            List<File> pomFiles = new ArrayList<>();
            PomUtil2.recur(file,pomFiles);
            allPomFiles.addAll(pomFiles);
        }
        System.out.println("allPomFiles.size = "+allPomFiles.size());

        List<List<Map<String,Object>>> queue = new ArrayList<>();
        int length = 1;
        //Producer2 p1 = new Producer2(queue,length, Constant.DEPEND_ON_ALL,allPomFiles);
        //Consumer2 c1 = new Consumer2(queue,length,Constant.DEPEND_ON_ALL);
        Producer p1 = new Producer(queue,length, Constant.ARTIFACT_ALL,allPomFiles);
        Consumer c1 = new Consumer(queue,length,Constant.ARTIFACT_ALL,folderNum);
        //Thread pt1 = new Thread(p1,"P1");
        //Thread ct1 = new Thread(c1, "C1");
        //ExecutorService service = Executors.newCachedThreadPool();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.execute(p1);
        Thread.sleep(100);
        service.execute(c1);
        //service.shutdown();
        if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
            service.shutdownNow();
            if (!service.awaitTermination(20,TimeUnit.SECONDS))
                service.shutdownNow();
        }
    }
}