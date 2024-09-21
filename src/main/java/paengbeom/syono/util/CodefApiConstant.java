package paengbeom.syono.util;

public class CodefApiConstant {
    public static final String API_DOMAIN = "https://api.codef.io/v1";                                        // API서버 도메인
    public static final String TEST_DOMAIN = "https://development.codef.io/v1";                                // API서버 데모 도메인
    public static final String SANDBOX_DOMAIN = "https://sandbox.codef.io/v1";

    public static final String TOKEN_DOMAIN = "https://oauth.codef.io";                                        // OAUTH2.0 테스트 도메인
    public static final String GET_TOKEN = "/oauth/token";                                                // OAUTH2.0 토큰 발급 요청 URL

    public static final String KR_CD_P_001 = "/kr/card/p/account/card-list";                            // 카드 개인 보유카드
    public static final String KR_CD_P_002 = "/kr/card/p/account/approval-list";                        // 카드 개인 승인내역
    public static final String KR_CD_P_003 = "/kr/card/p/account/billing-list";                         // 카드 개인 청구내역
    public static final String KR_CD_P_004 = "/kr/card/p/account/limit";                                // 카드 개인 한도조회
    public static final String KR_CD_P_005 = "/kr/card/p/account/result-check-list";                    // 카드 개인 실적조회
    public static final String KR_CD_P_006 = "/kr/card/p/user/registration-status";                    // 카드 개인 등록 여부 조회

    public static final String GET_CONNECTED_IDS = "/account/connectedId-list";                        // 커넥티드아이디 목록 조회
    public static final String GET_ACCOUNTS = "/account/list";                                            // 계정 목록 조회
    public static final String CREATE_ACCOUNT = "/account/create";                                        // 계정 등록(커넥티드아이디 발급)
    public static final String ADD_ACCOUNT = "/account/add";                                                // 계정 추가
    public static final String UPDATE_ACCOUNT = "/account/update";                                        // 계정 수정
    public static final String DELETE_ACCOUNT = "/account/delete";
}
