package _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository;

import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.TickerEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TickerRepository {
    List<TickerEntity> findByCoinIdOrderedByUtcDateTime(String symbol);
    Optional<TickerEntity> findLatestBySymbol(String symbol);
    List<TickerEntity> findLatestTickers(List<String> coinSymbols, String quoteSymbol);
}
