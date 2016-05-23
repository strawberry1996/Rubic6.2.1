package cn.edu.cqupt.rubic_framework.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文本数据集
 * Created by Vigo on 15/12/18.
 */
public class TextDataSet implements MyStruct,Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8289369643102910440L;
	// 数据集描述
    protected String description;
    // 数据集名字
    protected String name;
    // 数据集类型
    protected int type;
    // 属性说明
    protected String[] attributeLabels;
    // 属性值集合，每个Example为一行
    protected List<TextExample> textExamples;

    public TextDataSet(String[] attributeLabels){
        this.attributeLabels = attributeLabels;
        textExamples = new ArrayList<TextExample>();
    }

    public void addTextExample(TextExample textExample){
        this.textExamples.add(textExample);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] getAttributeLabels() {
        return attributeLabels;
    }

    public void setAttributeLabels(String[] attributeLabels) {
        this.attributeLabels = attributeLabels;
    }

    public List<TextExample> getTextExamples() {
        return textExamples;
    }

    public void setTextExamples(List<TextExample> textExamples) {
        this.textExamples = textExamples;
    }

    @Override
    public String toString() {
        return "TextDataSet [description=" + description + ", name=" + name
                + ", attributeLabels=" + Arrays.toString(attributeLabels)
                + ", textExamples=" + textExamples + "]";
    }
}
