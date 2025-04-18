package _1danhebojo.coalarm.coalarm_service.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AppHttpStatus {
    /**
     * 20X : 성공
     */
    OK(HttpStatus.OK, "요청이 정상적으로 수행되었습니다."),
    CREATED(HttpStatus.CREATED, "리소스를 생성하였습니다."),

    /**
     * 400 : 잘못된 문법으로 인해 요청을 이해할 수 없음
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),
    INVALID_OFFSET(HttpStatus.BAD_REQUEST, "오프셋은 0 이상이어야 합니다."),
    INVALID_LIMIT(HttpStatus.BAD_REQUEST, "한 페이지당 항목 수는 1 이상이어야 합니다."),
    INVALID_COIN_ID(HttpStatus.BAD_REQUEST, "코인 ID는 1 이상이어야 합니다."),
    INVALID_OAUTH_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 OAuth 타입입니다."),
    EMPTY_SEARCH_TERM(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
    EMPTY_DISCORD_WEBHOOK(HttpStatus.BAD_REQUEST, "웹훅 URL이 비어있습니다."),
    INVALID_DISCORD_WEBHOOK(HttpStatus.BAD_REQUEST, "유효하지 않은 디스코드 웹훅 URL입니다."),
    INVALID_NICKNAME_LENGTH(HttpStatus.BAD_REQUEST, "닉네임은 2자 이상 10자 이하여야 합니다."),


    /**
     * 401 : 인증된 사용자가 아님
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Access Token입니다."),
    INVALID_TOKEN( HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN( HttpStatus.UNAUTHORIZED, "손상된 토큰입니다."),
    EXPIRED_TOKEN( HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN( HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    BLOCKED_TOKEN( HttpStatus.UNAUTHORIZED, "로그아웃 처리된 토큰입니다."),
    OAUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "OAuth 로그인에 실패 했습니다."),

    /**
     * 403 : 접근 권한이 없음
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    /**
     * 404 : 응답할 리소스가 없음
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),
    NOT_FOUND_ENDPOINT(HttpStatus.NOT_FOUND, "존재하지 않는 엔드포인트입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"존재하지 않는 회원입니다."),
    NOT_FOUND_COIN(HttpStatus.NOT_FOUND, "존재하지 않는 코인입니다."),
    NOT_FOUND_ALERT(HttpStatus.NOT_FOUND, "존재하지 않는 알람입니다.."),
    NOT_FOUND_ALERT_HISTORY(HttpStatus.INTERNAL_SERVER_ERROR, "존재하지 않는 알람 히스토리입니다."),

    /**
     * 415 : 미디어 타입 에러
     */
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "허용되지 않은 파일 형식입니다."),

    /**
     * 409 : 현재 상태와 충돌되는 요청
     */
    ALREADY_EXISTS_ALERT(HttpStatus.CONFLICT, "등록할 수 있는 알람의 갯수를 초과했습니다."),

    /**
     * 500 : 서버 내부에서 에러가 발생함
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에 에러가 발생했습니다."),
    IMAGE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"S3에 이미지 업로드중 에러가 발생했습니다."),
    IMAGE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3의 이미지 삭제중 에러가 발생했습니다."),
    FAILED_AUTHENTICATION_OAUTH(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth 인증에 실패했습니다."),
    FAILED_TO_SAVE_ALERT(HttpStatus.INTERNAL_SERVER_ERROR, "알림 저장에 실패했습니다."),
    FAILED_TO_SEND_DISCORD(HttpStatus.INTERNAL_SERVER_ERROR, "디스코드 알림 전송에 실패했습니다."),

    /**
     * 502: 게이트웨이, 프록시 역할을 하는 서버가 다른 서버로부터 유효하지 않은 응답을 받았을 때
     */
    KAKAO_API_ERROR(HttpStatus.BAD_GATEWAY, "카카오 API 호출 중 오류가 발생했습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}