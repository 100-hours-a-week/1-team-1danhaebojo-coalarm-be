package _1danhebojo.coalarm.coalarm_service.domain.discord.controller;
import _1danhebojo.coalarm.coalarm_service.domain.discord.service.DiscordService;
import _1danhebojo.coalarm.coalarm_service.global.api.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/users/discord")
@RequiredArgsConstructor
public class DiscordController {

    private final DiscordService discordService;

    @PostMapping("/test")
    public ResponseEntity<BaseResponse<?>> testDiscordWebhook(
            @RequestBody Map<String, String> requestBody) {

        String webHookUrl = requestBody.get("web_hook_url");
        String message = requestBody.get("message");

        if (webHookUrl == null || webHookUrl.isBlank()) {
            return ResponseEntity
                    .status(404)
                    .body(BaseResponse.error());
        }

        if (message == null || message.isBlank()) {
            return ResponseEntity
                    .status(404)
                    .body(BaseResponse.error());
        }

        discordService.sendDiscordAlert(webHookUrl, message);

        return ResponseEntity.ok(BaseResponse.success("디스코드 웹훅 테스트 메시지가 성공적으로 전송되었습니다."));
    }
}
