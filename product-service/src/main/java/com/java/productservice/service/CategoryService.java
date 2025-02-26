package com.java.productservice.service;

import com.java.productservice.controller.dto.CategoryRequest;
import com.java.productservice.controller.dto.CategoryResponse;
import com.java.productservice.entity.Category;
import com.java.productservice.exception.CategoryNotFoundException;
import com.java.productservice.mapper.CategoryMapper;
import com.java.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Long createCategory(CategoryRequest request) {
        Category category = categoryMapper.categoryRequestToCategory(request);
        return categoryRepository.save(category).getId();
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::categoryToCategoryResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.categoryToCategoryResponse(
                categoryRepository.findById(id)
                        .orElseThrow(() -> new CategoryNotFoundException("Unable to find category with id: " + id))
        );
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Unable to find category with id: " + id));
        category.setName(request.getName());
        return categoryMapper.categoryToCategoryResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        if(!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Unable to find category with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public boolean existsCategory(Long id) {
        return categoryRepository.existsById(id);
    }

}
