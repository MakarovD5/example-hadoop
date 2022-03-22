package cn.itcast.sentiment_upload.task;

import cn.itcast.sentiment_upload.arg.SentimentOptions;
import cn.itcast.sentiment_upload.dfs.HDFSMgr;
import cn.itcast.sentiment_upload.dfs.HDFSMgrImpl;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskMgr {
    protected static Logger logger = LogManager.getLogger(TaskMgr.class.getName());

    protected static final String COPY_STATUS = "_COPY";
    protected static final String DONE_STATUS = "_DONE";

    private HDFSMgr hdfsMgr;

    public TaskMgr(){
        hdfsMgr = new HDFSMgrImpl();
    }

    public void genTask(SentimentOptions options){
        //判断原始数据目录是否存在
        File sourceDir = new File(options.sourceDir);
        if (!sourceDir.exists()){
            String errorMsg = String.format("%s 采集数据目录不存在:", options.sourceDir);
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        //读取原始数据目录下的所有文件
        //lambda函数实现过滤器
        File[] listFiles = sourceDir.listFiles(f -> {
            String fileName = f.getName();
            if (fileName.startsWith("weibo_data_")) {
                return true;
            }
            return false;
        });

        //判断待上传目录是否存在，不存在则创建一个
        File tempDir = new File(options.pendingDir);
        if (!tempDir.exists()){
            try {
                FileUtils.forceMkdirParent(tempDir);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
                throw new RuntimeException(e.getMessage());
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuilder stringBuilder = new StringBuilder();

        // 创建任务目录（目录名称：task_年月日时分秒_任务状态
        File taskDir = null;
        if (listFiles!=null && listFiles.length>0){
            taskDir = new File(tempDir,String.format("task_%s", sdf.format(new Date())));
            taskDir.mkdir();
        }else {
            return;
        }

        // 遍历待上传的文件
        // 在待上传目录生成一个willDoing文件
        for (File dataFile : listFiles){
            File destFile = new File(taskDir,dataFile.getName());
            try {
                FileUtils.moveFile(dataFile,destFile);
            } catch (IOException e) {
//                e.printStackTrace();
                logger.error(e.getMessage(),e);
            }
            stringBuilder.append(destFile.getAbsoluteFile()+"\n");

        }
        // 将待移动的文件添加到willDoing文件中
        String taskName = String.format("willDoing_%s",sdf.format(new Date()));
        try {
            FileUtils.writeStringToFile(new File(tempDir,taskName),stringBuilder.toString(),"utf-8");
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
    }

    public void work(SentimentOptions options){
        File pendingDir = new File(options.pendingDir);
        File[] pendingTaskDir = pendingDir.listFiles(f->{
            String taskName = f.getName();
            if (!taskName.startsWith("willDoing")) return false;
            if (taskName.endsWith(COPY_STATUS)||taskName.endsWith(DONE_STATUS)){
                return false;
            }

            return true;
        });

        for (File pendingFile : pendingTaskDir){
            try {
                // 将任务文件修改为_COPY，表示正在处理中
                File copyTaskFile = new File(pendingFile.getAbsolutePath()+"_"+COPY_STATUS);
                FileUtils.moveFile(pendingFile,copyTaskFile);

                // 获取任务的日期
                String taskDate = pendingFile.getName().split("_")[1];
                String dataPathInHDFS = options.output+String.format("/%s", taskDate);

                // 判断HDFS目标上传目录是否存在，不存在则创建
                hdfsMgr.mkdir(dataPathInHDFS);

                // 读取任务文件
                String tasks = FileUtils.readFileToString(copyTaskFile, "utf-8");

                //按照换行符切分
                String[] taskArray = tasks.split("\n");

                //上传每一个文件
                for (String task : taskArray){
                    hdfsMgr.put(task,dataPathInHDFS);
                }

                //上传成功后，将COPY后缀修改为_DONE
                File doneTaskFile = new File(pendingFile.getAbsolutePath()+"_"+DONE_STATUS);
                FileUtils.moveFile(copyTaskFile,doneTaskFile);


            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }

        }

    }



}
