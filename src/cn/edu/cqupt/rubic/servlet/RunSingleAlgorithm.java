package cn.edu.cqupt.rubic.servlet;

import cn.edu.cqupt.rubic_core.controller.*;
import cn.edu.cqupt.rubic_core.protocol.HadoopProtocol;
import cn.edu.cqupt.rubic_core.protocol.JavaProtocol;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 运行一个算法，添加线程池版本
 * Created by Vigo on 16/3/2.
 */
public class RunSingleAlgorithm extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("error type");
        out.flush();
        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 解析协议（内部协议xml，java版本Run协议）

        BufferedReader br = request.getReader();

        //获取platformInfo
        String platformInfo = br.readLine();
        System.out.println("platformInfo: "+platformInfo);

        String[] str = platformInfo.split(":");
        String platform = null;
        if(str[0].equalsIgnoreCase("platform")){
            platform = str[1];
        }
        System.out.println("platform   ---->>>>>  "+platform);

        // 获取body
        String bodyContent = "";
        String tempString = "";
        try {
            while ((tempString = br.readLine()) != null) {
                bodyContent += tempString;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }

        // 构造协议
        String reponseProtocol = null;

        RunTreadPool pool =  new RunTreadPool((ExecutorService) getServletContext().getAttribute("threadPool"));

        // 运行
        if ("\"java\"".equalsIgnoreCase(platform)
                || "java".equalsIgnoreCase(platform)) {
            //解析
            JavaProtocol protocol = new JavaProtocol();
            Map<String, Object> requestMap =  protocol .parserRequest(bodyContent);
            //提交到线程池
            RunThread runThread = new RunThread(getServletContext(), requestMap);

            pool.excuteTread(runThread);
            //返回协议
            reponseProtocol = protocol.creatResponse();
        }else{
            //解析
            HadoopProtocol protocol = new HadoopProtocol();
            Map<String, Object> requestMap =  protocol .parserRequest(bodyContent);
            //提交到线程池
            RunOnHadoopThread runOnHadoopThread = new RunOnHadoopThread(getServletContext(), requestMap);
            pool.excuteTread(runOnHadoopThread);
            //返回协议
            reponseProtocol = protocol.creatResponse();
        }

        // 返回结果
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(reponseProtocol);
        out.flush();
        out.close();
    }

}