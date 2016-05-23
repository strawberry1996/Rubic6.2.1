package cn.edu.cqupt.net.handle;

import cn.edu.cqupt.net.connect.GetFileConnection;
import cn.edu.cqupt.net.connect.SendResultFileConnection;
import cn.edu.cqupt.rubic_core.config.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件传输处理类
 * Created by Vigo on 16/3/25.
 */
public class TransFileHandle {
    /**
     * 从文件系统中获取文件
     * @param file_path 数据/算法文件相对路径
     * @return
     */
    public String getFileFromFileSystem(String file_path, Configuration config){

        Map<String, Object> requestMap=new HashMap<String, Object>();

        requestMap.put("protocol_id","2");

        requestMap.put("file_path", file_path);

        GetFileConnection connection = new GetFileConnection();

        connection.sendGetFileRequest(requestMap);

        int index = file_path.lastIndexOf("\\");

        String file_name = file_path.substring(index + 1, file_path.length());

        connection.saveFile(config.getTMP_PATH(), file_name);

        String dataPath = config.getTMP_PATH() + File.separator + file_name;

        return dataPath;
    }

    /**
     * 将结果文件发送到FileSystem
     * @param result_path 结果文件相对路径
     * @param file_path 结果文件在本地绝对路径
     */

    public void sendResultFileToFileSystem(String result_path, String file_path){
        Map<String, Object> requestMap=new HashMap<String, Object>();

        requestMap.put("protocol_id","3");

        requestMap.put("file_path", result_path);

        SendResultFileConnection connection = new SendResultFileConnection();

        connection.handleResultFile(requestMap, file_path);
    }

    /**
     * 删除从文件系统中取得的文件
     * @param tmp_file
     */
    public void deleteTmpFile(String tmp_file){
        FileHandler fileHandler = new FileHandler();
        File tmpFile = new File(tmp_file);
        try {
            fileHandler.deleteFile(tmpFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
