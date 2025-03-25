package _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "alert_history")
public class AlertHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_history_id")
    private Long alertHistoryId;

    @ManyToOne(fetch = FetchType.LAZY) // 알람 정보
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    @ManyToOne(fetch = FetchType.LAZY) // 알람 정보
    @JoinColumn(name = "user_id", nullable = false)
    private _1danhebojo.coalarm.coalarm_service.domain.user.repository.entity.UserEntity user;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime registeredDate;
}
