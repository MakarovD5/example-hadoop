package cn.itcast.sentiment_upload.arg;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class SentimentOptions extends OptionsBase {
    @Option(
            name = "help",
            abbrev = 'h',
            help = "打印帮助信息",
            defaultValue = "true"
    )
    public boolean help;

    @Option(
            name = "source",
            abbrev = 's',
            help = "采集数据位置",
            defaultValue = ""
    )
    public String sourceDir;

    @Option(
            name = "pending",
            abbrev = 'p',
            help = "待上传目录",
            defaultValue = "/tep/pending/sentiment"
    )
    public String pendingDir;

    @Option(
            name = "output",
            abbrev = 'o',
            help = "要上传的HDFS路径",
            defaultValue = ""
    )
    public String output;
}
