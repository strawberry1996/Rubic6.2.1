package cn.edu.cqupt.rubic_core.service;

import cn.edu.cqupt.rubic_core.exception.DataException;
import cn.edu.cqupt.rubic_framework.model.TextDataSet;
import cn.edu.cqupt.rubic_framework.model.TextExample;

import java.io.*;

/**
 * 构造纯文本数据
 * Created by Vigo on 16/4/13.
 */
public class TextDataSetConstruct {

    private TextDataSet textDataSet;

    public TextDataSetConstruct(String filePath, int dataType, String[] attributeLabels, Integer labelSequence) {

        File file = new File(filePath);

        initTextDataSet(file, dataType, attributeLabels, labelSequence);

    }
    /**
     * 初始化文本数据集
     * @param file
     * @param dataType
     * @param attributeLabels
     * @param labelSequence
     * @return
     */
    private void initTextDataSet(File file,  int dataType, String[] attributeLabels, Integer labelSequence) {
        textDataSet = new TextDataSet(attributeLabels);
        textDataSet.setType(dataType);
        String line = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file)));
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                TextExample textExample = generateTextExample(line, labelSequence);
                textDataSet.addTextExample(textExample);
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
     * 初始化纯文本数据集
     * @param line
     * @param sequence
     * @return
     */
    private TextExample generateTextExample(String line, int sequence){
        TextExample textExample = null;
        String[] str = line.split("\\*\\*");

        if (str.length == 1){

            textExample = new TextExample(str[0], null);

        } else {

            if (sequence == 1) {
                /** 标签在第一列 */
                textExample = new TextExample(str[1], str[0]);

            }else if (sequence == -1){
                /** 标签在最后一列 */
                textExample = new TextExample(str[0], str[1]);
            }
        }
        return textExample;
    }

    public TextDataSet getData() throws DataException {
        return textDataSet;
    }
}
