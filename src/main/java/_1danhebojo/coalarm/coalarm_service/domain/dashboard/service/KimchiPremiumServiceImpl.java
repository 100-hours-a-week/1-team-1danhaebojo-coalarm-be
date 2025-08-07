package _1danhebojo.coalarm.coalarm_service.domain.dashboard.service;

import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.CoinRepository;
import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.jpa.CoinJpaRepository;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.controller.response.ResponseKimchiPremium;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.KimchiPremiumRepository;
import _1danhebojo.coalarm.coalarm_service.domain.coin.repository.entity.CoinEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.TickerRepository;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.KimchiPremiumEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.entity.TickerEntity;
import _1danhebojo.coalarm.coalarm_service.domain.dashboard.repository.jpa.KimchiPremiumJpaRepository;
import _1danhebojo.coalarm.coalarm_service.global.api.ApiException;
import _1danhebojo.coalarm.coalarm_service.global.api.AppHttpStatus;
import _1danhebojo.coalarm.coalarm_service.global.api.OffsetResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KimchiPremiumServiceImpl implements KimchiPremiumService{
    private final KimchiPremiumRepository kimchiPremiumRepository;
    private final TickerRepository tickerRepository;
    private final CoinRepository coinRepository;
    private final KimchiPremiumJpaRepository kimchiPremiumJpaRepository;
    private final CoinJpaRepository coinJpaRepository;

    @Resource(name = "kimchiTaskExecutor")
    private final Executor kimchiTaskExecutor;
    private static final String EXCHANGE_RATE_API_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    // 계산 시 사용할 스케일 상수 정의
    private static final int CALCULATION_SCALE = 16;
    private static final int DISPLAY_SCALE = 8;

    @Override
    public OffsetResponse<ResponseKimchiPremium> getKimchiPremiums(int offset, int limit) {
        List<ResponseKimchiPremium> premiums = kimchiPremiumRepository.findAllKimchiPremiums(offset, limit)
                .stream()
                .map(ResponseKimchiPremium::fromEntity)
                .toList();

        long totalElements = kimchiPremiumRepository.countAllKimchiPremiums();

        return OffsetResponse.of(
                premiums,
                offset,
                limit,
                totalElements
        );
    }

    @Override
    public void calculateAndSaveKimchiPremium() {
        List<CoinEntity> coins = Optional.ofNullable(coinRepository.findAllWithoutUSDT())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ApiException(AppHttpStatus.NOT_FOUND_COIN));

        List<String> coinSymbols = coins.stream()
                .map(CoinEntity::getSymbol)
                .toList();

        // USD/KRW 환율 한 번만 가져오기
        BigDecimal exchangeRate = getUsdToKrwExchangeRate();
        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("환율 데이터를 가져올 수 없습니다.");
            throw new ApiException(AppHttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, Optional<TickerEntity>> krwTickers = getLatestTickers(coinSymbols, "KRW");
        Map<String, Optional<TickerEntity>> usdtTickers = getLatestTickers(coinSymbols, "USDT");

        if (krwTickers.isEmpty() || usdtTickers.isEmpty()) {
            log.warn("코인의 가격 데이터를 찾을 수 없습니다.");
            throw new ApiException(AppHttpStatus.NOT_FOUND);
        }

        // 어제 자정 시간 계산
        LocalDateTime yesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Map<String, BigDecimal> yesterdayPremiums = kimchiPremiumRepository
                .findLatestPremiumBySymbolsAndRegDtBetween(coinSymbols, yesterday, today);


        List<CompletableFuture<KimchiPremiumEntity>> futures = new ArrayList<>();
        for(CoinEntity coin: coins){
            CompletableFuture<KimchiPremiumEntity> future = CompletableFuture.supplyAsync(()->{
                return calculateKimchiPremiumForCoin(
                        coin, exchangeRate, krwTickers, usdtTickers,
                        yesterdayPremiums);
            },kimchiTaskExecutor);

            futures.add(future);
        }

        List<KimchiPremiumEntity> kimchiPremiums = futures.stream()
                .map(CompletableFuture::join) // 각 Future의 결과를 기다림
                .filter(Objects::nonNull) // null 값 제거 (계산 실패한 경우)
                .collect(Collectors.toList());

        // 계산된 김치프리미엄 일괄 저장
        if (!kimchiPremiums.isEmpty()) {
            try {
                kimchiPremiumJpaRepository.saveAll(kimchiPremiums);
                log.info("김치프리미엄 {}개 데이터 저장 완료", kimchiPremiums.size());
            } catch (Exception e) {
                log.error("김치프리미엄 데이터 저장 중 오류 발생", e);
                throw new ApiException(AppHttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("저장할 김치프리미엄 데이터가 없습니다.");
        }
    }

    //최신 코인 정보 조회
    private Map<String, Optional<TickerEntity>> getLatestTickers(List<String> coinSymbols,String quoteSymbol){
        List<TickerEntity> tickers = tickerRepository.findLatestTickers(coinSymbols, quoteSymbol);

        Map<String, TickerEntity> tickerMap = tickers.stream()
                .collect(Collectors.toMap(
                        ticker -> ticker.getId().getBaseSymbol(),
                        ticker -> ticker,
                        (existing, replacement) -> existing
                ));

        return coinSymbols.stream()
                .collect(Collectors.toMap(
                        symbol -> symbol,
                        symbol -> Optional.ofNullable(tickerMap.get(symbol))
                ));
    }

    private KimchiPremiumEntity calculateKimchiPremiumForCoin(
            CoinEntity coinEntity,
            BigDecimal exchangeRate,
            Map<String, Optional<TickerEntity>> krwTickers,
            Map<String, Optional<TickerEntity>> usdtTickers,
            Map<String, BigDecimal> yesterdayPremiums) {
        try {
            String coinSymbol = coinEntity.getSymbol();
            Optional<TickerEntity> krwTicker = krwTickers.get(coinSymbol);
            Optional<TickerEntity> usdtTicker = usdtTickers.get(coinSymbol);

            if (krwTicker == null || krwTicker.isEmpty() || usdtTicker == null || usdtTicker.isEmpty()) {
                log.warn("{}의 필요한 데이터를 찾을 수 없습니다.", coinSymbol);
                return null;
            }

            BigDecimal krwPrice = krwTicker.get().getClose();
            BigDecimal usdtPrice = usdtTicker.get().getClose();

            BigDecimal globalPriceInKrw = usdtPrice.multiply(exchangeRate)
                    .setScale(CALCULATION_SCALE, RoundingMode.HALF_UP);

            BigDecimal priceDifference = krwPrice.subtract(globalPriceInKrw);
            BigDecimal kimchiPremium;

            if (globalPriceInKrw.compareTo(BigDecimal.ZERO) == 0) {
                // 분모가 0인 경우 방지
                kimchiPremium = BigDecimal.ZERO;
                log.warn("글로벌 가격이 0이라 김치프리미엄을 0으로 설정합니다.");
            } else {
                kimchiPremium = priceDifference
                        .divide(globalPriceInKrw, CALCULATION_SCALE, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);
            }

            // 일별 변동률 계산
            BigDecimal dailyChange = calculateDailyChange(kimchiPremium, yesterdayPremiums.get(coinSymbol));

            return KimchiPremiumEntity.builder()
                    .coin(coinEntity)
                    .domesticPrice(krwPrice)
                    .globalPrice(usdtPrice)
                    .exchangeRate(exchangeRate)
                    .kimchiPremium(kimchiPremium)
                    .dailyChange(dailyChange)
                    .build();

        } catch (Exception e) {
            log.error("{}의 김치프리미엄 계산 중 오류 발생", coinEntity.getSymbol(), e);
            return null;
        }
    }

    private BigDecimal calculateDailyChange(BigDecimal currentValue, BigDecimal yesterdayPremium) {
        if (yesterdayPremium == null) {
            return BigDecimal.ZERO; // 어제 데이터가 없으면 변동률 0
        }

        // 변동률 계산: (오늘값 - 어제값) / |어제값| * 100
        // 분모가 0인 경우를 방지하기 위한 처리
        if (yesterdayPremium.compareTo(BigDecimal.ZERO) == 0) {
            // 어제 값이 0인 경우 (변동률 계산 불가)
            return currentValue.compareTo(BigDecimal.ZERO) > 0 ?
                    new BigDecimal("100") : new BigDecimal("-100");
        }

        // 정확한 계산을 위해 높은 스케일 사용
        return currentValue.subtract(yesterdayPremium)
                .divide(yesterdayPremium.abs(), CALCULATION_SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal getUsdToKrwExchangeRate() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(EXCHANGE_RATE_API_URL, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return BigDecimal.valueOf(jsonNode.get("rates").get("KRW").asDouble());
        } catch (Exception e) {
            log.error("환율 데이터를 가져오는 중 오류 발생: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}