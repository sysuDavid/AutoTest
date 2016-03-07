import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;

import global.Configuration;
import net.SocketClient;
import util.Method;
/**
 * AutoTest是本项目的主类，有运行入口main，及业务相关的函数
 * @author yuanwj
 *
 */
public class AutoTest {
	private String template;
	private ArrayList<String> dataList = new ArrayList<String>();
	private String filename;
	
	public AutoTest(String string) {
		filename = string;
	}
	
	/**
	 * 根据配置文件设置Configuration的静态变量
	 */
	private void init() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("properties.ini"));
		} catch (Exception e) {
			System.out.println("properties.ini not exist");
		}
		Configuration.NETWORK = properties.getProperty("network");
		Configuration.IP = properties.getProperty("ip");
		Configuration.PORT = Integer.parseInt(properties.getProperty("port"));
		
		String filePostfix = Method.getFilePostfix(filename);
		Configuration.FILE_POSTFIX = filePostfix;
	}
	
	/**
	 * 读取模板文件字符串
	 */
	private void readTemplate() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("template.txt")));
			String str;
			while ((str = reader.readLine()) != null) {
				template = str;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(template);
	}
	
	/**
	 * 交换报文，根据配置选择通信方式
	 * @param message - 传输的报文
	 */
	private void exchangeMessage(String message) {
		if ("socket".equals(Configuration.NETWORK)) {
			SocketClient.start(message);
		}
	}
	
	/**
	 * 根据数据文件后缀名选择读取数据的方式
	 */
	private void handleData() {
		String filePostfix = Configuration.FILE_POSTFIX;
		if ("xls".equals(filePostfix) || "xlsx".equals(filePostfix)) {
			handleExcel();
		}
	}
	
	/**
	 * 操作excel文件，将每一行数据依次写入dataList中，然后替换字符串形成报文，并传输报文
	 */
	private void handleExcel() {
		try {
			String filePostfix = Configuration.FILE_POSTFIX;
			Workbook workbook = null;
			Sheet sheet = null;
			if ("xlsx".equals(filePostfix)) {
				workbook = new XSSFWorkbook(new FileInputStream(filename));
				sheet = workbook.getSheetAt(0);
			} else {
				workbook = new HSSFWorkbook(new FileInputStream(filename));
				sheet = workbook.getSheetAt(0);
			}
			String dataString = "";
			String message = "";
			for (Row row : sheet) {
				String tmp = template;
				if (!dataList.isEmpty())
					dataList.clear();
				for (Cell cell : row) {
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
							dataString = sdf.format(cell.getDateCellValue());
						} else {
							// double类型
							double value = cell.getNumericCellValue();
							dataString = Method.delZeroAndDot(String.valueOf(value));
						}
						break;
					case Cell.CELL_TYPE_STRING:
						dataString = cell.getStringCellValue();
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						dataString = String.valueOf(cell.getBooleanCellValue());
						break;
					default:
						System.out.println();
					}
					dataList.add(dataString);
				}
				// 替换字符串，形成报文
				message = Method.replaceString(tmp, dataList);
				System.out.println(message);
				// 传输报文
				exchangeMessage(message);
				
				if (workbook != null) {
					workbook.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String args[]) {
		AutoTest autoTest = new AutoTest(args[0]);
		autoTest.init();
		autoTest.readTemplate();
		autoTest.handleData();
	}
	
	
}

