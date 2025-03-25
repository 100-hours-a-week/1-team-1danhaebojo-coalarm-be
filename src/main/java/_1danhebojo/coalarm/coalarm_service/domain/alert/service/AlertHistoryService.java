package _1danhebojo.coalarm.coalarm_service.domain.alert.service;

import _1danhebojo.coalarm.coalarm_service.domain.alert.controller.request.PaginationRequest;
import _1danhebojo.coalarm.coalarm_service.domain.alert.controller.response.AlertListResponse;
import _1danhebojo.coalarm.coalarm_service.domain.alert.controller.response.AlertResponse;
import _1danhebojo.coalarm.coalarm_service.domain.alert.controller.response.alertHistory.AlertHistoryResponse;
import _1danhebojo.coalarm.coalarm_service.domain.alert.controller.response.alertHistory.AlertHistoryListResponse;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.AlertHistoryRepositoryImpl;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.AlertRepositoryImpl;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity.Alert;
import _1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity.AlertHistory;
import _1danhebojo.coalarm.coalarm_service.domain.user.repository.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertHistoryService {

    private final AlertHistoryRepositoryImpl alertHistoryRepository;

    // 알람 리스트 조회
    @Transactional(readOnly = true)
    public AlertHistoryListResponse getAlertHistoryList(Long userId, PaginationRequest paginationRequest) {
        int offset = paginationRequest.getOffset();
        int limit = paginationRequest.getLimit();
        Pageable pageable = PageRequest.of(offset, limit);
        Page<AlertHistory> historyPage = alertHistoryRepository.findAlertHistoryByFilter(userId, pageable);

        List<AlertHistoryListResponse.AlertHistoryContent> contents = historyPage.getContent().stream()
                .map(AlertHistoryListResponse.AlertHistoryContent::new)
                .collect(Collectors.toList());

        return new AlertHistoryListResponse(
                contents,
                offset,
                limit,
                historyPage.getTotalElements(),
                historyPage.hasNext()
        );
    }

    // 알람 정보 조회
    public AlertHistoryResponse getAlertHistory(Long alertHistoryId) {
        AlertHistory alertHistory = alertHistoryRepository.findById(alertHistoryId)
                .orElseThrow(() -> new RuntimeException("해당 알람 히스토리를 찾을 수 없습니다."));

        return new AlertHistoryResponse(alertHistory);
    }

    // 알람 히스토리 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addAlertHistory(Long alertId, Long userId) {
        AlertHistory alertHistory = new AlertHistory();

        Alert alert = new Alert();
        alert.setAlertId(alertId);

        UserEntity user = UserEntity.builder()
                .userId(userId)
                .build();

        alertHistory.setUser(user);
        alertHistory.setAlert(alert);
        alertHistory.setRegisteredDate(LocalDateTime.now());

        alertHistoryRepository.save(alertHistory);
    }
}
