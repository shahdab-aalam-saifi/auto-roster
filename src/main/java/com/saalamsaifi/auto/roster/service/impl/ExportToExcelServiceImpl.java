package com.saalamsaifi.auto.roster.service.impl;

import static com.saalamsaifi.auto.roster.constant.ProjectConstant.SEPARATOR;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Table;
import com.saalamsaifi.auto.roster.service.ExportService;

@Service
public class ExportToExcelServiceImpl implements ExportService {
	private static final String TEAM = "Team";
	private static final String NAME = "Name";

	/**
	 *
	 */
	@Override
	public void export(Table<String, String, String> table, String fileName) {
		Set<String> rowKeys = table.rowKeySet();
		Set<String> colKeys = table.columnKeySet();
		int rowNumber = 0;
		int colNumber = 0;

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet();

			Row headerRow = sheet.createRow(rowNumber++);

			setCellValue(headerRow, colNumber++, TEAM);
			setCellValue(headerRow, colNumber++, NAME);

			for (String date : colKeys) {
				setCellValue(headerRow, colNumber++, date);
			}

			for (String r : rowKeys) {
				colNumber = 0;
				String[] t = r.split(SEPARATOR);
				Row row = sheet.createRow(rowNumber++);
				setCellValue(row, colNumber++, t[0]);
				setCellValue(row, colNumber++, t[1]);

				for (String c : colKeys) {
					setCellValue(row, colNumber++, table.get(r, c));
				}
			}

			int columns = colKeys.size() + 2;
			int rows = rowKeys.size();

			// Formating of output
			autoSizeColumn(sheet, columns);
			mergeCell(sheet, rows, 0);
			styleRow(sheet, 0, columns);

			// Write output to file
			write(workbook, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param row
	 * @param colNumber
	 * @param value
	 */
	private void setCellValue(final Row row, int colNumber, String value) {
		Cell cell = row.createCell(colNumber);
		if (!StringUtils.isEmpty(value)) {
			cell.setCellValue(value);
		}
	}

	/**
	 * @param workbook
	 * @param fileName
	 */
	private void write(final Workbook workbook, String fileName) {
		try (FileOutputStream stream = new FileOutputStream(fileName)) {
			workbook.write(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param sheet
	 * @param size
	 */
	private void autoSizeColumn(final Sheet sheet, int size) {
		for (int i = 0; i < size; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	/**
	 * @param sheet
	 * @param rows
	 * @param column
	 */
	private void mergeCell(final Sheet sheet, int rows, int column) {
		int i = 0;

		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		while (i <= rows) {
			Cell cell = sheet.getRow(i).getCell(column);
			while (i + 1 <= rows
					&& cell.getStringCellValue().equals(sheet.getRow(i + 1).getCell(column).getStringCellValue())) {
				i++;
			}
			if (i - cell.getRowIndex() > 0) {
				sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), i, column, column));
				cell.setCellStyle(style);
			}
			i++;
		}
	}

	/**
	 * @param row
	 */
	private void styleRow(final Sheet sheet, int row, int columns) {
		Workbook workbook = sheet.getWorkbook();

		Font font = workbook.createFont();
		font.setBold(true);

		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);

		for (int i = 0; i < columns; i++) {
			sheet.getRow(row).getCell(i).setCellStyle(style);
		}
	}

}
