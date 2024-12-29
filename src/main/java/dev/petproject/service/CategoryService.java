package dev.petproject.service;

import dev.petproject.domain.Category;
import dev.petproject.exception.CategoryAlreadyExistsException;
import dev.petproject.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @CachePut("categories")
    public void saveNewCategory(Category category) {
        if (! categoryRepository.existsByName(category.getName())) {
            categoryRepository.save(category);
        } else {
            throw new CategoryAlreadyExistsException("Such a category already exists");
        }
    }

    @Cacheable("categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

}
