package _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository;

import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.CoinEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.KimchiPremiumEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.QKimchiPremiumEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class KimchiPremiumRepositoryImpl implements KimchiPremiumRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<KimchiPremiumEntity> findAllKimchiPremiums(int offset, int limit) {
        QKimchiPremiumEntity kp = QKimchiPremiumEntity.kimchiPremiumEntity;

        // 코인별 최신 프리미엄 ID를 먼저 조회
        List<Long> latestPremiumIds = queryFactory
                .select(kp.id.max())
                .from(kp)
                .groupBy(kp.coin.id)
                .fetch();

        // 해당 ID로 실제 데이터 조회
        return queryFactory
                .selectFrom(kp)
                .join(kp.coin).fetchJoin()
                .where(kp.id.in(latestPremiumIds))
                .orderBy(kp.coin.id.asc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public Map<String, BigDecimal> findLatestPremiumBySymbolsAndRegDtBetween(
            List<String> symbols,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    ) {
        QKimchiPremiumEntity kp = QKimchiPremiumEntity.kimchiPremiumEntity;
        QKimchiPremiumEntity kpSub = new QKimchiPremiumEntity("kpSub");

        Instant fromInstant = fromDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Instant toInstant = toDateTime.atZone(ZoneId.systemDefault()).toInstant();

        List<Tuple> results = queryFactory
                .select(kp.coin.symbol, kp.kimchiPremium)
                .from(kp)
                .where(
                        kp.coin.symbol.in(symbols),
                        kp.regDt.goe(fromInstant),
                        kp.regDt.lt(toInstant),
                        kp.regDt.eq(
                                JPAExpressions
                                        .select(kpSub.regDt.max())
                                        .from(kpSub)
                                        .where(
                                                kpSub.coin.symbol.eq(kp.coin.symbol),
                                                kpSub.regDt.goe(fromInstant),
                                                kpSub.regDt.lt(toInstant)
                                        )
                        )
                )
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(kp.coin.symbol),
                        tuple -> tuple.get(kp.kimchiPremium)
                ));
    }


    @Override
    public long countAllKimchiPremiums() {
        QKimchiPremiumEntity kp = QKimchiPremiumEntity.kimchiPremiumEntity;

        // 중복 없이 코인 ID의 종류 수 카운트
        Long count = queryFactory
                .select(kp.coin.id.countDistinct())
                .from(kp)
                .fetchOne();
        // (null 처리 추가)
        return count != null ? count : 0L;
    }
}
