package _1danhebojo.coalarm.coalarm_service.domain.alert.repository;

import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity.Alert;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity.AlertHistory;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.jpa.AlertSSEJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AlertSSERepositoryImpl {
    private final AlertSSEJpaRepository alertSSEJpaRepository;

    public AlertSSERepositoryImpl(AlertSSEJpaRepository alertSSEJpaRepository) {
        this.alertSSEJpaRepository = alertSSEJpaRepository;
    }

    @Transactional(readOnly = true)
    public List<Alert> findByUserId(Long userId) {
        return alertSSEJpaRepository.findByUserId(userId);
    }

    public void save(AlertHistory history) {
    }

}
