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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;


/**
 * JAVA平台运行线程类
 * Created by Vigo on 16/3/1.
 */
public class RunThread implements Runnable {
    private DataSetService dataSetService;
    private AlgorithmService algorithmService;
    private AttributeService attributeService;
    private ResultService resultService;
    private ProcessRecordService processRecordService;
    private ModelServiceImpl modelService;
    private HashMap<String, Object> processHash;

    private Map<String, Object> request;

    private TransFileHandle transFileHandle;

    private static final Logger logger = Logger.getLogger(RunThread.class);


    public RunThread(ServletContext servletContext, Map<String, Object> request) {
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
        // 获取前端参数
        int userId = (Integer) request.get("userId");
        String platform = (String) request.get("platform");
        int[] dataIds = (int[]) request.get("dataIds");
        int algorithmId = (Integer) request.get("algorithmId");
        // 参数信息
        double[] parameters = (double[]) request.get("parameters");

        // 赋值
        UserPo userPo = new UserPo();
        userPo.setUser_id(userId);
        Configuration config = new Configuration(userPo);

        // 获取数据信息(多数据集)
        DataSetPo[] dataSetPos = getDataSetPos(dataIds);

        // 获取一个数据集属性信息
        List<AttributePo> attrList = attributeService
                .getAttributesByDId(dataIds[0]);

        // 构造一个数据集
        MyStruct[] inputs = getMyStruct(dataSetPos[0], attrList, config);

        // 获取一个算法信息
        AlgorithmPo algorithmPo = algorithmService
                .findAlgorithmById(algorithmId);

        // 构造算法jar包路径以及算法执行类
       // String jarFile = Configuration.getRubic() + algorithmPo.getFile_path();
        String jarFile = getJarPath(algorithmPo, config);
        String runClass = algorithmPo.getPackage_name();

        // 根据平台选择,获取process
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
            processRecord = initProcessRecord(dataSetPos[0],
                    algorithmPo, platform, userId, response);
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
            // 执行算法
            result = process.execute(inputs, config, parameters);
            logger.debug(result);

            // 获取运行结束时间
            endDate = new Date();

            // 生成模型???是否是在这里生成运行模型
            Object object = process.createModel();
            if(object!=null){

                modelService.createModel(object, algorithmPo, dataSetPos, attrList,userId,parameters);
            }
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
        } catch (FileNotFoundException e) {
            response.put("if_success","0");
            response.put("reason","临时文件没找到");
            e.printStackTrace();
        } catch (Exception e) {
            response.put("if_success","0");
            response.put("reason","模型生成失败");
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
                outResult(result, userId, algorithmPo, dataSetPos[0], attrList,
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
    }

    /**
     * 获取数据信息
     *
     * @param dataIds
     * @return
     */
    private DataSetPo[] getDataSetPos(int[] dataIds) {
        DataSetPo[] dataSetPos = new DataSetPo[dataIds.length];
        for (int i = 0; i < dataIds.length; i++) {
            int dataId = dataIds[i];
            dataSetPos[i] = dataSetService.findDataSetById(dataId);
        }
        return dataSetPos;
    }

    /**
     * 构造数据集(日后需要添加数据预处理！！！！！！！)
     *
     * @param dataSetPo
     * @param attrList
     * @return
     */
    public MyStruct[] getMyStruct(DataSetPo dataSetPo,
                                  List<AttributePo> attrList, Configuration config) {
        // 此处myStruct[0]测试数据，myStruct[1]预测数据
        MyStruct[] myStruct = new MyStruct[2];
    //    String dataPath = Configuration.getRubic() + dataSetPo.getFile_path();
   //     System.out.println(dataPath);
        List<String> attributeNames = new ArrayList<String>();
        int labelSequence = 0;
        for (int i = 0; i < attrList.size(); i++) {
            AttributePo attributepo = attrList.get(i);
            attributeNames.add(attributepo.getAttribute_name());
            if (attributepo.getAttribute_label() == 1) {
                labelSequence = attributepo.getAttribute_sequence();
            }
        }
        //从文件系统中取得文件,返回文件位置(!!!!!!!日后会修改!!!!!!!!!!)
        String dataPath = transFileHandle.getFileFromFileSystem(dataSetPo.getFile_path(), config);
        //构造数据集对象
        // 此处日后做交叉验证分析!!!!!!!!!!!
//        myStruct[0] = new FileDataFactory(dataPath,
//                dataSetPo.getDataset_type(),
//                attributeNames.toArray(new String[attributeNames.size()]),
//                labelSequence).getData("");
        /** 数值型*/
        if (dataSetPo.getDataset_type() == 0) {
            myStruct[0] = FileFactory.dataSetConstruct(dataPath,
                    dataSetPo.getDataset_type(),
                    attributeNames.toArray(new String[attributeNames.size()]),
                    labelSequence);
            System.out.println("-----数值型-----");
        }else if (dataSetPo.getDataset_type() == 1){
            myStruct[0] = FileFactory.textDataSetConstruct(dataPath,
                    dataSetPo.getDataset_type(),
                    attributeNames.toArray(new String[attributeNames.size()]),
                    labelSequence);
            System.out.println("-----纯文本型-----");
        }



        return myStruct;
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


       // ResultDataSet resultDataSet = (ResultDataSet) result[0];

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

    /**
     * 获取jar包路径
     * @param algorithmPo
     * @return
     */
    public String getJarPath(AlgorithmPo algorithmPo, Configuration config){

        String jarPath = transFileHandle.getFileFromFileSystem(algorithmPo.getFile_path(), config);
        return jarPath;
    }
}

