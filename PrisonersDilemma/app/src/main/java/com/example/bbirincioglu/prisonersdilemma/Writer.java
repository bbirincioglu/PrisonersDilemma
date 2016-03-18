package com.example.bbirincioglu.prisonersdilemma;

import android.content.Context;
import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by bbirincioglu on 3/18/2016.
 */
public class Writer {
    private Context context;

    public Writer(Context context) {
        this.context = context;
    }

    public void writeExcel(String fileName, String sheetName, String[] headers, List<Object> gameResults) {
        //String directory = Environment.getExternalStorageDirectory() + "/Documents/";
        File excelFile = new File("/sdcard/" + fileName);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        writeHeaders(sheet, headers);
        writeGameResults(sheet, gameResults);

        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/

        try {
            if (!excelFile.exists()) {
                excelFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(excelFile);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeHeaders(HSSFSheet sheet, String[] headers) {
        HSSFRow headerRow = sheet.createRow(0);
        int length = headers.length;

        HSSFCellStyle p1CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.RED.index, true, sheet.getWorkbook());
        HSSFCellStyle p2CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_BLUE.index, true, sheet.getWorkbook());
        HSSFCellStyle gameSettingsCellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_GREEN.index, true, sheet.getWorkbook());

        for (int i = 0; i < length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            if (i == 0) {
                cell.setCellStyle(gameSettingsCellStyle);
            } else if (0 < i && i <= 4) {
                cell.setCellStyle(p1CellStyle);
            } else if (4 < i && i <= 8) {
                cell.setCellStyle(p2CellStyle);
            } else {
                cell.setCellStyle(gameSettingsCellStyle);
            }
        }
    }

    private void writeGameResults(HSSFSheet sheet, List<Object> gameResults) {
        int size = gameResults.size();
        HSSFCellStyle p1CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.RED.index, true, sheet.getWorkbook());
        HSSFCellStyle p2CellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_BLUE.index, true, sheet.getWorkbook());
        HSSFCellStyle gameSettingsCellStyle = createCellStyle(HSSFColor.BLACK.index, HSSFColor.LIGHT_GREEN.index, true, sheet.getWorkbook());

        for (int i = 0; i < size; i++) {
            HSSFRow row = sheet.createRow(i + 1);
            StringTokenizer tokenizer = new StringTokenizer(gameResults.get(i).toString(), GameResult.SPLIT_WITH);
            int j = 0;

            while (tokenizer.hasMoreTokens()) {
                String cellValue = tokenizer.nextToken();
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(cellValue);

                if (j == 0) {
                    cell.setCellStyle(gameSettingsCellStyle);
                } else if (0 < j && j <= 4) {
                    cell.setCellStyle(p1CellStyle);
                } else if (4 < j && j <= 8) {
                    cell.setCellStyle(p2CellStyle);
                } else {
                    cell.setCellStyle(gameSettingsCellStyle);
                }

                j++;
            }
        }
    }

    public HSSFCellStyle createCellStyle(short foreground, short background, boolean wrapText, HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setColor(foreground);
        cellStyle.setFont(font);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(background);
        cellStyle.setWrapText(wrapText);
        return cellStyle;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
