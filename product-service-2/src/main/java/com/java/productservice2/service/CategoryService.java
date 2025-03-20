package com.java.productservice2.service;

import com.java.productservice2.controller.dto.CategoryIdResponse;
import com.java.productservice2.controller.dto.CategoryRequest;
import com.java.productservice2.controller.dto.CategoryResponse;
import com.java.productservice2.entity.Category;
import com.java.productservice2.exception.CategoryNotFoundException;
import com.java.productservice2.mapper.CategoryMapper;
import com.java.productservice2.repository.CategoryRepository;
import com.java.productservice2.util.MessageExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.categoryRequestToCategory(request);
        Category saved = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryResponse(saved);
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::categoryToCategoryResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.categoryToCategoryResponse(
                categoryRepository.findById(id)
                        .orElseThrow(() -> new CategoryNotFoundException(MessageExceptionUtil.CategoryNotFoundWithId.formatted(id)))
        );
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(MessageExceptionUtil.CategoryNotFoundWithId.formatted(id)));
        category.setName(request.getName());
        return categoryMapper.categoryToCategoryResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        if(!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(MessageExceptionUtil.CategoryNotFoundWithId.formatted(id));
        }
        categoryRepository.deleteById(id);
    }

    public boolean existsCategory(Long id) {
        return categoryRepository.existsById(id);
    }

}
