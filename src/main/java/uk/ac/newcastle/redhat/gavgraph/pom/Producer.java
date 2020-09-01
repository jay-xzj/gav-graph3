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
 *
 * @author ctk
 * 生产者消费者模型
 */

public class Producer implements Runnable {
    static CountDownLatch cdl;
    static AtomicInteger ai = new AtomicInteger(0);

    private List<List<Map<String, Object>>> queue;
    private int len;
    private String type;
    private List<File> allPomFiles;
    private boolean flag = true;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Producer(List<List<Map<String, Object>>> queue, int len, String type, List<File> allPomFiles) {
        this.queue = queue;
        this.len = len;
        this.type = type;
        this.allPomFiles = allPomFiles;
    }

    @Override
    public void run() {
        try {

            List<Map<String, Object>> dataList = new ArrayList<>();
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
                List<Map<String, Object>> data = PomUtil2.extractData(pom, type);
                //i增加
                dataList.addAll(data);
                int size1 = dataList.size();

                if (1000_000 <= size1 || cdl.getCount() == 0) {
                    try {
                        Main2.lock.lock();
                        while (queue.size() == 1) Main2.empty.await();
                        int batchNo = ai.incrementAndGet();
                        queue.add(dataList);
                        Main2.full.signal();
                        if (queue.size() == len) {
                            System.out.println("生产者ID:" + Thread.currentThread().getId() + " 生产到了第" + batchNo + "批次");
                            dataList = null;
                            System.gc();
                            dataList = new ArrayList<>();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Main2.lock.unlock();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
