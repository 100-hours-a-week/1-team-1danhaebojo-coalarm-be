package _1danhebojo.coalarm.coalarm_service.domain.discord.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class DiscordService {
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendDiscordAlert(String webhookUrl, String message) {
        // 요청 JSON 생성
        Map<String, String> request = Map.of(
                "username", "코알람",
                "content", message
        );

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("디스코드 알림 전송 실패: " + response.getStatusCode());
        }
    }
}
