package dev.petproject.controller;

import dev.petproject.domain.Category;
import dev.petproject.domain.Product;
import dev.petproject.service.CategoryService;
import dev.petproject.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
@SessionAttributes("product")
@Slf4j
public class ProductController {

    public static final String PRODUCTS = "products";
    public static final String EDIT_PRODUCT = "edit";
    public static final String REDIRECT_PRODUCTS_ALL = "redirect:/products";
    public static final String CATEGORIES = "categories";
    private final ProductService productService;
    private final CategoryService categoryService;
    @Value("${spring.app.pageSize}")
    int pageSize;


    @GetMapping()
    public String viewProductsWithPaginated(Model model, HttpSession session) {
        log.info("Viewing all products with pagination");
        findPaginated(1, "name", "asc", model, session);

        model.addAttribute(CATEGORIES, categoryService.getAllCategories());
        model.addAttribute("successCreateProduct", "New product has been created successfully!");
        model.addAttribute("errorSearch");

        return PRODUCTS;
    }

    @GetMapping("/add")
    public String addNewProductPage(Model model, HttpSession session) {
        log.info("Accessing add new product page");
        model.addAttribute(CATEGORIES, categoryService.getAllCategories());
        model.addAttribute("category", getProductFromSession(session));

        return EDIT_PRODUCT;
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, BindingResult result, Model model, HttpSession session) {
        log.info("Attempting to save product: {}", product);
        model.addAttribute(CATEGORIES, categoryService.getAllCategories());
        model.addAttribute("product", product);
        model.addAttribute("category", new Category());

        session.setAttribute("product", product);
        session.setAttribute("category", new Category());
        session.setAttribute("categories", categoryService.getAllCategories());

        if (result.hasErrors()) {
            log.warn("Validation errors occurred while saving product: {}", result.getAllErrors());
            return EDIT_PRODUCT;
        }

        productService.saveProduct(product);
        log.info("Product saved successfully: {}", product);

        return REDIRECT_PRODUCTS_ALL + "?success=true";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable(value = "id") Integer id,
                              Model model,
                              HttpSession session) {
        log.info("Editing product with ID: {}", id);

        Product product = productService.findProductById(id);
        List<Category> allCategories = categoryService.getAllCategories();

        session.setAttribute("product", product);
        session.setAttribute("category", product.getCategory());
        session.setAttribute("categories", allCategories);

        model.addAttribute(CATEGORIES, allCategories);
        model.addAttribute("product", product);
        model.addAttribute("category", new Category());
        model.addAttribute("errorCreateProduct");
        model.addAttribute("errorCreateNewCategory");

        return EDIT_PRODUCT;
    }


    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Integer id, HttpSession session) {
        log.info("Deleting product with ID: {}", id);

        productService.deleteProductById(id);

        int currentPage = (int) session.getAttribute("currentPage");
        String sortField = (String) session.getAttribute("sortField");
        String sortDir = (String) session.getAttribute("sortDir");


        log.info("Product deleted successfully with ID: {}", id);

        return "redirect:/products/page/" + currentPage + "?sort-field=" + sortField + "&sort-dir=" + sortDir;
    }

    @GetMapping("/find")
    public String findProductsByKeyword(Model model, String keyword) {
        log.info("Searching for products with keyword: {}", keyword);
        List<Product> products;

        products = productService.searchProductsByKeyword(keyword);

        model.addAttribute(PRODUCTS, products);

        return "search";
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable("pageNo") int pageNo,
                                @RequestParam("sort-field") String sortField,
                                @RequestParam("sort-dir") String sortDir,
                                Model model, HttpSession session) {

        Page<Product> page = productService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Product> products = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(PRODUCTS, products);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("product", new Product());

        session.setAttribute("currentPage", pageNo);
        session.setAttribute("sortField", sortField);
        session.setAttribute("sortDir", sortDir);

        return PRODUCTS;
    }

    @ModelAttribute("product")
    public Product getProductFromSession(HttpSession session) {
        Product product = (Product) session.getAttribute("product");
        return product != null ? product : new Product();
    }

}
