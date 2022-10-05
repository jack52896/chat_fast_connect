package pool;

import lombok.extern.slf4j.Slf4j;
import pool.exector.ConnectionRunnable;
import pool.queue.BlockQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yujie
 * @createTime 2022/10/5 18:52
 * @description 数据库连接池
 */
@Slf4j
public class DataSourcePool {

    private int maxSize;

    private int coreSize;

    private int capacity;

    private BlockQueue blockQueue;

    private Lock lock = new ReentrantLock();

    private List<ConnectionRunnable> workThreadList = new LinkedList<>();

    public DataSourcePool(int maxSize, int coreSize, int capacity) {
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        this.capacity = capacity;
        blockQueue = new BlockQueue(capacity);
    }

    public void submit(Runnable runnable){
        try{
            lock.lock();
            if(workThreadList.size() < coreSize){
                blockQueue.put(runnable);
                ConnectionRunnable connectionRunnable = new ConnectionRunnable(blockQueue, runnable);
                workThreadList.add(connectionRunnable);
                new Thread(connectionRunnable).start();
            }
        }catch (Exception e){
            log.error(e.getClass().getSimpleName(), e);
        }finally {
            lock.unlock();
        }
    }


}
