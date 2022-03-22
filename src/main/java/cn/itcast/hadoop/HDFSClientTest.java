package cn.itcast.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HDFSClientTest {
    private Configuration configuration = null;
    private FileSystem fileSystem = null;

    @Before
    public void connect2HDFS() throws IOException {
        System.setProperty("HADOOP_USER_NAME","root");
        configuration = new Configuration();
        configuration.set("fs.defaultFS","hdfs://node1.itcast.cn:8020");
        fileSystem = FileSystem.get(configuration);
    }

    @Test
    public void mkdir() throws IOException {
        if (!fileSystem.exists(new Path("/itheima"))){
            fileSystem.mkdirs(new Path("/itheima"));
        }
    }

    @Test
    public void put() throws IOException {
        Path src = new Path("D:\\QQ CACHE\\1773534412\\FileInfo.db");
        Path dst = new Path("/itheima/FileInfo.db");
        fileSystem.copyFromLocalFile(src,dst);
    }

    @Test
    public void get() throws IOException {
        Path src = new Path("/itheima/FileInfo.db");
        Path dst = new Path("D:\\QQ CACHE\\hd.db");
        fileSystem.copyToLocalFile(src,dst);
    }

    @After
    public void closeConnect(){
        if (fileSystem!=null){
            try {
                fileSystem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
