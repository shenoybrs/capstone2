package com.capstone.offerbank;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

/**
 * Utility for executing tasks (Runnables) in a background thread. Currently
 * support only synchronous execution (still in bg thread).
 */
public class TaskExecutor implements Executor {

    private static final String TAG = "TaskExecutor" ;
    // / Max count 1, enforce fair lock order true (mutex == semaphore with max
    // count 1).
    private final Semaphore mTaskMutex = new Semaphore(1, true);
    private TaskThread mTaskThread;


    /**
     * Method to check if the current task executor can be used or not
     * @return - if semaphore is available , here we are using binary semaphore so
     * there is only one lock possible
     *
     */
    public boolean getAvailability()
    {
        return mTaskMutex.availablePermits() > 0 ? true : false;
    }

    /**
     * Background thread for executing tasks.
     */
    private class TaskThread extends Thread {
        private static final String TAG = "TaskThread";
        private Handler mHandler;
        private final Semaphore mMutex = new Semaphore(1, true);

        /**
         * Constructor. Waits until the event loop is running before return.
         */
        public TaskThread() throws InterruptedException {
            // Acquire for waiting of task thread event loop start, see run()

            mMutex.acquire();
            start();
            waitForTask();
        }

        @Override
        public void run() {
            Looper.prepare();

            mHandler = new Handler(Looper.myLooper());

            // Release initial mutex lock (by ctor) from within the event loop
            // to ensure the ctor exists after the event loop is running.
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mMutex.release();
                }
            });

            Looper.loop();
        }

        /**
         * Post task to be executed on this thread.
         */
        public synchronized void postTask(Runnable task) {
            mHandler.post(task);
        }

        /**
         * Post quit message. The event loop will terminate and Thread.run()
         * will return when this task is executed.
         */
        @SuppressWarnings("unused")
        public void postQuit() {
            postTask(new Runnable() {
                @Override
                public void run() {
                    Looper.myLooper().quit();
                }
            });
        }

        private void waitForTask() {
            try {
                // The mutex is locked (at count 1) when a task is executing.
                // The mMutex has max count of 1.
                // Wait (if necessary) for lock, release immediately.
                mMutex.acquire();
                mMutex.release();
            } catch (InterruptedException ex) {
                Log.e(TAG, "Waiting for task failed");
            }
        }
    }

    /**
     * Constructor. Waits until ready for receiving tasks.
     */
    public TaskExecutor() {
        try {
            mTaskThread = new TaskThread();
        } catch (InterruptedException ex) {
            Log.e(TAG, "Failed to start");
        }
    }

    /**
     * Executes the task in background thread synchronously. The point here is
     * not perf, but to make it easier to handle network requests, which must
     * always be on a background thread.
     */
    @Override
    public void execute(final Runnable task) {
        try {
            // Acquire mutex, possibly waits for previous task here.
            mTaskMutex.acquire();
            mTaskThread.postTask(new Runnable() {
                @Override
                public void run() {
                    // Run the client supplied task
                    task.run();
                    // Release for acquire in the execute() 1st line
                    mTaskMutex.release();
                }
            });

            waitForTask();
        } catch (InterruptedException ex) {
            Log.d(TAG, "Task failed");
        }
    }

    /**
     * Wait for any previous task execution to complete. Returns quickly if no
     * task is executing.
     */
    private void waitForTask() {
        try {
            // The mutex is locked (at count 1) when a task is executing.
            // The mTaskMutex has max count of 1.
            // Wait (if necessary) for lock, release immediately.
            mTaskMutex.acquire();
            mTaskMutex.release();
        } catch (InterruptedException ex) {
            Log.e(TAG, "Waiting for task failed");
        }
    }

}
