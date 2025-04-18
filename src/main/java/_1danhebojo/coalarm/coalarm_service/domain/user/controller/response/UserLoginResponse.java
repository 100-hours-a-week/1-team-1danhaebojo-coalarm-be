package _1danhebojo.coalarm.coalarm_service.domain.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponse {
    private final Long userId;
    private final String accessToken;
    private final String refreshToken;

    public static UserLoginResponse of(Long userId, String accessToken, String refreshToken) {
        return UserLoginResponse.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
