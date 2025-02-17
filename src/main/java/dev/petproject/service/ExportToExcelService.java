package dev.petproject.service;

import dev.petproject.domain.Product;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExportToExcelService {
    private final SXSSFWorkbook workbook;
    private final List<Product> products;
    private SXSSFSheet sheet;

    public ExportToExcelService(List<Product> products) {
        this.products = products;
        workbook = new SXSSFWorkbook(100);
    }

    private static void setBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.DASHED);
        style.setBorderLeft(BorderStyle.DASHED);
        style.setBorderRight(BorderStyle.DASHED);
        style.setBorderTop(BorderStyle.DASHED);
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Products");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setItalic(true);
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        setBorder(style);


        createCell(row, 0, "Id", style);
        createCell(row, 1, "Name", style);
        createCell(row, 2, "Price", style);
        createCell(row, 3, "Description", style);
        createCell(row, 4, "Category", style);

    }

    private void createCell(Row row, int columnCount, Object data, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellStyle(style);

        if (data instanceof Integer) {
            cell.setCellValue((Integer) data);
        } else if (data instanceof Double) {
            cell.setCellValue((Double) data);
        } else {
            cell.setCellValue((String) data);

        }

        int length = data.toString().length();
        sheet.setColumnWidth(columnCount, (length + 2) * 256 );
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);

        for (Product product : products) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;


            createCell(row, columnCount++, product.getId(), style);
            createCell(row, columnCount++, product.getName(), style);
            createCell(row, columnCount++, product.getPrice(), style);
            createCell(row, columnCount++, product.getDescription(), style);
            createCell(row, columnCount, product.getCategory().getName(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
    }
}
