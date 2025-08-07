package _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository;

import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.CoinEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.KimchiPremiumEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KimchiPremiumRepository {
    List<KimchiPremiumEntity> findAllKimchiPremiums(int offset, int limit);

    public Map<String, BigDecimal> findLatestPremiumBySymbolsAndRegDtBetween(
            List<String> symbols,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    );

    long countAllKimchiPremiums();
}
