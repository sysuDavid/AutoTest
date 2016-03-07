package util;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Method�ǹ����࣬�䷽����ҵ���޹ص�ͨ�÷�������Ϊstatic����
 * @author yuanwj
 *
 */
public class Method {
	
	public static String getFilePostfix(String s) {
		return s.substring(s.lastIndexOf(".")+1);
	}
	/**
	 * ɾ�������ַ��������С���㼰0��
	 * �������������double����Ĭ�ϵ�һλС����
	 * @param s
	 * @return
	 */
	public static String delZeroAndDot(String s) {
		if (s.indexOf('.') > 0) {
			s = s.replaceAll("0+?$", "");
			s = s.replaceAll("[.]$", "");
		}
		return s;
	}
	
	/**
	 * replace�����滻�ַ�����ָ������
	 * @param str - ���滻���ַ���
	 * @param list - �����滻�ַ�����һ��String����
	 * @return �滻����ַ���
	 */
	public static String replaceString(String str, ArrayList<String> list) {
		StringBuffer buf = new StringBuffer();
		String strArray[] = str.split("\\{\\{|\\}\\}", 3);
		while (strArray.length == 3) {
			buf.append(strArray[0]);
			if (isNumeric(strArray[1])) {
				int i = Integer.parseInt(strArray[1])-1;
				if (list.size() > i) {
					buf.append(list.get(i));
				} else {
					System.out.println("û�е�"+(i+1)+"��");
				}
			} else {
				System.out.println("��������");
			}
			strArray = strArray[2].split("\\{\\{|\\}\\}", 3);
		}
		if (strArray.length == 1) {
			buf.append(strArray[0]);
		} else {
			System.out.println("ģ���ļ���ʽ����");
		}
		return buf.toString();
	}
	
	/**
	 * �ж��ַ����Ƿ�����
	 * @param s 
	 */
	public static boolean isNumeric(String s) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(s).matches();
	}
}
