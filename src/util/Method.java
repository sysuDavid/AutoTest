package util;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Method是工具类，其方法是业务无关的通用方法，多为static函数
 * @author yuanwj
 *
 */
public class Method {
	
	public static String getFilePostfix(String s) {
		return s.substring(s.lastIndexOf(".")+1);
	}
	/**
	 * 删除数字字符串后面的小数点及0，
	 * 用于整数情况下double变量默认的一位小数点
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
	 * replace可以替换字符串的指定部分
	 * @param str - 被替换的字符串
	 * @param list - 用于替换字符串的一组String变量
	 * @return 替换后的字符串
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
					System.out.println("没有第"+(i+1)+"列");
				}
			} else {
				System.out.println("不是数字");
			}
			strArray = strArray[2].split("\\{\\{|\\}\\}", 3);
		}
		if (strArray.length == 1) {
			buf.append(strArray[0]);
		} else {
			System.out.println("模板文件格式出错");
		}
		return buf.toString();
	}
	
	/**
	 * 判断字符串是否数字
	 * @param s 
	 */
	public static boolean isNumeric(String s) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(s).matches();
	}
}
