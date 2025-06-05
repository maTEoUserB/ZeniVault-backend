package pl.finances.finances_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.finances.finances_app.dto.CategoryToListDTO;
import pl.finances.finances_app.repositories.CategoryRepository;
import pl.finances.finances_app.repositories.entities.CategoryEntity;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides business logic for managing categories in the system.
 */
@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * Constructs a new CategoryService with the required repository.
     *
     * @param categoryRepository the repository for category data access
     */
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Optional<CategoryEntity> findCategoryById(long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public ResponseEntity<Set<CategoryToListDTO>> findAllCategories(String categoryType) {
        Set<CategoryToListDTO> categories = categoryRepository.getAllByTypeForCategory(categoryType)
                .orElseThrow(() -> new RuntimeException("Categories not found"))
                .stream().map(category -> new CategoryToListDTO(category.getId(), category.getCategoryName()))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(categories);
    }
}
