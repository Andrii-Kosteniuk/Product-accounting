package dev.petproject.controller;

import dev.petproject.domain.Product;
import dev.petproject.repository.ProductRepository;
import dev.petproject.service.ExportToExcelService;
import dev.petproject.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class ExcelController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @GetMapping("/products/export-to-excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        int sizeOfProducts = (int) productRepository.count();

        Page<Product> products = productService.findPaginated(1, sizeOfProducts, "name", "asc");

        ExportToExcelService service = new ExportToExcelService(products.getContent());

        service.export(response);
    }
}
