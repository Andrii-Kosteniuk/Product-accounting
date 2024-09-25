package dev.petproject.excel;

import dev.petproject.domain.Product;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class ExcelHelper {
    private final XSSFWorkbook workbook;
    private final List<Product> products;
    private XSSFSheet sheet;

    public ExcelHelper(List<Product> products) {
        this.products = products;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Products");

        Row row = sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 1, "Id", style);
        createCell(row, 2, "Name", style);
        createCell(row, 3, "Price", style);
        createCell(row, 4, "Description", style);
        createCell(row, 5, "Category", style);

    }

    private void createCell(Row row, int columnCount, Object data, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);

        if (data instanceof Integer) {
            cell.setCellValue((Integer) data);
        } else if (data instanceof Boolean) {
            cell.setCellValue((Boolean) data);
        } else if (data instanceof Double) {
            cell.setCellValue((Double) data);
        } else cell.setCellValue((String) data);
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Product product : products) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;


            createCell(row, columnCount++, product.getId(), style);
            sheet.autoSizeColumn(columnCount);
            createCell(row, columnCount++, product.getName(), style);
            sheet.autoSizeColumn(columnCount);
            createCell(row, columnCount++, product.getPrice(), style);
            sheet.autoSizeColumn(columnCount);
            createCell(row, columnCount++, product.getDescription(), style);
            sheet.autoSizeColumn(columnCount,true);
            createCell(row, columnCount, product.getCategory().getName(), style);
            sheet.autoSizeColumn(columnCount, true);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
