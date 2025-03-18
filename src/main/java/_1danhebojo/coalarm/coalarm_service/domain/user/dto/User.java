package _1danhebojo.coalarm.coalarm_service.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId; // 유저 ID (Primary Key)

    private String kakaoId; // 카카오에서 제공하는 고유 ID

    private String nickname; // 닉네임 (초기 연동 시 설정 후 수정 가능)

    private String email; // 카카오에서 제공하는 이메일

    private String profileImg; // 프로필 이미지 URL

    private String discordWebhook; // 디스코드 웹훅 URL (연동되면 값 저장, 미연동 시 NULL)

    private LocalDateTime regDt; // 등록일

    private LocalDateTime chgDt; // 수정일

}
