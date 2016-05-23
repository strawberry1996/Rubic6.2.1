package cn.edu.cqupt.rubic_framework.model;

import java.io.Serializable;

/**
 * 一行文本数据
 * Created by Vigo on 15/12/18.
 */
public class TextExample implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5725765694352017459L;

	//文本数据内容
    private String content;

    //标签
    private String label;

    /**
     * 初始化带标签的文本数据
     * @param content
     * @param label
     */
    public TextExample(String content, String label){
        this.content = content;
        this.label = label;
    }

    public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
     * 初始化不带标签的文本数据
     * @param content
     */
    public TextExample(String content){
        this.content = content;
    }

    @Override
    public String toString() {
        return "[content=" + content + ", label=" + label + "]";
    }

}
