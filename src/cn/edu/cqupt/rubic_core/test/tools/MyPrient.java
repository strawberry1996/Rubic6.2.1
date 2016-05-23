package cn.edu.cqupt.rubic_core.test.tools;

import java.util.ArrayList;


public class MyPrient {

	/**
	 * @param args
	 */
	public static void prient(double[][] a, boolean flag) {
		if (flag == true) {
			for (int i = 0; i < a.length; i++) {
				for (int j = 0; j < a[0].length; j++) {
					System.out.print(a[i][j] + "   ");
				}
				System.out.println();
			}
		}

	}
	public static void prient(double[] a, boolean flag) {
		if (flag == true) {
			for (int i = 0; i < a.length; i++) {
					System.out.println(a[i] + "   ");
				}
			}
	}
	public static void prient(double a, boolean flag) {
		if (flag == true) {
				System.out.println(a);
		}

	}

	public static void prient(ArrayList<NBOneCluster> arrayList, boolean flag) {
		if (flag == true) {
			for (int i = 0; i < arrayList.size(); i++) {
				NBOneCluster oneCluster = (NBOneCluster) arrayList.get(i);
				System.out.println(oneCluster.toString());
			}
		}

	}

	public static void prient(String string, boolean flag) {
		if (flag == true) {
			System.out.println(string);

		}

	}
//	public static void print(String[] old,String[] newString, boolean flag){
//		if (flag==true) {
//			int right = 0;
//			for (int i = 0; i < newString.length; i++) {
//				//System.out.print(old[i] + "  " + newString[i]);
//				if (old[i].equals(newString[i])) {
//					right++;
//				//	System.out.print("   ......Yes");
//				}
//				//System.out.println();
//			}
//			System.out.println("all " + old.length + "    right" + right);
//			for (int i = 0; i < newString.length; i++) {
//				System.out.print(old[i] + "  " + newString[i]);
//				if (old[i].equals(newString[i])) {
////					right++;
//					System.out.print("   ......Yes");
//				}
//				System.out.println();
//			}
//		}
//		
//	}
	public static void print(String[] old,String[] newString, boolean flag){
		if (flag==true) {
			int right = 0;
			for (int i = 0; i < newString.length; i++) {
				System.out.print(old[i] + "  " + newString[i]);
				if (old[i].equals(newString[i])) {
					right++;
					System.out.print("   ......Yes");
				}
				System.out.println();
			}
			System.out.println("all " + old.length + "    right" + right);

		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
