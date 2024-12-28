package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.service.CategoryService;
import dev.petproject.service.ProductService;
import jakarta.servlet.http.HttpSession;
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
@SessionAttributes("product")
public class ProductController {

    public static final String PRODUCTS = "products";
    public static final String EDIT_PRODUCT = "edit";
    public static final String REDIRECT_PRODUCTS_ALL = "redirect:/products/all";
    public static final String CATEGORIES = "categories";
    private final ProductService productService;
    private final CategoryService categoryService;


    @GetMapping("/home")
    public String homePage(Model model) {
        return "index";
    }

    @GetMapping("/products/all")
    public String viewProductsWithPaginated(Product product, Model model) {
        findPaginated(1, "name", "asc", model, product);

        model.addAttribute(CATEGORIES, categoryService.getAllCategories());
        return PRODUCTS;
    }

    @GetMapping("/products/add")
    public String addNewProductPage(Model model, HttpSession session) {

        model.addAttribute(CATEGORIES, categoryService.getAllCategories());
        model.addAttribute("category", getProductFromSession(session));

        return EDIT_PRODUCT;
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid Product product, BindingResult result, Model model) {
        model.addAttribute(CATEGORIES, categoryService.getAllCategories());

        if (result.hasErrors()) {
            return EDIT_PRODUCT;
        }
        productService.saveProduct(product);
        return REDIRECT_PRODUCTS_ALL;
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable(value = "id") Integer id, Model model, HttpSession session) {

        Product product = productService.findProductById(id);
        model.addAttribute("product", product);
        session.setAttribute("product", product);
        model.addAttribute(CATEGORIES, categoryService.getAllCategories());
        model.addAttribute("category", new Category());

        return EDIT_PRODUCT;
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Integer id) {
        productService.deleteProductById(id);
        return REDIRECT_PRODUCTS_ALL;
    }

    @GetMapping("/products/find")
    public String findProductsByKeyword(Model model, String keyword) {
        List<Product> products = productService.searchProductsByKeyword(keyword);
        model.addAttribute(PRODUCTS, products);

        return "search";
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable("pageNo") int pageNo,
                                @RequestParam("sort-field") String sortField,
                                @RequestParam("sort-dir") String sortDir,
                                Model model, Product product) {
        int pageSize = 3;
        Page<Product> page = productService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Product> products = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(PRODUCTS, products);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("product", new Product());

        return PRODUCTS;
    }

    @ModelAttribute("product")
    public Product getProductFromSession(HttpSession session) {
        Product product = (Product) session.getAttribute("product");
        return product != null ? product : new Product();
    }

}
