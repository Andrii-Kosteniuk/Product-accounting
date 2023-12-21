package com.example.springmvc.root;

import com.example.springmvc.model.Product;
import com.example.springmvc.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {

        Product bmw = new Product();
        bmw.setName("BMW car");
        bmw.setDescription("BMW car drive very fast");
        bmw.setType("Sedan");
        bmw.setCategory("Sport-car");
        bmw.setPrice(55000.0);

        productRepository.save(bmw);


        Product audi = new Product();
        audi.setName("Audi car");
        audi.setDescription("Audi car drive very fast");
        audi.setType("Coupe");
        audi.setCategory("Sport-car");
        audi.setPrice(45850.0);

        productRepository.save(audi);
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
