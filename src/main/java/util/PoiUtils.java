package util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Utils to operate excels with POI
 * 
 * @author Playboy
 * @since 2010-12-22
 */
public class PoiUtils {
	private static final NumberFormat FMT_NUMBER = new DecimalFormat("0.####");

	public static int getIntValue(XSSFCell cell) {
		if ((cell == null) || (cell.toString().trim().length() == 0)) {
			return 0;
		}
		if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
			return (int) cell.getNumericCellValue();
		return (int) Double.parseDouble(cell.toString());
	}

	public static long getLongValue(XSSFCell cell) {
		if ((cell == null) || (cell.toString().trim().length() == 0)) {
			return 0L;
		}
		if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
			return (long) cell.getNumericCellValue();
		return Long.parseLong(cell.getRawValue());
	}

	public static short getShortValue(XSSFCell cell) {
		if ((cell == null) || (cell.toString().length() == 0)) {
			return 0;
		}
		return (short) (int) Double.parseDouble(cell.toString());
	}

	public static byte getByteValue(XSSFCell cell) {
		if ((cell == null) || (cell.toString().length() == 0)) {
			return 0;
		}
		return (byte) (int) Double.parseDouble(cell.toString());
	}

	public static double getDoubleValue(XSSFCell cell) {
		if (cell == null) {
			return 0.0;
		}
		return cell.getNumericCellValue();
	}

	public static Date getDateValue(XSSFCell cell) {
		if ((cell != null) && (cell.toString().length() > 0)) {
			return cell.getDateCellValue();
		}
		return null;
	}

	public static String getStringValue(XSSFCell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.toString().trim();
		case Cell.CELL_TYPE_NUMERIC:
			String str = FMT_NUMBER.format(cell.getNumericCellValue());
			if (str.endsWith(".0")) {
				return str.substring(0, str.length() - 2);
			}
			return str;
		case Cell.CELL_TYPE_FORMULA:
			return cell.getRichStringCellValue().getString();
		}
		return cell.toString().trim();
	}

	public static float getFloatValue(XSSFCell cell) {
		if (cell == null)
			return 0.0f;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
			return (float) cell.getNumericCellValue();
		}
		String cellStr = cell.toString();
		if (cellStr.length() == 0)
			return 0.0F;
		if (cellStr.equals("0%")) {
			return 0f;
		}
		if (cellStr.indexOf('%') > 0) {
			return getPercentum(cell);
		}
		return Float.parseFloat(cellStr);
	}

	public static String getIntString(XSSFCell cell) {
		return "" + getIntValue(cell);
	}

	public static boolean isEmpty(XSSFRow row) {
		if (row == null) {
			return true;
		}
		XSSFCell cell = row.getCell(0);
		if ((cell == null) || (cell.toString().length() == 0)) {
			return true;
		}
		return false;
	}

	public static float getPercentum(XSSFCell cell) {
		if ((cell == null) || (cell.toString().length() == 0)) {
			return 0.0f;
		}
		try {
			Number parse = NumberFormat.getPercentInstance().parse(cell.toString());
			return parse.floatValue();
		} catch (ParseException e) {
			throw new InvalidParameterException("无法将此格式转换成小数：" + cell.toString());
		}
	}

	/**
	 * 将列索引转成由由A-Z表示的列名,支持范围A~YZ
	 * 
	 * @param columnIndex
	 *          有效范围0~26^2-1(675)
	 * @return
	 */
	public static String toExcelColumnName(int columnIndex) {
		if (columnIndex > 26 * 26 - 1) {
			throw new IndexOutOfBoundsException(String.valueOf(columnIndex));
		}
		String _26RadixNum = Integer.toString(columnIndex, 26);
		StringBuilder excelColName = new StringBuilder();
		if (_26RadixNum.length() == 1) {
			excelColName.append(convertAAs0(_26RadixNum.charAt(0)));
		} else if (_26RadixNum.length() == 2) {
			excelColName.append(convertAAs1(_26RadixNum.charAt(0)));
			for (int i = 1; i < _26RadixNum.length(); ++i) {
				excelColName.append(convertAAs0(_26RadixNum.charAt(i)));
			}
		}
		return excelColName.toString();
	}

	private static char convertAAs1(char c) {
		char colChar;
		if (c >= '1' && c <= '9') {
			colChar = (char) (c + ('A' - '1')); // 1~9 -> A->I
		} else if (c != '0') {
			colChar = (char) (c - ('a' - 'J')); // a~p -> J~Z
		} else {
			colChar = c;
		}
		return colChar;
	}

	private static char convertAAs0(char c) {
		char colChar;
		if (c >= '0' && c <= '9') {
			colChar = (char) (c + ('A' - '0')); // 0~9 -> A->J
		} else {
			colChar = (char) (c - ('a' - 'K')); // a~p -> K~Z
		}
		return colChar;
	}
}