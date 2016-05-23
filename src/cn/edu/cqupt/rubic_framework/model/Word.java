package cn.edu.cqupt.rubic_framework.model;

public class Word {

	/**
	 * @param args
	 * 词类，有两个成员变量，一个是词本身，另一个是该词的词性
	 */
	String word;
	String property;
	/**
	 * 获得该词本身内容
	 * */
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * 获得该词词性
	 * */
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}
