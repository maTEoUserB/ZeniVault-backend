package pl.finances.finances_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.finances.finances_app.dto.TopCategoryDTO;
import pl.finances.finances_app.dto.requestAndResponse.CategoryResponse;
import pl.finances.finances_app.repositories.CategoryRepository;
import pl.finances.finances_app.repositories.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<CategoryEntity> findCategoryById(long id) {
        return categoryRepository.findById(id);
    }

    public ResponseEntity<CategoryResponse> findAllCategories(String categoryType) {
        Set<String> categories = categoryRepository.getAllByTypeForCategory(categoryType)
                .orElseThrow(() -> new RuntimeException("Categories not found"))
                .stream().map(CategoryEntity::getCategoryName).collect(Collectors.toSet());

        CategoryResponse response = new CategoryResponse(categories);
        return ResponseEntity.ok(response);
    }
}
