package uk.ac.newcastle.redhat.gavgraph.pom;


import uk.ac.newcastle.redhat.gavgraph.pom.util.PomUtil2;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者
 * @author ctk
 * 生产者消费者模型
 */

public class Producer2 implements Runnable{
    static CountDownLatch cdl;
    static AtomicInteger ai=new AtomicInteger(0);

    private List<List<Map<String,Object>>> queue;
    private int len;
    private String type;
    private List<File> allPomFiles;
    private boolean flag = true;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Producer2(List<List<Map<String,Object>>> queue, int len, String type, List<File> allPomFiles){
        this.queue = queue;
        this.len = len;
        this.type = type;
        this.allPomFiles = allPomFiles;
    }
    @Override
    public void run() {
        try {
            while (flag) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                List<Map<String,Object>> dataList = new ArrayList<>();
                Iterator<File> iterator = allPomFiles.iterator();
                cdl = new CountDownLatch(allPomFiles.size());
                int i = 0;

                //只要pomFiles没有遍历完，就一直遍历
                while (iterator.hasNext()) {
                    System.err.println("++++++++++当前处理到第:" + (++i) + "个文件++++++++++");
                    //每处理一个文件countDown一次。
                    cdl.countDown();

                    File pom = iterator.next();
                    //拿到pom里面的数据
                    List<Map<String,Object>> data = PomUtil2.extractData(pom, type);
                    //i增加
                    dataList.addAll(data);
                    int size1 = dataList.size();
                    System.err.println("~~~~~~~~~dataList.size = " + size1 + "~~~~~~~~~");

                    //int batchNo = ai.incrementAndGet();
                    //int batchNo = 0;
                    //Thread.sleep(100);
                    //到了100W条写出去一次
                    //生成的文件的长度，应该是2*queue的大小
                    //这边拿到的数据可能不会正好是batchsize的倍数，所以当正好是1倍的时候就加进队列

                    if (1000_000 <= size1 || cdl.getCount() == 0) {
                        //System.out.println("第{"+(i/1000_000)+"}批数据");
                        //dataList.addAll(data);
                        try {
                            System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());
                            Main2.lock.lock();
                            System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());
                            int batchNo = ai.incrementAndGet();
                            queue.add(dataList);
                            if (queue.size() >= len) {
                                System.out.println("生产者ID:" + Thread.currentThread().getId() + " 生产到了第" + batchNo + "批次");
                                if (cdl.getCount() == 0) {
                                    Main2.lastBatch = Boolean.TRUE;
                                }
                                Main2.empty.signalAll();
                                Main2.full.await();
                            }
                            TimeUnit.SECONDS.sleep(1);
                            //queue.add(dataList);
                            dataList = null;
                            System.gc();
                            dataList = new ArrayList<>();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }finally{
                            System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());
                            Main2.lock.unlock();
                            System.out.println("+++++++++++++++++当前是否上锁：" + Main2.lock.isLocked());
                        }
                        //因为这边的数量是不确定的，无法确定具体有多少批次，所以没法事先算出countDownLatch的countdown次数
                        //cdl.countDown();
                        //batchNo = ai.incrementAndGet();
                    /*if (cdl.getCount()==0){
                        cdl.await();
                    }*/
                        //开启Consumer线程去执行写入


                        //cdl.await();
                    }

                }
                    /*if (batchNo>0 && cdl.getCount()==0){
                        Main2.lastBatch = Boolean.TRUE;
                        //return;
                    }*/
            }
            if (cdl.getCount() == 0) {
                //Main2.lastBatch = Boolean.TRUE;
                setFlag(false);
                //break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
