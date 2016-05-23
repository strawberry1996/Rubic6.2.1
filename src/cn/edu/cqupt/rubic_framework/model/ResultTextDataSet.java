package cn.edu.cqupt.rubic_framework.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文本数据结果集
 * Created by Vigo on 15/12/18.
 */
public class ResultTextDataSet extends TextDataSet implements MyStruct {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4151851959900209497L;
	private List<ResultTextExample> result;

    public ResultTextDataSet(String[] labels) {
        super(labels);
        this.result = new ArrayList<ResultTextExample>();
    }

    public void addResultTextExample(ResultTextExample resultTextExample){
        this.result.add(resultTextExample);
    }

    public List<ResultTextExample> getResult() {
        return result;
    }

    public int getSize(){return result.size();}

    @Override
    public String toString() {
        return "ResultTextDataSet [description=" + description + ", name=" + name
                + ", labels=" + Arrays.toString(attributeLabels) + "]";
    }
}
