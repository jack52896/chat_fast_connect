package pool.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yujie
 * @createTime 2022/10/5 18:56
 * @description 连接阻塞队列
 */
@Slf4j
public class BlockQueue {

    //最大队列长度
    private int capacity;

    private Lock lock = new ReentrantLock();

    private Condition put = lock.newCondition();

    private Condition take = lock.newCondition();

    private LinkedList<Runnable> sets = new LinkedList<>();

    public BlockQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(Runnable runnable){
        try{
            lock.lock();
            while(sets.size() == capacity){
                take.signalAll();
                put.await();
            }
            sets.add(runnable);
        }catch (Exception e){
            log.error("将任务加入队列失败，异常信息:{}", e.getClass().getSimpleName(), e);
        }finally {
            lock.unlock();
        }
    }

    public Runnable take(){
        try{
            lock.lock();
            while(sets.isEmpty()){
                put.signal();
                take.await();
            }
            return sets.removeFirst();
        }catch (Exception e){
            log.error("将任务拿出队列失败，异常信息:{}", e.getClass().getSimpleName(), e);
        }finally {
            lock.unlock();
        }
        return null;
    }

}
