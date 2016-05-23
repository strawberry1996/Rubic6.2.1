package cn.edu.cqupt.rubic_core.service;

import cn.edu.cqupt.rubic_core.exception.DataException;
import cn.edu.cqupt.rubic_framework.model.DataSet;
import cn.edu.cqupt.rubic_framework.model.Example;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 非文本数据集构造
 * Created by Vigo on 16/4/13.
 */
public class DataSetConstruct {

    private DataSet dataSet;

    public DataSetConstruct(String filePath, int dataType, String[] attributeLabels, Integer labelSequence) {

        File file = new File(filePath);
        initDataSet(file, dataType, attributeLabels, labelSequence);

    }
    /**
     * 初始化DataSet
     * @param file
     * @param dataType
     * @param attributeLabels
     * @param labelSequence
     * @return
     */
    public void initDataSet(File file,  int dataType, String[] attributeLabels, Integer labelSequence) {
        dataSet = new DataSet(attributeLabels);
        dataSet.setType(dataType);
        String line = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file)));
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }

                Example example = generateExample(line, labelSequence);
                dataSet.addExample(example);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理数值类数据集
     * @param line
     * @param labelSequence
     * @return
     */
    private Example generateExample(String line,Integer labelSequence) {
        Example example = new Example();

        line.replace("^\\s+|\\s+$", "");
        line=line.replaceAll("\\s{1,}", ",");

        String[] tokens = line.split(",");
        if(labelSequence == 0){//修改labelSequence==null为labelSequence==0
            for (int i = 0; i < tokens.length; i++)
                example.addAttribute(tokens[i]);
        }else{
            List<String> strs = new ArrayList<String>();
            strs.addAll(Arrays.asList(tokens));//将tokens放入数组中
            String label = strs.get(labelSequence-1);//取出label，并将label从数组中删除，其他的都是attribute
            strs.remove(labelSequence-1);
            for (String string : strs) {
                example.addAttribute(string);
            }
            example.setLabel(label);
        }
        return example;
    }

    public DataSet getData() throws DataException {
        return dataSet;
    }
}
