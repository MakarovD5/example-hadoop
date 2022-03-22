package cn.itcast.sentiment_upload.dfs;

import java.util.List;

public interface HDFSMgr {
    List<String> ls(String path, boolean recursion);

    void put(String src, String dest);

    void get(String src, String dest);

    /**
     * 创建文件夹
     * @param path 文件夹路径
     */
    void mkdir(String path);

    void close();
}
