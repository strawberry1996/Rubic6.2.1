package cn.edu.cqupt.rubic_core.controller;

import cn.edu.cqupt.net.connect.GetFileConnection;
import cn.edu.cqupt.rubic_business.Model.po.*;
import cn.edu.cqupt.rubic_business.service.*;
import cn.edu.cqupt.rubic_business.service.impl.ModelServiceImpl;
import cn.edu.cqupt.rubic_core.config.Configuration;
import cn.edu.cqupt.rubic_core.execute.OperationProcessException;
import cn.edu.cqupt.rubic_core.io.HDFSConnection;
import cn.edu.cqupt.rubic_framework.algorithm_interface.ErrorInputFormatException;
import cn.edu.cqupt.rubic_hadoop.excute.ProcessOnHadoop;
import cn.edu.cqupt.rubic_hadoop.tools.XMLPackage;
import com.alibaba.fastjson.JSON;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.jdom.Document;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Hadoop平台运行线程类
 * Created by Vigo on 16/3/2.
 */
public class RunOnHadoopThread implements Runnable {
    private DataSetService dataSetService;
    private AlgorithmService algorithmService;
    private AttributeService attributeService;
    private ResultService resultService;
    private ProcessRecordService processRecordService;
    @SuppressWarnings("unused")
    private ModelServiceImpl modelService;
    private Map<String, Object> request;

    public RunOnHadoopThread(ServletContext servletContext, Map<String, Object> request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.dataSetService = webApplicationContext.getBean(DataSetService.class);
        this.algorithmService = webApplicationContext.getBean(AlgorithmService.class);
        this.attributeService = webApplicationContext.getBean(AttributeService.class);
        this.resultService = webApplicationContext.getBean(ResultService.class);
        this.processRecordService = webApplicationContext.getBean(ProcessRecordService.class);
        this.modelService = webApplicationContext.getBean(ModelServiceImpl.class);
        this.request = request;
    }

    @Override
    public void run() {
        //返回给前端的运行状态
        Map<String, Object> response = new HashMap<String, Object>();

        //获取前端参数
        int userId = (Integer) request.get("userId");
        String platform = (String) request.get("platform");
        int[] dataIds = (int[]) request.get("dataIds");
        int algorithmId = (Integer) request.get("algorithmId");
        //参数信息
        double[] parameters = (double[]) request.get("parameters");
//        String type = (String) request.get("type");
//        String protocol_id = (String) request.get("protocol_id");

//        Integer threshold = (Integer) request.get("threshold");
        double threshold = (Double) request.get("threshold");
        Integer numIterators = (Integer) request.get("numIterators");

        // 赋值
        UserPo userPo = new UserPo();
        userPo.setUser_id(userId);
        Configuration config = new Configuration(userPo);

        //获取数据信息(多数据集)
        DataSetPo[] dataSetPos = getDataSetPos(dataIds);

        //获取一个数据集属性信息
        List<AttributePo> attrList = attributeService.getAttributesByDId(dataIds[0]);
        int label = -1;
        for (AttributePo attributePo : attrList) {
			if(attributePo.getAttribute_label()==1){
				label = attributePo.getAttribute_sequence();
			}
		}

        System.out.println("runonhadoop lsbel >>>>  "+label);
        
        //构造一个数据路径
        String dataSetPath = Configuration.getHDFS()+dataSetPos[0].getFile_path();
        //构造输出目录
        String subPath = Configuration.getHDFS()+userId+"/result/"+System.currentTimeMillis();

        //获取一个算法信息
        AlgorithmPo algorithmPo = algorithmService.findAlgorithmById(algorithmId);

        //构造算法jar包路径以及算法执行类
//        String jarFile = Configuration.getRubic() + algorithmPo.getFile_path();
        //-----修改：从文件系统中取得-----------
        String jarFile = getJarPath(algorithmPo, config);
        String runClass = algorithmPo.getPackage_name();


        //根据Hadoop平台,获取process
        ProcessOnHadoop process =null;

        //运行记录
        ProcessRecordPo processRecord = null;
        try {
            //加载算法
            System.out.println("jarFile ---- >>"+jarFile+"     runClass----->>>"+runClass);
            process = new ProcessOnHadoop(jarFile, runClass);
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
        } finally {//error1
            //加载算法成功后，初始化运行记录，并插入DB
            processRecord = initProcessRecord(dataSetPos[0], algorithmPo, platform, userId, response);
        }

        //运行状态信息
        String run_state = "运行结束";//数据库processRecord表的状态字段
        Date endDate = null;//运行结束的时间

        //运行结果信息
        String outputPath = null;

        try {
            //执行算法
            Document document = XMLPackage.packageToXML(dataSetPath, subPath, parameters, platform, threshold, numIterators,label,attrList);
            outputPath = process.execute(document);
            //生成模型！！！！！！！！！！！！！！！！

            //获取运行结束时间
            endDate = new Date();
        } catch (OperationProcessException e) {
            response.put("if_success", "0");
            response.put("reason", "运算过程发生错误：" + " " + e.getMessage());
            run_state = "运行失败";
            e.printStackTrace();
        } catch (ErrorInputFormatException e) {
            e.printStackTrace();
        } finally {

            if (response.get("if_success")==null) {
                //添加运行状态
                response.put("if_success", "1");
                //更新processRecord部分信息
                processRecord.setProcess_end(endDate);
                processRecord.setRun_state(run_state);
                processRecord.setReason(JSON.toJSONString(response));

                //更新运行结果到DB(修改此处！！！！！！！！！！修改outResult函数)
                outHadoopResult(outputPath, dataSetPos[0], attrList, processRecord);
            }else {
                //更新processRecord部分信息
                processRecord.setReason(JSON.toJSONString(response));
                processRecordService.updateProcessRecordByID(processRecord);
            }
        }
    }


    /**
     * 获取数据信息
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
     * 初始化运行记录
     * @param dataSetpo
     * @param algorithmPo
     * @param platform
     * @param userId
     * @return
     */
    private ProcessRecordPo initProcessRecord(DataSetPo dataSetpo, AlgorithmPo algorithmPo, String platform, int userId, Map<String, Object> response) {
        ProcessRecordPo processRecord = new ProcessRecordPo();
        Date process_start = new Date();
        String json_detail = "{\"algorithm_id\":"
                + algorithmPo.getAlgorithm_id()
                + ",\"algorithm_name\":"
                + algorithmPo.getAlgorithm_name()
                + ",\"dataset_id\":"
                + dataSetpo.getDataset_id()
                + ","
                + "\"dataset_name\":"
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
     * 合并输出目录下所有输出文件，更新DB
     * @param outputPath
     * @param dataSetPo
     * @param attrList
     * @param processRecord
     * @return
     */
    private void outHadoopResult(String outputPath, DataSetPo dataSetPo, List<AttributePo> attrList, ProcessRecordPo processRecord) {

    	//将多个结果文件合并为mergerd-result
    	String resultFileName = outputPath;
    	Path resultFile;  //合并后的结果文件
        Pattern dirPattern = Pattern.compile("\\w+-\\d+-final$");
        Pattern filePattern = Pattern.compile("part-\\w+-\\d+$");
        
        try {
            FileSystem fs;
            fs = HDFSConnection.getFileSystem();
            Path src = new Path(outputPath);
            if(fs.isFile(src)){  //判断返回的结果集文件是否为单个文件，如果是则不需要合并
            	resultFileName = outputPath;
            	resultFile = new Path(resultFileName);
            }else{
            	resultFileName = outputPath+"/mergerd-result";
            	resultFile = new Path(resultFileName);
            	
            	FileStatus[] status = fs.listStatus(src);
            	FSDataOutputStream out = fs.create(resultFile);
            	
            	for (FileStatus fileStatus : status) {
            		if(!fileStatus.isDir()&&
            				filePattern.matcher(fileStatus.getPath().toString()).find()){          //判断是否为文件夹，如果为文件夹则不合并
            			Path file = fileStatus.getPath();
            			
            			FSDataInputStream in = fs.open(file);
            			IOUtils.copyBytes(in, out, 4096, false);
            			fs.deleteOnExit(file);   //删除源文件
            			in.close();
            			break;
            		}else if(dirPattern.matcher(fileStatus.getPath().toString()).find()){
            			Path dir = fileStatus.getPath();
            			for (FileStatus fileStatus2 : fs.listStatus(dir)) {
            				if(filePattern.matcher(fileStatus2.getPath().toString()).find()){
            					Path file = fileStatus2.getPath();
            					
            					FSDataInputStream in = fs.open(file);
            					IOUtils.copyBytes(in, out, 4096, false);
            					fs.deleteOnExit(file);   //删除源文件
            					in.close();
            					break;
            				}
            			}
            		}
            	}
            	out.close();
            }
            


        } catch (IOException e) {
        	System.out.println("fs uploading wrong");
            e.printStackTrace();
        }



        //找到数据集标签列，构造结果标签label，并插入attribute表中,返回结果标签属性id即lableId
        AttributePo lable = null;
        for (int i = 0; i < attrList.size(); i++) {
            AttributePo attributepo = attrList.get(i);
            if (attributepo.getAttribute_label() == 1){
                lable = attributepo;
                break;
            }
        }
        if(lable!=null){
        	
        
        	AttributePo resultLabel = new AttributePo();
        	
        	resultLabel.setAttribute_label(1);
        	resultLabel.setAttribute_character(lable.getAttribute_character());
        	resultLabel.setAttribute_missing(lable.getAttribute_missing());
        	resultLabel.setAttribute_range(lable.getAttribute_range());
        	resultLabel.setAttribute_type(lable.getAttribute_type());
        	resultLabel.setAttribute_name("result_attribute");
        	resultLabel.setAttribute_sequence(lable.getAttribute_sequence() + 1);
        	int resultLabelId = attributeService.addAttribute(resultLabel);
        	
        	
        	//构造结果集resultPo，并将结果集插入DB,返回结果集id
        	ResultPo resultPo = dataSetPoToResultPo(dataSetPo, resultFileName);
        	int resultdataset_id = resultService.insertResult(resultPo);
        	
        	//更新resultData-Attribute关系表
        	ResultdatasetAttributeRelationshipPo relationship = new ResultdatasetAttributeRelationshipPo();
        	for (AttributePo attr : attrList) {
        		relationship.setResultdataset_id(resultdataset_id);
        		relationship.setAttribute_id(attr.getAttribute_id());
        		attributeService.addResultdatasetAttributeRelation(relationship);
        	}
        	
        	relationship.setResultdataset_id(resultdataset_id);
        	relationship.setAttribute_id(resultLabelId);
        	attributeService.addResultdatasetAttributeRelation(relationship);
        	processRecord.setResultdataset_id(resultdataset_id);
        }else{
        	System.out.println("outhadoopresult  label >>> null");
        	processRecord.setResultdataset_id(0);
        }




        //更新ProcessRecord表
        processRecord.setResult_path(resultFileName.substring(Configuration.getHDFS().length()));

        processRecordService.updateProcessRecordByID(processRecord);

    }


    /**
     * 将DataSetPo转为ResultPo，方便数据库插入
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
        resultPo.setFile_path(filePath.substring(7));
        resultPo.setIspublic(dataSetPo.getIspublic());
        resultPo.setNumber_examples(dataSetPo.getNumber_examples());
        resultPo.setResultdataset_name(dataSetPo.getDataset_name());
        resultPo.setSubmit_datetime(dataSetPo.getSubmit_datetime());
        resultPo.setPlatform(dataSetPo.getPlatform());

        return resultPo;
    }

    /**
     * 从文件系统中获取文件
     * @param file_path 数据/算法文件相对路径
     * @return
     */
    public String getFileFromFileSystem(String file_path, Configuration config){

        Map<String, Object> requestMap=new HashMap<String, Object>();

        requestMap.put("protocol_id","2");

        requestMap.put("file_path", file_path);

        GetFileConnection connection = new GetFileConnection();

        connection.sendGetFileRequest(requestMap);

        int index = file_path.lastIndexOf("\\");

        String file_name = file_path.substring(index + 1, file_path.length());

        connection.saveFile(config.getTMP_PATH(), file_name);
        
        String dataPath = config.getTMP_PATH() + File.separator + file_name;
//        String dataPath = "F:" +File.separator+"Rubic\\62\\algorithm"+ File.separator + file_name;

        return dataPath;
    }

    /**
     * 获取jar包路径
     * @param algorithmPo
     * @return
     */
    public String getJarPath(AlgorithmPo algorithmPo, Configuration config){

        String jarPath = getFileFromFileSystem(algorithmPo.getFile_path(), config);
        return jarPath;
    }
}
