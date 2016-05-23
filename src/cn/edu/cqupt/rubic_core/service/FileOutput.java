package cn.edu.cqupt.rubic_core.service;

import cn.edu.cqupt.rubic_framework.model.*;

import java.io.*;

/**
 * 结果集写入文件类
 * Created by Vigo on 16/4/15.
 */
public class FileOutput {

	/**
	 * 根据数据类型输出文件
	 * @param result
	 * @param filePath
	 * @param data_type
	 */
	public FileOutput(MyStruct[] result, String filePath, int data_type) {
		File file = new File(filePath);
		/** !!!!!!暂时只有个一个结果集!!!!!!*/
		if (data_type == 0){
			ResultDataSet resultDataSet = (ResultDataSet) result[0];
			writeResultDataSetFile(resultDataSet, file);
		}else if (data_type == 1){
			ResultTextDataSet resultTextDataSet = (ResultTextDataSet) result[0];
            writeResultTextDataSetFile(resultTextDataSet, file);
		}

	}

	/**
	 * 将ResultDataSet类型结果集写入文件
	 * @param result
	 * @param file
	 */
	private void writeResultDataSetFile(ResultDataSet result, File file) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			String content = parseResultDataSet(result);
			byte[] contentByte = content.getBytes();
			os.write(contentByte);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解析ResultDataSet类型结果集
	 * @param result
	 * @return
	 */
	private String parseResultDataSet(ResultDataSet result) {
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < result.getExamples().size(); j++) {
			ResultExample resultExample = (ResultExample) result.getExamples().get(j);
			String label = resultExample.getLabel();
			String newLabel = resultExample.getNewLabel();
			for (int i = 0; i < resultExample.size(); i++) {
				sb.append(resultExample.get(i));
				sb.append(",");
			}
			sb.append(label + "," + newLabel);
			sb.append("\r\n");
		}
		return sb.toString();
	}


    /**
     * 将ResultTextDataSet类型结果集写入文件
     * @param result
     * @param file
     */
    private void writeResultTextDataSetFile(ResultTextDataSet result, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            String content = parseResultTextDataSet(result);
            byte[] contentByte = content.getBytes();
            os.write(contentByte);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析ResultTextDataSet类型结果集
     * @param result
     * @return
     */
    private String parseResultTextDataSet(ResultTextDataSet result) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < result.getSize(); j++) {
            ResultTextExample resultTextExample = (ResultTextExample) result.getResult().get(j);
            String label = resultTextExample.getLabel();
            String newLabel = resultTextExample.getNewLabel();
            sb.append(resultTextExample.getContent());
            sb.append("**");
            sb.append(label + "**" + newLabel);
            sb.append("\r\n");
        }
        return sb.toString();
    }


}
