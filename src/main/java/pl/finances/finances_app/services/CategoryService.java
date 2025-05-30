package pl.finances.finances_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.finances.finances_app.dto.requestsAndResponsesDto.CategoryToListDTO;
import pl.finances.finances_app.repositories.CategoryRepository;
import pl.finances.finances_app.repositories.entities.CategoryEntity;

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

    public ResponseEntity<Set<CategoryToListDTO>> findAllCategories(String categoryType) {
        Set<CategoryToListDTO> categories = categoryRepository.getAllByTypeForCategory(categoryType)
                .orElseThrow(() -> new RuntimeException("Categories not found"))
                .stream().map(category -> new CategoryToListDTO(category.getId(), category.getCategoryName()))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(categories);
    }
}
