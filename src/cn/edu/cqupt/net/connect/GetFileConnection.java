package cn.edu.cqupt.net.connect;

import cn.edu.cqupt.net.handle.FileHandler;
import cn.edu.cqupt.net.protocol.GetFileProtocol;
import cn.edu.cqupt.rubic_core.config.Configuration;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * 数据集、算法文件获取连接类
 * Created by Vigo on 16/3/4.
 */
public class GetFileConnection {

    Socket socket;

    public GetFileConnection(){
        try {
             socket =  new Socket(Configuration.SERVER_IP, Configuration.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存文件
     * @param tmpPath 临时文件存放目录
     * @param file_name 文件名
     */
    public void saveFile(String tmpPath, String file_name){

        File file = null;
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            //建立临时文件存放目录
            FileHandler fileHandler = new FileHandler();

            fileHandler.createFile(tmpPath);

            //写文件
            file = new File(tmpPath + File.separator + file_name);
            FileOutputStream fos =new FileOutputStream(file);

            byte[] sendBytes =new byte[1024];

            while(true){
                int read = 0;
                read = dis.read(sendBytes);
                if(read == -1)
                    break;

                fos.write(sendBytes,0, read);
                fos.flush();
            }
            fos.close();
            socket.close();
        } catch (IOException e) {

            if (file != null){
                file.delete();
            }
            e.printStackTrace();
        }

    }

    /**
     * 发送获取文件请求
     * @param requestMap 请求参数
     */
    public void sendGetFileRequest(Map<String, Object> requestMap){

        //构造请求协议
        GetFileProtocol protocol = new GetFileProtocol();
        String request = protocol.creatResquest(requestMap);
        try {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            out.write(request.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<process>\r\n" +
                "        <type>request</type>\r\n" +
                "        <protocol>\r\n" +
                "            <protocol_id>2</protocol_id>\r\n" +
                "            <platform>java</platform>\r\n" +
                "            <type>1</type>\r\n" +
                "            <file_path>/Users/lwg/iris.data</file_path>\r\n" +
                "        </protocol>\r\n" +
                "</process>\r\n";

        new GetFileConnection();
    }
}
