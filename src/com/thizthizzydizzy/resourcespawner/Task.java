package com.thizthizzydizzy.resourcespawner;
public interface Task<T>{
    public String getName();
    /**
     * Run one step of the task. Steps should be as small as possible to allow smooth operation.
     */
    public void step();
    /**
     * Checks whether or not the task has finished
     * @return true if the task has finished
     */
    public boolean isFinished();
    /**
     * Gets the result of the task
     * @return the task result
     */
    public T getResult();
}