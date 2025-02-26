package com.java.productservice2.mapper;

import com.java.productservice2.controller.dto.CategoryRequest;
import com.java.productservice2.controller.dto.CategoryResponse;
import com.java.productservice2.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category categoryRequestToCategory(CategoryRequest categoryRequest);

    CategoryResponse categoryToCategoryResponse(Category category);
}
