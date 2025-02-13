package dev.petproject.service;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExportToExcelServiceTest {

    private ExportToExcelService exportToExcelService;
    private List<Product> products;

    @BeforeEach
    void setUp() {

        Category category = new Category(1, "Electronics", new ArrayList<>());
        products = List.of(
                new Product(1, "Laptop", 999.99, 5, "Gaming Laptop", category),
                new Product(2, "Phone", 699.49, 10, "Latest smartphone", category)
        );

        exportToExcelService = new ExportToExcelService(products);
    }

    @Test
    void testHeaderCreation() throws IOException {
        Row row;
        CellStyle style;
        XSSFFont font;
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");
            row = sheet.createRow(0);

            style = workbook.createCellStyle();
            font = (XSSFFont) workbook.createFont();
        }
        font.setItalic(true);
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);


        String[] expectedHeaders = {"Id", "Name", "Price", "Description", "Category"};
        for (int i = 0; i < expectedHeaders.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(expectedHeaders[i]);
        }

        for (int i = 0; i < expectedHeaders.length; i++) {
            assertEquals(expectedHeaders[i], row.getCell(i).getStringCellValue());
        }
    }

    @Test
    void testWriteDataLines() {
        Sheet sheet;
        try (Workbook workbook = new SXSSFWorkbook()) {
            sheet = workbook.createSheet("Products");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int rowCount = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getPrice());
            row.createCell(3).setCellValue(product.getDescription());
            row.createCell(4).setCellValue(product.getCategory().getName());
        }


        assertEquals(products.size(), sheet.getPhysicalNumberOfRows());


        Row firstProductRow = sheet.getRow(1);
        assertEquals(products.get(0).getId(), (int) firstProductRow.getCell(0).getNumericCellValue());
        assertEquals(products.get(0).getName(), firstProductRow.getCell(1).getStringCellValue());
        assertEquals(products.get(0).getPrice(), firstProductRow.getCell(2).getNumericCellValue());
        assertEquals(products.get(0).getDescription(), firstProductRow.getCell(3).getStringCellValue());
        assertEquals(products.get(0).getCategory().getName(), firstProductRow.getCell(4).getStringCellValue());
    }

    @Test
    void testExportWritesToResponse() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);


        exportToExcelService.export(response);

        verify(response, times(1)).getOutputStream();
        verify(outputStream, times(1)).close();
    }
}