package cn.edu.cqupt.rubic_core.factory;

import cn.edu.cqupt.rubic_core.service.DataSetConstruct;
import cn.edu.cqupt.rubic_core.service.TextDataSetConstruct;
import cn.edu.cqupt.rubic_framework.model.DataSet;
import cn.edu.cqupt.rubic_framework.model.TextDataSet;

/**
 * Created by Vigo on 16/4/13.
 */
public class FileFactory {

    public static DataSet dataSetConstruct(String filePath, int dataType, String[] attributeLabels, Integer labelSequence){
        return new DataSetConstruct(filePath, dataType, attributeLabels, labelSequence).getData();
    }

    public static TextDataSet textDataSetConstruct(String filePath, int dataType, String[] attributeLabels, Integer labelSequence){
        return new TextDataSetConstruct(filePath, dataType, attributeLabels, labelSequence).getData();
    }
}
