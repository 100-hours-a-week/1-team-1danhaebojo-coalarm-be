package _1danhebojo.coalarm.coalarm_service.domain.alert.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpbitService {
    private static final String UPBIT_MARKET_URL = "https://api.upbit.com/v1/market/all?is_details=true";

    public static List<String> getTradingVolumeSoaringMarkets() {
        RestTemplate restTemplate = new RestTemplate();
        List<String> soaringMarkets = new ArrayList<>();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(UPBIT_MARKET_URL, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            for (JsonNode market : root) {
                JsonNode marketEvent = market.get("market_event");
                if (marketEvent != null && marketEvent.has("caution")) {
                    JsonNode caution = marketEvent.get("caution");
                    if (caution.has("TRADING_VOLUME_SOARING") && caution.get("TRADING_VOLUME_SOARING").asBoolean()) {
                        soaringMarkets.add(market.get("market").asText() + " - " + market.get("korean_name").asText());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soaringMarkets;
    }
}
