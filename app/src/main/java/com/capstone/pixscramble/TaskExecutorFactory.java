package com.capstone.pixscramble;


import android.util.Log;

import java.util.ArrayList;


public class TaskExecutorFactory {

    private static final String TAG = "TaskExecutorFactory";
    /** Single instance of task executor*/
    private static TaskExecutorFactory sInstance = new TaskExecutorFactory();

    /* Cached list of task executors */
    private ArrayList<TaskExecutor> listExecutor = new ArrayList<TaskExecutor>();
    /* Private constructor*/
    private TaskExecutorFactory()
    {

    }

    private static final int MAX_EXECUTOR_SIZE = 6;
    /* Public method to get instance of TaskExecutorFactory*/
    public static TaskExecutorFactory getInstance()
    {
        return sInstance;
    }

    /**
     * Caller invokes this function to get instance of task executor and perfom
     * the actions
     * Caller should not free the task executor as it can be reused
     * @return TaskExecutor instance on which the caller can run their runnables
     *
     */
    public synchronized TaskExecutor getTaskExecutor()
    {
        TaskExecutor te = null;
       if ( listExecutor.size() > 0)
       {
            for (int i = 0 ; i< listExecutor.size() ; i++)
            {
                te = listExecutor.get(i);
                if (te!=null && te.getAvailability() == true)
                {

                    Log.d(TAG,"TaskExecutorFactory: Returning from cached executors !! index : "+ i);
                    return te;
                }
            }

            //It means all our current task executors are busy
           te = new TaskExecutor();
           if (listExecutor.size() < MAX_EXECUTOR_SIZE)
           {
               Log.d(TAG,"TaskExecutorFactory: Adding new executor to list Size :" +(listExecutor.size()+1) );
               listExecutor.add(te);
           }
           else
           {
               // we dont want to keep cache size more than 6 rather we create new task executor and
               //give it to end user
               Log.d(TAG,"Cache limit exceeded so we are not caching further!! returning new instance");
           }
       }
        else
       {
           te = new TaskExecutor();
           listExecutor.add(te);
       }
        return  te;
    }






}
