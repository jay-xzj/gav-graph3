package uk.ac.newcastle.redhat.gavgraph.pom;


import sun.applet.Main;
import uk.ac.newcastle.redhat.gavgraph.pom.util.WriteCsvTool2;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    static CountDownLatch cdl = new CountDownLatch(10);
    ;
    static AtomicInteger ai = new AtomicInteger(0);
    final String downloadFilePath = "D:\\dev\\neo4j_data\\neo4jDatabases\\database-7665b2bb-35ee-4797-8011-6068d7c8edd4\\installation-3.5.14\\import\\";
    //final String downloadFilePath = "/Users/xuzhijie/Documents/small_poms";
    //final String downloadFilePath = "C:\\Users\\jayxu\\Desktop\\test";

    private List<List<Map<String, Object>>> queue;
    //与type有关的有csv的格式，表头，文件名
    private String type;
    private int len;
    private boolean flag = true;
    private int folderNum;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Consumer(List<List<Map<String, Object>>> queue, int len, String type, int folderNum) {
        this.queue = queue;
        this.len = len;
        this.type = type;
        this.folderNum = folderNum;
    }

    @Override
    public void run() {
        try {
            List<Map<String, Object>> data = null;
            Main2.lock.lock();
            while (queue.size() == 0) Main2.full.await();

            data = queue.remove(0);
            // 导出文件名称
            int batchNum = ai.incrementAndGet(); //这边的index是第几个10，也就是消耗完第几个队列了。
            List<Object> headList = null;
            String fileName = null;
            if (Constant.DEPEND_ON_ALL.equalsIgnoreCase(type)) {
                //每个数据集是10W条
                String[] displayColNames = {"from", "to"};
                String[] fieldNames = {"from", "to"};
                fileName = "depend_" + batchNum;
                WriteCsvTool2.writeCvs(downloadFilePath + "dep_" + folderNum + "\\" + fileName + ".csv", data, displayColNames, fieldNames);
            } else if (Constant.ARTIFACT_ALL.equalsIgnoreCase(type)) {
                //每个数据集是10W条
                String[] displayColNames = {"gav", "groupId", "artifactId", "version", "packaging"};
                String[] fieldNames = {"gav", "groupId", "artifactId", "version", "packaging"};
                fileName = "artifact_" + batchNum;
                WriteCsvTool2.writeCvs(downloadFilePath + "art_" + folderNum + "\\" + fileName + ".csv", data, displayColNames, fieldNames);
            }
            System.out.println("消费者ID:" + Thread.currentThread().getId() + " 消费到了第" + batchNum + "批次");

            Main2.empty.signal();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            Main2.lock.unlock();
        }
    }

}