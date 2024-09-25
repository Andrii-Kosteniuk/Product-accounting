package dev.petproject.controller;

import dev.petproject.domain.Product;
import dev.petproject.service.CategoryService;
import dev.petproject.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;


    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/products/all")
    public String viewProductsWithPaginated(Model model) {
        findPaginated(1, "name", "asc", model);
        return "products";
    }

    @GetMapping("/products/add")
    public String createProduct(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "edit";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid Product product, BindingResult result, Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());

        if (result.hasErrors()) {
            return "edit";
        }
        productService.saveProduct(product);
        return "redirect:/products/all";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(Model model, @ModelAttribute("product") Product product, @PathVariable(value = "id") Integer id) {

        model.addAttribute("product", productService.findProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());

        return "edit";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Integer id) {

        productService.deleteProductById(id);

        return "redirect:/products/all";
    }

    @GetMapping("/products/find")
    public String findProductsByKeyword(Model model, String keyword) {
        List<Product> products = productService.searchProductsByKeyword(keyword);
        model.addAttribute("products", products);

        return "search";
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable("pageNo") int pageNo,
                                @RequestParam("sort-field") String sortField,
                                @RequestParam("sort-dir") String sortDir,
                                Model model) {
        int pageSize = 3;
        Page<Product> page = productService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Product> products = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("products", products);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "products";
    }

}
