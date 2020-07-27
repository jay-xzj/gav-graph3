package uk.ac.newcastle.redhat.gavgraph.pom;


import uk.ac.newcastle.redhat.gavgraph.pom.util.WriteCsvTool2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer2 implements Runnable{
    static CountDownLatch cdl=new CountDownLatch(10);;
    static AtomicInteger ai=new AtomicInteger(0);
    final String downloadFilePath = "D:\\dev\\neo4j_data\\neo4jDatabases\\database-7665b2bb-35ee-4797-8011-6068d7c8edd4\\installation-3.5.14\\import\\";
    //final String downloadFilePath = "/Users/xuzhijie/Documents/small_poms";
    //final String downloadFilePath = "C:\\Users\\jayxu\\Desktop\\test";

    private List<List<Map<String,Object>>> queue;
    //与type有关的有csv的格式，表头，文件名
    private String type;
    private int len;
    private boolean flag = true;
    private int folderNum;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Consumer2(List<List<Map<String,Object>>> queue, int len, String type,int folderNum){
        this.queue = queue;
        this.len = len;
        this.type = type;
        this.folderNum = folderNum;
    }
    @Override
    public void run() {
        try {
            while (flag) {
                if (Thread.currentThread().isInterrupted())
                    break;
                try {
                    List<Map<String,Object>> data = null;
                    System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());
                    Main2.lock.lock();
                    System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());

                    if (queue.size() == 0 && !Main2.lastBatch) {
                        Main2.full.signalAll();
                        Main2.empty.await();
                    }

                    TimeUnit.SECONDS.sleep(1);
                    //取到queue里面的一个数据集进行消费，在我们这就是写到csv中啦
                    //队列里面的每个数据集的大小是10W,队列长队为10,那么都写到csv文件中就是100W条数据
                    if (!queue.isEmpty()) {
                        data = queue.remove(0);
                    } else {
                        //第二次校验，queue里面应该有数据集才对，如果没有就是生产者挂了
                        setFlag(false);
                        break;
                    }

                    // 导出文件名称
                    int batchNum = ai.incrementAndGet(); //这边的index是第几个10，也就是消耗完第几个队列了。
                    //cdl.countDown();
                    /*if (cdl.getCount() == 0){
                        idx = idx + ai.incrementAndGet();
                        cdl = new CountDownLatch(10);
                    }else{
                        idx = idx + ai.get();
                    }*/

                    List<Object> headList = null;
                    String fileName = null;
                    if (Constant.DEPEND_ON_ALL.equalsIgnoreCase(type)){
                        //每个数据集是10W条
                        String[] displayColNames = {"from","to"};
                        String[] fieldNames = {"from","to"};
                        //String[] fieldNames = {"from","to","groupId","artifactId","version"};
                        //headList = Arrays.asList(head);
                        fileName = "depend_"+batchNum;
                        //File csvFile = CSVUtils.createCSVFile(headList, data, downloadFilePath+"dep_"+folderNum, fileName);
                        WriteCsvTool2.writeCvs(downloadFilePath+"dep_"+folderNum+"\\"+fileName+".csv",data, displayColNames,fieldNames );
                    }else if (Constant.ARTIFACT_ALL.equalsIgnoreCase(type)){
                        //每个数据集是10W条
                        String[] displayColNames = {"gav","groupId","artifactId","version","packaging"};
                        String[] fieldNames = {"gav","groupId","artifactId","version","packaging"};
                        //headList = Arrays.asList(head);
                        fileName = "artifact_"+batchNum;
                        //File csvFile = CSVUtils.createCSVFile(headList, data, downloadFilePath+"art_"+folderNum, fileName);
                        WriteCsvTool2.writeCvs(downloadFilePath+"art_"+folderNum+"\\"+fileName+".csv",data, displayColNames,fieldNames );
                    }
                    //File csvFile = CSVUtils.createCSVFile(headList, data, downloadFilePath, fileName);
                    System.out.println("消费者ID:"+Thread.currentThread().getId()+" 消费到了第"+batchNum+"批次");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());
                    Main2.lock.unlock();
                    System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());
                }
                //System.out.println("消费者ID:"+Thread.currentThread().getId()+" 消费到了第"+(ai.get() * len + (10-cdl.getCount()))+"批次");

            }
            /*if (Main2.lastBatch == Boolean.TRUE){
                setFlag(false);
                break;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}