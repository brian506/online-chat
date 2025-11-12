package org.common.utils;

public final class ErrorMessages {

    // 인증 / 인가 관련
    public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
    public static final String EXPIRED_TOKEN = "토큰이 만료되었습니다.";
    public static final String UNAUTHORIZED = "인증되지 않은 사용자입니다.";
    public static final String ACCESS_DENIED = "접근 권한이 없습니다.";

    // 사용자 관련
    public static final String USER_NOT_FOUND = "존재하지 않는 사용자입니다.";
    public static final String DUPLICATE_EMAIL = "이미 가입된 이메일입니다.";
    public static final String DUPLICATE_NICKNAME = "이미 사용 중인 닉네임입니다.";
    public static final String INVALID_CREDENTIALS = "아이디 또는 비밀번호가 올바르지 않습니다.";

    // 게시글 / 댓글 관련
    public static final String POST_NOT_FOUND = "존재하지 않는 게시글입니다.";
    public static final String COMMENT_NOT_FOUND = "존재하지 않는 댓글입니다.";
    public static final String INVALID_POST_OWNER = "게시글 작성자만 수정 또는 삭제할 수 있습니다.";

    // 서버 / 요청 관련
    public static final String INVALID_REQUEST = "잘못된 요청입니다.";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다.";
    public static final String DATABASE_ERROR = "데이터베이스 처리 중 오류가 발생했습니다.";


    // 생성자 private → 인스턴스화 방지
    private ErrorMessages() {}
}
