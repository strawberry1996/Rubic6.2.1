package cn.edu.cqupt.net.handle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @description 文件处理类
 * @author Wong JW
 * @date 2015年6月3日 下午1:06:06
 * @version 2.0
 */
public class FileHandler {

	/**
	 * @description 创建文件夹
	 * @param path 路径
	 * @return
	 */
	public File createFile(String path) {
		File file = new File(path);
		if(!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * @description 删除文件夹 /文件
	 * @param file 待删除文件
	 * @throws FileNotFoundException 文件不存在 
	 */
	public static void deleteFile(File file) throws FileNotFoundException {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File item : files) {
					deleteFile(item);
				}
				file.delete();
			}
		} else {
			throw new FileNotFoundException();
		}
	}
	
	/**
	 * @description 读取文件某一列数据
	 * @param path 文件路径
	 * @param columnSequence 列序 从1开始
	 * @return
	 */
	public static String[] readOneColumn(String path, int columnSequence) {
		return readOneColumn(new File(path), columnSequence);
	}
	
	/**
	 * @description 读取文件某一列数据
	 * @param file 文件
	 * @param columnSequence  列序  从1开始
	 * @return
	 */
	public static String[] readOneColumn(File file, int columnSequence) {
		
		BufferedReader reader = null;
		List<String> dataList = new ArrayList<String>();
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			String regex = "\\,\\s{0,}|\\s{1,}";
			String replacement = ",";
			while((line = reader.readLine()) != null) {
				line = line.trim();
				line = Pattern.compile(regex).matcher(line).replaceAll(replacement);
				dataList.add(line.split(replacement)[columnSequence - 1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null) 
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return dataList.toArray(new String[]{});
		
	}
	
	/**
	 * @description 从文件读取某一列
	 * @param path 文件路径
	 * @param columnSequence 列序 从0开始
	 * @return
	 */
	public static List<String> readColumn(String path, int columnSequence) {
		
		File file = new File(path);
		
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(file));
			
			String line = null;
			String regex = "\\,\\s{0,}|\\s{1,}";
			String replacement = ",";
			while( (line = br.readLine()) != null) {
				line = line.trim();
				line = Pattern.compile(regex).matcher(line).replaceAll(replacement);
				list.add(line.split(replacement)[columnSequence - 1]);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static void main(String[] args) {

	//	new FileHandler().createFile("/Users/lwg/Fiona/dataset/iris.data");
//		String path = "/Users/lwg/Fiona/dataset/iris.data";
//		int index = path.lastIndexOf(File.separator);
//		String dirPath = path.substring(0, index);
////		System.out.println(path.substring(index + 1, path.length()));
//		new FileHandler().createFile(dirPath);
//		new File(dirPath + path.substring(index + 1, path.length()));
//		try {
//			new FileHandler().deleteFile(new File("/Users/lwg/Fiona/dataset/iris.data"));
//			new FileHandler().deleteFile(new File("/Users/lwg/Fiona/dataset"));
//			new FileHandler().createFile("/Users/lwg/F1/dataset/iris.data");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}

}
