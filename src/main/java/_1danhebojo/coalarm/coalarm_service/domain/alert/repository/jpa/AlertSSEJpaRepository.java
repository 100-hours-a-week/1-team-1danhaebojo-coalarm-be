package _1danhebojo.coalarm.coalarm_service.domain.alert.repository.jpa;

import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertSSEJpaRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a JOIN FETCH a.coin WHERE a.userId = :userId AND a.active = true")
    List<Alert> findByUserId(Long userId);

    @Query("SELECT MAX(h.registeredDate) FROM AlertHistory h WHERE h.alert.alertId = :alertId")
    LocalDateTime findLastAlertTime(Long alertId);

    @Query("SELECT a FROM Alert a WHERE a.userId = :userId AND a.active = true")
    List<Alert> findActiveAlertsByUserId(Long userId);
}
