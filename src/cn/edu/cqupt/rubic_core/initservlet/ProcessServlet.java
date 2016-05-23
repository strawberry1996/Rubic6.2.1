package cn.edu.cqupt.rubic_core.initservlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 运行记录类（Tomcat自动加载）
 * Created by Vigo on 15/11/17.
 */
@WebServlet(name = "ProcessServlet")
public class ProcessServlet extends HttpServlet {

    public HashMap<String, Object> processHash;

    public ExecutorService runThreadPool;

    public void init() throws ServletException {
        ServletContext context = this.getServletContext();
        processHash = new HashMap<String,Object>();
        context.setAttribute("processHash", processHash);
        runThreadPool = Executors.newCachedThreadPool();
        context.setAttribute("threadPool", runThreadPool);
    }
}
