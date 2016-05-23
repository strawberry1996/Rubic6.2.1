package cn.edu.cqupt.rubic_framework.model;

/**
 * 存储一行文本数据的结果
 * Created by Vigo on 16/3/31.
 */
public class ResultTextExample extends TextExample {

    /**
	 * 
	 */
	private static final long serialVersionUID = 85090463170999823L;
	private String newLabel;

    public ResultTextExample(String content) {
        super(content);
    }

    public String getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(String newLabel) {
        this.newLabel = newLabel;
    }
}
