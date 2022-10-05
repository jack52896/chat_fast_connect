package pool.exector;

import pool.queue.BlockQueue;

/**
 * @author yujie
 * @createTime 2022/10/5 18:56
 * @description
 */
public class ConnectionRunnable implements Runnable{

    private BlockQueue blockQueue;

    private Runnable runnable;

    public ConnectionRunnable(BlockQueue blockQueue, Runnable runnable) {
        this.blockQueue = blockQueue;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        while(runnable != null || (runnable = blockQueue.take()) != null){
            runnable.run();
            runnable = null;
        }
    }
}
