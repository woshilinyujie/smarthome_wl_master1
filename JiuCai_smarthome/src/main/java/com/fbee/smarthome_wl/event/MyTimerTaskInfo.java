package com.fbee.smarthome_wl.event;

import com.fbee.zllctl.TaskTimerAction;

/**
 * 场景定时任务联动
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/27 19:27
 */
public class MyTimerTaskInfo {
    private String taskName;
    private TaskTimerAction timerAction;
    private short sceneId;

    public MyTimerTaskInfo(String taskName, TaskTimerAction timerAction, short sceneId) {
        this.taskName = taskName;
        this.timerAction = timerAction;
        this.sceneId = sceneId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TaskTimerAction getTimerAction() {
        return timerAction;
    }

    public void setTimerAction(TaskTimerAction timerAction) {
        this.timerAction = timerAction;
    }

    public short getSceneId() {
        return sceneId;
    }

    public void setSceneId(short sceneId) {
        this.sceneId = sceneId;
    }
}
