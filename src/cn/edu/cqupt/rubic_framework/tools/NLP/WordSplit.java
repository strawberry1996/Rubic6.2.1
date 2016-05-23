package cn.edu.cqupt.rubic_framework.tools.NLP;

import java.util.ArrayList;
import java.util.List;


import cn.edu.cqupt.rubic_core.tools.WordSplitRunTime;
import cn.edu.cqupt.rubic_framework.model.Word;

/**
 * 分词工具,
 * 
 * @author Felix
 * 
 */
public class WordSplit {

	/**
	 * 示例
	 * 
	 * @param 需要分词的文本串
	 * @return List<String> 字符数组
	 */
	public List<String> doSplit(String text) {

		return new WordSplitRunTime().doSplit(text);
	}

	/**
	 * @param：原始文本，删除词组，添加词组
	 * @return 分词结果list Word,每个word包括了内容和词性
	 * */
	public ArrayList<Word>  doSplit(String text, ArrayList<Word> delWords,
			ArrayList<Word> addWords) {

		return new WordSplitRunTime().doSplit(text, delWords,
				addWords);
	}

	public static void main(String[] args) {

		String text = "创新是推动国家和民族向前发展的重要力量，党的十八大提出实施科技创新驱动发展战略，科技创新上升到国家发展的核心位置。高校是国家创新体系的重要组成部分，科技创新与成果转化更是高校服务经济社会发展的重要手段。近年来，我校高度重视产学研用协同创新和成果转化工作，积极发挥学校学科特色和优势，不断推进多方面的合作，探索新模式，构建技术创新平台，取得了积极成效。";
		List<Word> res = new WordSplitRunTime().doSplit(text,null,null);
		for (int i = 0; i < res.size(); i++) {
			System.out.println(res.get(i).getWord());
		}
	}

}