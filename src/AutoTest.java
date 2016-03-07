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
 * AutoTest�Ǳ���Ŀ�����࣬���������main����ҵ����صĺ���
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
	 * ���������ļ�����Configuration�ľ�̬����
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
	 * ��ȡģ���ļ��ַ���
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
	 * �������ģ���������ѡ��ͨ�ŷ�ʽ
	 * @param message - ����ı���
	 */
	private void exchangeMessage(String message) {
		if ("socket".equals(Configuration.NETWORK)) {
			SocketClient.start(message);
		}
	}
	
	/**
	 * ���������ļ���׺��ѡ���ȡ���ݵķ�ʽ
	 */
	private void handleData() {
		String filePostfix = Configuration.FILE_POSTFIX;
		if ("xls".equals(filePostfix) || "xlsx".equals(filePostfix)) {
			handleExcel();
		}
	}
	
	/**
	 * ����excel�ļ�����ÿһ����������д��dataList�У�Ȼ���滻�ַ����γɱ��ģ������䱨��
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
							// double����
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
				// �滻�ַ������γɱ���
				message = Method.replaceString(tmp, dataList);
				System.out.println(message);
				// ���䱨��
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

