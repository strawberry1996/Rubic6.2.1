package cn.edu.cqupt.rubic_framework.model;


/**
 * 矩阵模型接口
 * 
 * @author Colin Wang
 *
 */
public interface Matrix extends MyStruct {

	/**
	 * 获得矩阵行数
	 * 
	 * @return Row
	 */
	public int getRow();

	/**
	 * 获得矩阵列数
	 * 
	 * @return Column
	 */
	public int getColumn();

	/**
	 * 通过坐标获得矩阵中结点的内容
	 * 
	 * @param Coordinate 格式为(x,y,...,z)
	 * @return model 自定义model结点内容（权值等）
	 */
	public Object getNode(int[] Coordinate);

	/**
	 * 通过坐标设置矩阵中结点的内容
	 * 
	 * @param Coordinate
	 * @param value
	 */
	public void setNode(int[] Coordinate, Object value);

	/**
	 * 通过两点坐标取得边的内容
	 * 
	 * @param Coordinate1
	 * @param Coordinate2
	 * @return
	 */
	public Object getEdge(int[] Coordinate1, int[] Coordinate2);

	/**
	 * 通过两点坐标设置边的内容
	 * 
	 * @param Coordinate1
	 * @param Coordinate2
	 * @param value
	 */
	public void setEdge(int[] Coordinate1, int[] Coordinate2, Object value);

	/**
	 * 复制同结构矩阵
	 * 
	 * @param matrix
	 * @return
	 */
	public Matrix copy(Matrix matrix);

	/**
	 * 打印矩阵
	 */
	public void printMatrix();
}
