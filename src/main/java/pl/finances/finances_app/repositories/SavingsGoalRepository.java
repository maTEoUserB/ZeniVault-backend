package pl.finances.finances_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.finances.finances_app.dto.SavingsGoalDTO;
import pl.finances.finances_app.repositories.entities.SavingsGoalEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, Long> {
    Optional<SavingsGoalDTO> findFirstByUserAccount_IdOrderByGoalDeadlineAsc(long userAccountId); //metoda Spring Data JPA - automatycznie wybiera dane do SavingsGoalDTO
    List<SavingsGoalEntity> findAllByUserAccount_Id(long userAccountId);
}
