package com.ican.quartz;

import java.util.Date;

import com.ican.constant.ScheduleConstants;
import com.ican.entity.Task;
import com.ican.entity.TaskLog;
import com.ican.mapper.TaskLogMapper;
import com.ican.utils.ExceptionUtils;
import com.ican.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import static com.ican.constant.CommonConstant.FALSE;
import static jdk.nashorn.tools.Shell.SUCCESS;

/**
 * 抽象quartz调用
 *
 * @author ican
 */
@SuppressWarnings(value = "all")
public abstract class AbstractQuartzJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) {
        Task task = new Task();
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), task);
        try {
            before(context, task);
            doExecute(context, task);
            after(context, task, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, task, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param task     系统计划任务
     */
    protected void before(JobExecutionContext context, Task task) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param task     系统计划任务
     */
    protected void after(JobExecutionContext context, Task task, Exception e) {
        Date startTime = threadLocal.get();
        threadLocal.remove();
        final TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setTaskName(task.getTaskName());
        taskLog.setTaskGroup(task.getTaskGroup());
        taskLog.setInvokeTarget(task.getInvokeTarget());
        taskLog.setStartTime(startTime);
        taskLog.setEndTime(new Date());
        long runMs = taskLog.getEndTime().getTime() - taskLog.getStartTime().getTime();
        taskLog.setTaskMessage(taskLog.getTaskName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null) {
            taskLog.setStatus(FALSE);
            String errorMsg = StringUtils.substring(ExceptionUtils.getExceptionMessage(e), 0, 2000);
            taskLog.setErrorInfo(errorMsg);
        } else {
            taskLog.setStatus(SUCCESS);
        }
        // 写入数据库当中
        SpringUtils.getBean(TaskLogMapper.class).insert(taskLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param task     系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, Task task) throws Exception;
}