package util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ExcelFile implements Closeable {

	public enum PathType {
		/**
		 * classpath路径
		 */
		CLASSPATH,
		/**
		 * 绝对路径
		 */
		ABSOLUTE
	}


	private final String filePath;

	private final PathType pathType;

	private XSSFWorkbook workbook;

	private InputStream input;

	/** 列名字到列索引的映射 key:名字，value:索引 */
	private Map<String, Integer> columnBiMap;

	/**
	 * @param path 文件路径
	 * @param pathType 路径类型
	 */
	protected ExcelFile(String path, PathType pathType) {
		this.filePath = path;
		this.pathType = pathType;
	}

	/**
	 * 打开文件
	 */
	public ExcelFile open() {
		switch (pathType) {
			case CLASSPATH:
				openClasspath();
				break;
			case ABSOLUTE:
				openAbsolute();
				break;
		}
		return this;
	}

	private void openClasspath() {
		try {
			URL url = getClass().getClassLoader().getResource(filePath);
			URLConnection conn = url.openConnection();
			conn.setUseCaches(false);
			this.input = conn.getInputStream();
			this.workbook = new XSSFWorkbook(this.input);
		} catch (Exception e) {
			close();
			throw new RuntimeException("Excel open fail: " + this.filePath, e);
		}
	}

	private void openAbsolute() {
		try {
			this.input = new FileInputStream(this.filePath);
			this.workbook = new XSSFWorkbook(this.input);
		} catch (Exception e) {
			close();
			throw new RuntimeException("Excel open fail: " + this.filePath, e);
		}
	}

	/**
	 * 关闭文件
	 */
	@Override
	public void close() {
		if (this.workbook != null) {
			this.workbook = null;
		}
		if (this.input != null) {
			try {
				this.input.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * excel的一行
	 *
	 * @author liul
	 * @since 2013年9月2日
	 *
	 */
	public class Row {

		private XSSFRow xssfRow;

		private int curCellIndex;

		public Row(XSSFRow hssfRow) {
			this.xssfRow = hssfRow;
			this.curCellIndex = 0;
		}

		public boolean hasColumn(String column) {
			return ExcelFile.this.columnBiMap.containsKey(column);
		}

		private XSSFCell getCellByColumnName(String column) {
			if (!ExcelFile.this.columnBiMap.containsKey(column)) {
				throw new RuntimeException("找不到列: " + column);
			}
			this.curCellIndex = ExcelFile.this.columnBiMap.get(column);
			return this.xssfRow.getCell(this.curCellIndex);
		}

		public int readInt(String column) {
			XSSFCell cell = getCellByColumnName(column);
			return PoiUtils.getIntValue(cell);
		}

		public long readLong(String column) {
			return Long.parseLong(readString(column));
		}

		public double readDouble(String column) {
			XSSFCell cell = getCellByColumnName(column);
			return PoiUtils.getDoubleValue(cell);
		}

		public float readFloat(String column){
			XSSFCell cell = getCellByColumnName(column);
			return PoiUtils.getFloatValue(cell);
		}

		public String readString(String column) {
			XSSFCell cell = getCellByColumnName(column);
			return PoiUtils.getStringValue(cell);
		}

		public Date readDate(String column) {
			XSSFCell cell = getCellByColumnName(column);
			return PoiUtils.getDateValue(cell);
		}




		public void assertNotNull(Object obj, Object originCellData, String msg, Object... args) {
			if (obj == null) {
				fail(originCellData, msg, args);
			}
		}


		public void fail(Object value, String format, Object... args) {
			String msg = String.format(format, args);
			if (value != null) {
				throw new RowParseException(value, "原因: " + msg);
			} else {
				throw new RuntimeException("原因: " + msg);
			}
		}

	}

	public interface RowParser {
		void parse(Row row);
	}

	public interface ExcelParser {
		void parse(ExcelFile excel);
	}

	@SuppressWarnings("serial")
	public static class RowParseException extends RuntimeException {

		private Object value;

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public RowParseException(Object value, String msg) {
			super(msg);
			this.value = value;
		}

	}

	public void foreachRow(int sheetIndex, RowParser parser) {
		XSSFSheet sheet = this.workbook.getSheetAt(sheetIndex);
		if (!foreachRow(sheet, parser)) {
			throw new RuntimeException();
		}
	}

	public void foreachRow(String sheetName, RowParser parser) {
		XSSFSheet sheet = this.workbook.getSheet(sheetName);
		if (sheet == null) {
			throw new IllegalArgumentException("cannot find sheet " + sheetName + " in " + this.filePath);
		}
		if (!foreachRow(sheet, parser)) {
			throw new RuntimeException();
		}
	}

	public boolean hasSheet(String sheetName) {
		return this.workbook.getSheet(sheetName) != null;
	}

	private boolean foreachRow(XSSFSheet sheet, RowParser parser) {
		makeColumnMap(sheet);
		int lastRowNum = sheet.getLastRowNum(); // 0-based
		boolean isSuccess = true;
		for (int i = 1; i <= lastRowNum; ++i) {
			XSSFRow xssfRow = sheet.getRow(i);
			if (PoiUtils.isEmpty(xssfRow)) {
				continue;
			}
			Row row = new Row(xssfRow);
			try {
				parser.parse(row);
			} catch (Exception e) {
				isSuccess = false;
			}
		}
		return isSuccess;
	}

	private void makeColumnMap(XSSFSheet sheet) {
		Map<String, Integer> builder = new HashMap<>();
		XSSFRow xssfRow = sheet.getRow(0);
		for (int i = 0; i < xssfRow.getLastCellNum(); ++i) {
			XSSFCell cell = xssfRow.getCell(i);
			String name = PoiUtils.getStringValue(cell);
			if (name != null && !name.isEmpty()) {
				builder.put(name.trim(), i);
			}
		}
		this.columnBiMap = builder;
	}

	/**
	 * 解析excel，路径类型默认为classpath
	 */
	public static void parse(String filePath, ExcelParser parser) {
		parse(filePath, PathType.CLASSPATH, parser);
	}

	/**
	 * 解析excel，指定路径类型
	 */
	public static void parse(String filePath, PathType pathType, ExcelParser parser) {
		try (ExcelFile excel = new ExcelFile(filePath, pathType).open()) {
			parser.parse(excel);
		}
	}

	public String filePath(){
		return this.filePath;
	}

}
