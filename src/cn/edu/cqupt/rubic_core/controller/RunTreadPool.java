package cn.edu.cqupt.rubic_core.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 线程池类
 * Created by Vigo on 16/3/1.
 */
public class RunTreadPool {

    public ExecutorService runThreadPool;

    public RunTreadPool(ExecutorService runThreadPool){
        this.runThreadPool = runThreadPool;
    }

    public void excuteTread(Runnable runTread){
        runThreadPool.execute(runTread);
    }
}
