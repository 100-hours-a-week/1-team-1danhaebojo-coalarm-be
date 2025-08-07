package _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository;

import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.CoinEntity;
import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.QCoinEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.QTickerEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.TickerEntity;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TickerRepositoryImpl implements TickerRepository{

    private final JPAQueryFactory query;

    @Override
    public List<TickerEntity> findByCoinIdOrderedByUtcDateTime(String symbol) {
        QTickerEntity ticker = QTickerEntity.tickerEntity;

        // 명확한 형태로 검색
        return query
                .selectFrom(ticker)
                .where(ticker.id.baseSymbol.eq(symbol))
                .orderBy(ticker.id.timestamp.asc())
                .limit(100)
                .fetch();
    }

    @Override
    public Optional<TickerEntity> findLatestBySymbol(String symbol) {
        QTickerEntity ticker = QTickerEntity.tickerEntity;

        return Optional.ofNullable(
                query
                        .selectFrom(ticker)
                        .where(ticker.id.baseSymbol.eq(symbol))
                        .orderBy(ticker.id.timestamp.desc()) // 최신 순으로 정렬
                        .fetchFirst()
        );
    }

    @Override
    public List<TickerEntity> findLatestTickers(List<String> coinSymbols, String quoteSymbol){
        QTickerEntity ticker = QTickerEntity.tickerEntity;
        QTickerEntity subTicker = new QTickerEntity("subTicker");

        return query
                .selectFrom(ticker)
                .where(
                        ticker.id.baseSymbol.in(coinSymbols),
                        ticker.id.quoteSymbol.eq(quoteSymbol),
                        ticker.id.timestamp.eq(
                                JPAExpressions
                                        .select(subTicker.id.timestamp.max())
                                        .from(subTicker)
                                        .where(
                                                subTicker.id.baseSymbol.eq(ticker.id.baseSymbol),
                                                subTicker.id.quoteSymbol.eq(quoteSymbol)
                                        )
                        )
                )
                .fetch();
    }

}
