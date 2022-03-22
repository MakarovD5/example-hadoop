package cn.itcast.sentiment_upload;

import cn.itcast.sentiment_upload.arg.SentimentOptions;
import cn.itcast.sentiment_upload.task.TaskMgr;
import com.google.devtools.common.options.OptionsParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;

public class Entrance {
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(Entrance.class.getName());
//        String name = logger.getName();
//        System.out.println(name);


        OptionsParser parser = OptionsParser.newOptionsParser(SentimentOptions.class);
        parser.parseAndExitUponError(args);
        SentimentOptions options = parser.getOptions(SentimentOptions.class);

        if (options.sourceDir.isEmpty()||options.output.isEmpty()){
            printUsage(parser);
            return;
        }

        logger.info("舆情上报程序启动...");
        TaskMgr taskMgr = new TaskMgr();

        logger.info("采集数据，生成上传任务");
        taskMgr.genTask(options);

        logger.info("正在上传数据到HDFS");
        taskMgr.work(options);
        logger.info("DONE");
    }

    public static void printUsage(OptionsParser parser){
        System.out.println("Usage: java -jar sentiment.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String,String>emptyMap(),OptionsParser.HelpVerbosity.LONG));
    }
}
