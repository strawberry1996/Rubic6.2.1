package cn.edu.cqupt.rubic_core.controller;

import cn.edu.cqupt.net.handle.TransFileHandle;
import cn.edu.cqupt.rubic_business.Model.po.*;
import cn.edu.cqupt.rubic_business.service.*;
import cn.edu.cqupt.rubic_business.service.impl.ModelServiceImpl;
import cn.edu.cqupt.rubic_core.config.Configuration;
import cn.edu.cqupt.rubic_core.execute.OperationListener;
import cn.edu.cqupt.rubic_core.execute.OperationProcessException;
import cn.edu.cqupt.rubic_core.execute.Process;
import cn.edu.cqupt.rubic_core.factory.FileFactory;
import cn.edu.cqupt.rubic_core.service.FileOutput;
import cn.edu.cqupt.rubic_framework.algorithm_interface.ErrorInputFormatException;
import cn.edu.cqupt.rubic_framework.model.MyStruct;
import cn.edu.cqupt.rubic_framework.service_interface.EventListener;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


import javax.servlet.ServletContext;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by Vigo on 16/5/16.
 */
public class APIRunThread implements Runnable {
    private DataSetService dataSetService;
    private AlgorithmService algorithmService;
    private AttributeService attributeService;
    private ResultService resultService;
    private ProcessRecordService processRecordService;
    private ModelServiceImpl modelService;
    private HashMap<String, Object> processHash;
    private Map<String, Object> request;
    private TransFileHandle transFileHandle;

    private static final Logger logger = Logger.getLogger(APIRunThread.class);

    public APIRunThread(ServletContext servletContext,Map<String, Object> request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getWebApplicationContext(servletContext);
        this.dataSetService = webApplicationContext
                .getBean(DataSetService.class);
        this.algorithmService = webApplicationContext
                .getBean(AlgorithmService.class);
        this.attributeService = webApplicationContext
                .getBean(AttributeService.class);
        this.resultService = webApplicationContext.getBean(ResultService.class);
        this.processRecordService = webApplicationContext
                .getBean(ProcessRecordService.class);
        this.modelService = webApplicationContext
                .getBean(ModelServiceImpl.class);
        this.processHash = (HashMap<String, Object>) servletContext
                .getAttribute("processHash");
        this.request = request;
        this.transFileHandle = new TransFileHandle();
    }

    @Override
    public void run() {
        // 存入运行记录中的运行状态
        Map<String, Object> response = new HashMap<String, Object>();

        // 获取用户编号
        int user_id = (Integer) request.get("userid");
        // 获取模型使用平台
        String platform = (String) request.get("platform");
        // 获取数据id
        int data_id = (Integer) request.get("data_id");
        // 获取数据类型(0:数值型；1：文本型)
        int data_type = (Integer) request.get("data_type");
        // 获取模型
        Object model = request.get("model");
        // 获取算法（可能多个算法）
        List<AlgorithmPo> algorithms = (ArrayList<AlgorithmPo>) request.get("algorithms");
        // 获取属性
        List<AttributePo> attributes = (ArrayList<AttributePo>) request.get("attributes");

        // 初始化配置类
        UserPo userPo = new UserPo();
        userPo.setUser_id(user_id);
        Configuration config = new Configuration(userPo);

        // 构造数据集
        DataSetPo dataSetPo =  dataSetService.findDataSetById(data_id);
        MyStruct[] inputs = getMyStruct(dataSetPo, data_type, attributes, config);

        // 获取算法jar包路径以及算法执行类(!!!!!暂时只有一个算法!!!!!)
        AlgorithmPo algorithmPo = algorithms.get(0);
        String jarFile = getJarPath(algorithmPo, config);
        String runClass = algorithmPo.getPackage_name();

        // 运行
        Process process = null;
        // 运行记录
        ProcessRecordPo processRecord = null;
        try {
            // 加载算法
            process = new Process(jarFile, runClass);
        } catch (ClassNotFoundException e) {
            response.put("if_success", "0");
            response.put("reason", "运行类没有找到");
            e.printStackTrace();
        } catch (InstantiationException e) {
            response.put("if_success", "0");
            response.put("reason", "运行类没有无参构造函数");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            response.put("if_success", "0");
            response.put("reason", "运行类非法");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            response.put("if_success", "0");
            response.put("reason", "该算法损坏");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            // 加载算法成功后或者发生异常，初始化运行记录，并插入DB
            processRecord = initProcessRecord(dataSetPo,
                    algorithmPo, platform, user_id, response);
        }

        // 运行状态信息
        String run_state = "运行结束";// 数据库processRecord表的状态字段
        Date endDate = null;// 运行结束的时间

        // 定义运行所需变量
        MyStruct[] result = null;
        EventListener listener = null;


        try {
            listener = new OperationListener();
            // 给运行添加监听器，监听运行状态
            process.addActionListener(listener);
            // 保存运行信息
            processHash.put(processRecord.getProcess_id() + "", listener);
            // 调用模型
            result = process.usingModel(inputs, model);
            logger.debug(result);

            // 获取运行结束时间
            endDate = new Date();

        } catch (ErrorInputFormatException e) {
            response.put("if_success", "0");
            response.put("reason", "数据格式错误");
            run_state = "运行失败";
            e.printStackTrace();
        } catch (OperationProcessException e) {
            response.put("if_success", "0");
            response.put("reason", "运算过程发生错误：" + " " + e.getMessage());
            run_state = "运行失败";
            e.printStackTrace();
        } catch (Exception e) {
            response.put("if_success","0");
            response.put("reason","模型调用失败");
            e.printStackTrace();
        } finally {

            if (response.get("if_success") == null) {
                // 添加运行状态
                response.put("if_success", "1");
                // 更新processRecord部分信息
                processRecord.setProcess_end(endDate);
                processRecord.setRun_state(run_state);
                processRecord.setReason(JSON.toJSONString(response));

                // 更新运行结果到DB
                outResult(result, user_id, algorithmPo, dataSetPo, attributes,
                        processRecord);
            } else {
                // 更新processRecord部分信息
                processRecord.setReason(JSON.toJSONString(response));
                processRecordService.updateProcessRecordByID(processRecord);
            }

            //删除从文件系统中取得的文件
            transFileHandle.deleteTmpFile(config.getTMP_PATH());
            System.out.println("tmpPath:----->" + config.getTMP_PATH());
        }


        // 回调用户地址
    }

    /**
     * 构造数据集
     * @param dataSetPo
     * @param data_type
     * @param attributes
     * @param config
     * @return
     */
    private MyStruct[] getMyStruct(DataSetPo dataSetPo, int data_type, List<AttributePo> attributes, Configuration config) {
        MyStruct[] myStruct = new MyStruct[1];
        List<String> attributeNames = new ArrayList<String>();
        int labelSequence = 0;
        for (int i = 0; i < attributes.size(); i++) {
            AttributePo attributepo = attributes.get(i);
            attributeNames.add(attributepo.getAttribute_name());
            if (attributepo.getAttribute_label() == 1) {
                labelSequence = attributepo.getAttribute_sequence();
            }
        }

        //从文件系统中取得文件,返回文件位置(!!!!!!!日后会修改!!!!!!!!!!)
        String dataPath = transFileHandle.getFileFromFileSystem(dataSetPo.getFile_path(), config);
        /** 数值型*/
        if (data_type == 0) {
            myStruct[0] = FileFactory.dataSetConstruct(dataPath,
                    dataSetPo.getDataset_type(),
                    attributeNames.toArray(new String[attributeNames.size()]),
                    labelSequence);
            System.out.println("-----数值型-----");
        }else if (data_type == 1){
            myStruct[0] = FileFactory.textDataSetConstruct(dataPath,
                    dataSetPo.getDataset_type(),
                    attributeNames.toArray(new String[attributeNames.size()]),
                    labelSequence);
            System.out.println("-----纯文本型-----");
        }
        return myStruct;
    }

    /**
     * 获取jar包路径
     * @param algorithmPo
     * @return
     */
    public String getJarPath(AlgorithmPo algorithmPo, Configuration config){

        String jarPath = transFileHandle.getFileFromFileSystem(algorithmPo.getFile_path(), config);
        return jarPath;
    }

    /**
     * 初始化运行记录
     *
     * @param dataSetpo
     * @param algorithmPo
     * @param platform
     * @param userId
     * @param response
     * @return
     */
    private ProcessRecordPo initProcessRecord(DataSetPo dataSetpo,
                                              AlgorithmPo algorithmPo, String platform, int userId, Map<String, Object> response) {
        ProcessRecordPo processRecord = new ProcessRecordPo();
        Date process_start = new Date();
        String json_detail = "{\"algorithm_id\":"
                + algorithmPo.getAlgorithm_id() + ",\"algorithm_name\":"
                + algorithmPo.getAlgorithm_name() + ",\"dataset_id\":"
                + dataSetpo.getDataset_id() + "," + "\"dataset_name\":"
                + dataSetpo.getDataset_name() + "," + "\"platform\":"
                + platform + "}";
        processRecord.setProcess_start(process_start);
        processRecord.setPlatform(platform);
        processRecord.setJson_detail(json_detail);
        processRecord.setRun_state("running");
        processRecord.setUser_id(userId);
        int processID = resultService.addProcessRecord(processRecord);
        processRecord.setProcess_id(processID);
        //添加失败原因
        String fail_reason = JSON.toJSONString(response);
        processRecord.setReason(fail_reason);
        return processRecord;
    }

    /**
     * 将结果集输出到文件中保存,返回该结果数据集文件的完整路径
     *
     * @param result
     * @param userId
     * @param algorithmPo
     * @param dataSetPo
     * @return
     */
    private void outResult(MyStruct[] result, int userId,
                           AlgorithmPo algorithmPo, DataSetPo dataSetPo,
                           List<AttributePo> attrList, ProcessRecordPo processRecord) {

        String resultPath = Configuration.getRubic() + userId + File.separator
                + "result";
        File file = new File(resultPath);
        file.mkdirs();

        String dataName = dataSetPo.getDataset_name();// 现在运行一个数据集，所以以第一个数据作为实际运行用的数据
        String resultFileName = System.currentTimeMillis() + "_"
                + algorithmPo.getAlgorithm_name() + "_" + dataName + ".data";

        String filePath = resultPath + File.separator + resultFileName;
        FileOutput fileOutput = new FileOutput(result, filePath, dataSetPo.getDataset_type());

        // 找到数据集标签列，构造结果标签label，并插入attribute表中,返回结果标签属性id即lableId
        AttributePo lable = null;
        for (int i = 0; i < attrList.size(); i++) {
            AttributePo attributepo = attrList.get(i);
            if (attributepo.getAttribute_label() == 1) {
                lable = attributepo;
                break;
            }
        }
        AttributePo resultLabel = new AttributePo();

        resultLabel.setAttribute_label(1);
        resultLabel.setAttribute_character(lable.getAttribute_character());
        resultLabel.setAttribute_missing(lable.getAttribute_missing());
        resultLabel.setAttribute_range(lable.getAttribute_range());
        resultLabel.setAttribute_type(lable.getAttribute_type());
        resultLabel.setAttribute_name("result_attribute");
        resultLabel.setAttribute_sequence(lable.getAttribute_sequence() + 1);
        int resultLabelId = attributeService.addAttribute(resultLabel);

        // 构造结果集resultPo，并将结果集插入DB,返回结果集id
        ResultPo resultPo = dataSetPoToResultPo(dataSetPo, filePath);
        int resultdataset_id = resultService.insertResult(resultPo);

        // 更新resultData-Attribute关系表
        ResultdatasetAttributeRelationshipPo relationship = new ResultdatasetAttributeRelationshipPo();
        for (AttributePo attr : attrList) {
            relationship.setResultdataset_id(resultdataset_id);
            relationship.setAttribute_id(attr.getAttribute_id());
            attributeService.addResultdatasetAttributeRelation(relationship);
        }
        //
        relationship.setResultdataset_id(resultdataset_id);
        relationship.setAttribute_id(resultLabelId);
        attributeService.addResultdatasetAttributeRelation(relationship);

        // 更新ProcessRecord表
        processRecord.setResult_path(userId + File.separator + "result"
                + File.separator + resultFileName);
        processRecord.setResultdataset_id(resultdataset_id);

        processRecordService.updateProcessRecordByID(processRecord);

        //---文件发送到FileSystem
        transFileHandle.sendResultFileToFileSystem(resultPo.getFile_path(), filePath);
        //删除结果文件
        transFileHandle.deleteTmpFile(filePath);

    }

    /**
     * 将DataSetPo转为ResultPo，方便数据库插入
     *
     * @param dataSetPo
     * @return
     */
    private ResultPo dataSetPoToResultPo(DataSetPo dataSetPo, String filePath) {
        ResultPo resultPo = new ResultPo();
        resultPo.setArea(dataSetPo.getArea());
        resultPo.setAssociated_tasks(dataSetPo.getAssociated_tasks());
        resultPo.setAttribute_count(dataSetPo.getAttribute_count() + 1);
        resultPo.setDataset_type(dataSetPo.getDataset_type());
        resultPo.setDescription(dataSetPo.getDescription());
        resultPo.setDownload_count(dataSetPo.getDownload_count());
        resultPo.setFile_path(filePath.substring(9));
        resultPo.setIspublic(dataSetPo.getIspublic());
        resultPo.setNumber_examples(dataSetPo.getNumber_examples());
        resultPo.setResultdataset_name(dataSetPo.getDataset_name());
        resultPo.setSubmit_datetime(dataSetPo.getSubmit_datetime());
        resultPo.setPlatform(dataSetPo.getPlatform());

        return resultPo;
    }

}
