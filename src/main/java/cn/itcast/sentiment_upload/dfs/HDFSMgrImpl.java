package cn.itcast.sentiment_upload.dfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HDFSMgrImpl implements HDFSMgr {
    Logger logger = LogManager.getLogger(HDFSMgrImpl.class.getName());

    private Configuration configuration;
    private FileSystem fileSystem;

    public HDFSMgrImpl(){
        configuration = new Configuration();
        try {
            fileSystem = FileSystem.get(configuration);
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> ls(String path, boolean recursion) {

        try {
            RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(new Path(path), recursion);
            ArrayList<String> list = new ArrayList<>();

            while (iterator.hasNext()){
                LocatedFileStatus fileStatus = iterator.next();
                list.add(fileStatus.getPath().toString());
            }

            return list;
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
//        return null;
    }

    @Override
    public void put(String src, String dest) {
        try {
            fileSystem.copyFromLocalFile(false,true,new Path(src), new Path(dest));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void get(String src, String dest) {
        try {
            fileSystem.copyToLocalFile(new Path(src),new Path(dest));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mkdir(String path) {
        try {
            if (fileSystem.exists(new Path(path))){
                return;
            }
            fileSystem.mkdirs(new Path(path));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            fileSystem.close();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }
}
