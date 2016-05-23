package cn.edu.cqupt.rubic_core.tools;

import java.util.List;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.sun.jna.Library;
import com.sun.jna.Native;


import cn.edu.cqupt.rubic_framework.model.Word;


import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 
 * <p>
 * Description:分词
 * </p>
 * 
 * @author hey
 * @author liwg
 * @date 2015-9-27
 */
public class WordSplitRunTime {

	// 到时候给用户的
	
	/*  public List<String> doSplit(String text){
	  
	  return null; }
	 */
	
	//真正的分词
	public List<String> doSplit(String text) {
		List<String> words = new ArrayList<String>();
		StringReader sr = new StringReader(text);
		IKSegmenter ik = new IKSegmenter(sr, true);
		Lexeme lex;
		try {
			while ((lex = ik.next()) != null) {
				words.add(lex.getLexemeText());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return words;
	}
	
	
	
	
	/*
	 * NLPIR文件夹地址 NLPIR的使用是权限限制，证书只能使用一段时间，过期后需要更新证书
	 * 如果NLPIR不可使用时，提示证书过期，到官方网站http://ictclas.nlpir.org/downloads下载最新版本
	 * 把最新版本中的Data文件夹中NLPIR.user更新老版本中的Data文件夹中NLPIR.user
	 */
	public static String fileNLPIR = "C:\\NLPIR";

	// 到时候给用户的

	/*
	 * public List<String> doSplit(String text){
	 * 
	 * return null; }
	 */

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(fileNLPIR
				+ "\\bin\\ICTCLAS2015\\NLPIR", CLibrary.class);

		public int NLPIR_Init(String sDataPath, int encoding,
							  String sLicenceCode);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
										boolean bWeightOut);

		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
											boolean bWeightOut);

		public int NLPIR_AddUserWord(String sWord);// add by qp 2008.11.10

		public int NLPIR_DelUsrWord(String sWord);// add by qp 2008.11.10

		public String NLPIR_GetLastErrorMsg();

		public void NLPIR_Exit();
	}

	public static String transString(String aidString, String ori_encoding,
			String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * @param：原始文本，删除词组，添加词组
	 * @return 分词结果
	 * */
 
	public ArrayList<Word> doSplit(String text, ArrayList<Word> delWords,
			ArrayList<Word> addWords) {
		ArrayList<Word> resultWords = new ArrayList<Word>();
		String argu = fileNLPIR;
		int charset_type = 1;

		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;

		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is " + nativeBytes);
			return null;
		}

		try {
			// 删除不需要的词组
			if (delWords != null) {
				for (int i = 0; i < delWords.size(); i++) {
					CLibrary.Instance.NLPIR_DelUsrWord(delWords.get(i)
							.getWord());
				}
			}
			// 添加新词词典
			if (addWords != null) {
				for (int i = 0; i < addWords.size(); i++) {
					CLibrary.Instance.NLPIR_AddUserWord(addWords.get(i)
							.getWord() + " " + addWords.get(i).getProperty());
				}
			}
			// 执行分词
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(text, 1);

			// 取得分词结果
			String[] allwords = nativeBytes.split(" ");

			if (allwords.length > 0) {
				for (int i = 0; i < allwords.length; i++) {
					String[] s = allwords[i].split("/");
					if (s.length == 2) {
						Word word = new Word();
						word.setWord(s[0]);
						word.setProperty(s[1]);
						resultWords.add(word);
					}
				}
			}

			CLibrary.Instance.NLPIR_Exit();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultWords;
	}
	 public static void main(String[] args) {

	        String text = "创新是推动国家和民族向前发展的重要力量，党的十八大提出实施科技创新驱动发展战略，科技创新上升到国家发展的核心位置。高校是国家创新体系的重要组成部分，科技创新与成果转化更是高校服务经济社会发展的重要手段。近年来，我校高度重视产学研用协同创新和成果转化工作，积极发挥学校学科特色和优势，不断推进多方面的合作，探索新模式，构建技术创新平台，取得了积极成效。";
	        List<Word> res = new WordSplitRunTime().doSplit(text,null,null);
	        for (int i = 0; i < res.size(); i++) {
	            System.out.println(res.get(i).getWord());
	        }
	    }

}
