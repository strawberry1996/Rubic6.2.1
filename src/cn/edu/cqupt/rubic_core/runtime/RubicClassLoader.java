package cn.edu.cqupt.rubic_core.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import cn.edu.cqupt.rubic_core.config.Configuration;


/**
 * 
 * @ClassName: RubicClassLoader
 * @Description: TODO
 * @author Vigo
 * @date 2015-5-18 上午10:07:45
 */
public class RubicClassLoader {

	public URLClassLoader classLoader = null;

	public Class<?> myClass;


	/**
	 *初始化类加载器
	 * @param path jar包路径
	 * @throws MalformedURLException
	 */
	public RubicClassLoader(String path) throws MalformedURLException   {
		// 设定类加载路径
		File file = new File(path);
		URL url = file.toURI().toURL();
		classLoader = new URLClassLoader(new URL[]{url}, Thread
					.currentThread().getContextClassLoader());
		// 初始化class对象
		myClass = classLoader.getClass();
	}


	/**
	 * 指定加载类，获取该类对象
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Object getInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException   {
		myClass = classLoader.loadClass(className);
		Object obj = myClass.newInstance();
		return obj;
	}

	/**
	 * 根据方法名获取Methond对象
	 * @param methodName
	 * @return
	 */
	public Method getMethod(String methodName) {
		Method method = null;
		try {
			method = myClass.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}

	/**
	 * 释放资源
	 */
	public void release(){
		try {
			classLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 测试方法---->通过
	public static void main(String[] args) {

//		RubicClassLoader c = new RubicClassLoader("lwg3.jar");
//		Object o = c.getInstance("ta.TestA");
//		Method m = c.getMethod("action");
//		try {
//			m.invoke(o, null);
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
