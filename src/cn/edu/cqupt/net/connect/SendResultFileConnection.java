package cn.edu.cqupt.net.connect;

import cn.edu.cqupt.net.protocol.GetFileProtocol;
import cn.edu.cqupt.rubic_core.config.Configuration;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * 结果集发送到FileSystem类
 * Created by Vigo on 16/3/10.
 */
public class SendResultFileConnection {

    Socket socket;

    public SendResultFileConnection(){
        try {
            socket =  new Socket(Configuration.SERVER_IP, Configuration.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理发送结果集文件
     * @param requestMap 请求参数
     * @param filePath 结果集文件绝对路径
     */
    public void handleResultFile(Map<String, Object> requestMap, String filePath){

        //发送协议
        sendRequest(requestMap);
        //收取
        try {
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            byte[] bytes = new byte[256];

            //接收数据长度
            int count = inputStream.read(bytes);

            //收到响应数据
            String response = new String(bytes, 0, count);

            if (response.equals("success")){

                //发送文件
                sendFile(socket, new File(filePath));
            }
            //关闭流
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送请求
     * @param requestMap
     */
    public void sendRequest(Map<String, Object> requestMap){

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

    /**
     * 发送文件
     * @param socket
     * @param file
     */
    private void sendFile(Socket socket, File file){
        FileInputStream fis = null;
        DataOutputStream dos = null;
        try {
            fis = new FileInputStream(file);
            dos = new DataOutputStream(socket.getOutputStream());

            //传输文件
            byte[] sendBytes =new byte[1024];
            int length = 0;
            while((length = fis.read(sendBytes,0, sendBytes.length)) >0){
                dos.write(sendBytes,0, length);
                dos.flush();
            }
            //关闭流
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        }
    }
}
