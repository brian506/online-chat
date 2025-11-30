//package org.user.unit_test;
//
//import org.common.exception.custom.ConflictException;
//import org.common.utils.SecurityUtil;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//import org.user.domain.dto.event.FollowEvent;
//import org.user.domain.dto.event.UserWhiskyFavoritesEvent;
//import org.user.domain.dto.request.AuthRegisterRequest;
//import org.user.domain.dto.request.CreateUserRequest;
//import org.user.domain.dto.request.UserPreferenceRequest;
//import org.user.domain.dto.request.WhiskyFavoritesRequest;
//import org.user.domain.dto.response.AuthRegisterResponse;
//import org.user.domain.dto.response.UserPreferenceResponse;
//import org.user.domain.dto.response.WhiskyFavoritesResponse;
//import org.user.domain.entity.*;
//import org.user.domain.repository.FollowRepository;
//import org.user.domain.repository.UserRepository;
//import org.user.domain.repository.WhiskyFavoritesRepository;
//import org.user.domain.service.UserService;
//import org.user.domain.service.client.AuthServiceClient;
//import org.user.domain.service.client.WhiskyServiceClient;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//
//    @InjectMocks
//    private UserService userService; // 테스트 대상
//
//    @Mock
//    private ApplicationEventPublisher publisher;
//
//    @Mock
//    private AuthServiceClient authServiceClient; // 외부 서비스 호출 Mock 처리
//
//    @Mock
//    private WhiskyServiceClient whiskyServiceClient;
//
//    @Mock
//    private UserRepository userRepository; // DB mock 처리 - 단위테스트에서는 DB 사용 X
//
//    @Mock
//    private FollowRepository followRepository;
//
//    @Mock
//    private WhiskyFavoritesRepository whiskyRepository;
//
//    @Test
//    @DisplayName("회원가입 성공 테스트")
//    void createUserInfo_SUCCESS() throws Exception {
//        //given
//        CreateUserRequest userRequest = new CreateUserRequest("test@email.com", "password", "영민짱", LocalDate.of(2000, 05, 06), Gender.MALE);
//        given(authServiceClient.registerUser(any(AuthRegisterRequest.class))) // 외부 호출 mock 처리
//                .willReturn(new AuthRegisterResponse("1234", "test@email.com", "영민짱"));
//
//        given(userRepository.existsByEmail("test@email.com")).willReturn(false); // false 일 때 통과
//        given(userRepository.existsByNickname("영민짱")).willReturn(false);
//
//        //when
//        userService.createUserInfo(userRequest);
//        //then
//        verify(userRepository).save(argThat(user ->
//                user.getNickname().equals("영민짱") &&
//                        user.getGender() == Gender.MALE &&
//                        user.getId().equals("1234")
//        ));
//    }
//
//    @Test
//    @DisplayName("회원가입 닉네임 중복 실패 테스트")
//    void createUserInfo_FAIL() throws Exception {
//        //given
//        CreateUserRequest userRequest = new CreateUserRequest("test@email.com", "password", "영민짱", LocalDate.of(2000, 05, 06), Gender.MALE);
//
//
//        given(userRepository.existsByNickname("영민짱")).willReturn(true); // 이미 닉네임이 있을 때로 가정
//        //when
//        assertThatThrownBy(() -> userService.createUserInfo(userRequest))
//                .isInstanceOf(RuntimeException.class);
//
//        //then
//        verify(userRepository, never()).save(any(User.class));
//
//    }
//
//    @Test
//    @DisplayName("설문조사 성공 테스트")
//    void createPreference_SUCCESS() throws Exception {
//        //given
//        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) { // 로그인 사용자 Mock 으로 강제 주입
//            String loginUserId = "user1";
//            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(loginUserId);
//            User user = User.builder()
//                    .id(loginUserId)
//                    .level(null)
//                    .taste(null)
//                    .build();
//
//            UserPreferenceRequest preferenceRequest = new UserPreferenceRequest(Level.BEGINNER, Taste.CEREAL);
//            given(userRepository.findById(loginUserId)).willReturn(Optional.of(user)); // given 은 mock 객체일때만
//
//            //when
//            UserPreferenceResponse response = userService.createPreference(preferenceRequest);
//
//            //then
//            // 응답값이 실제로 왔는지
//            assertThat(response.level()).isEqualTo(Level.BEGINNER);
//            assertThat(response.taste()).isEqualTo(Taste.CEREAL);
//
//            // 객체에 실제로 반영됐는지
//            assertThat(user.getLevel()).isEqualTo(Level.BEGINNER);
//            assertThat(user.getTaste()).isEqualTo(Taste.CEREAL);
//        }
//    }
//
//    @Test
//    @DisplayName("팔로잉 성공 테스트")
//    void followUser_SUCCESS() throws Exception {
//
//        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) { // 로그인 사용자 Mock 으로 강제 주입
//            //given
//            String loginUserId = "user1";
//            String targetId = "user2";
//            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(loginUserId);
//
//            User user1 = User.builder().id(loginUserId).build();
//            User user2 = User.builder().id(targetId).build();
//
//            given(userRepository.findById(loginUserId)).willReturn(Optional.of(user1));
//            given(userRepository.findById(targetId)).willReturn(Optional.of(user2));
//
//            given(followRepository.existsByFollower_IdAndFollowing_Id(loginUserId, targetId)).willReturn(false);
//
//            //when
//            userService.followUser(targetId);
//
//            //then
//            // 1. 팔로잉,팔로워 증가 카운트 검증
//            assertThat(user1.getFollowingCount()).isEqualTo(1);
//            assertThat(user2.getFollowerCount()).isEqualTo(1);
//
//            // 2. 인자로 넘어온 follow 객체의 Id 값이 옳바른지 검증
//            verify(followRepository).save(argThat(f ->
//                    f.getFollower().getId().equals(loginUserId) &&
//                            f.getFollowing().getId().equals(targetId)
//            ));
//
//            // 3. 발행된 이벤트 내용이 올바른지 검증
//            verify(publisher).publishEvent(argThat((Object event) -> {
//                if (event instanceof FollowEvent followEvent) {
//                    return followEvent.followerId().equals(loginUserId) &&
//                            followEvent.followingId().equals(targetId) &&
//                            followEvent.actionType() == ActionType.ADD;
//                }
//                return false;
//            }));
//        }
//    }
//
//    @Test
//    @DisplayName("이미 팔로잉한 사용자로 팔로잉 실패 테스트")
//    void followUser_FAIL() throws Exception {
//
//        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) { // 로그인 사용자 Mock 으로 강제 주입
//            //given
//            String loginUserId = "user1";
//            String targetId = "user2";
//            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(loginUserId);
//
//            User user1 = User.builder().id(loginUserId).build();
//            User user2 = User.builder().id(targetId).build();
//
//            given(userRepository.findById(targetId)).willReturn(Optional.of(user2));
//            given(userRepository.findById(loginUserId)).willReturn(Optional.of(user1));
//
//            given(followRepository.existsByFollower_IdAndFollowing_Id(loginUserId, targetId)).willReturn(true); // 이미 팔로잉한 사용자로 설정
//            //when
//            assertThatThrownBy(() -> userService.followUser(targetId))
//                    .isInstanceOf(ConflictException.class)
//                    .extracting("errorMessage")
//                    .isEqualTo("이미 팔로우한 사용자입니다."); // when + then
//            //then
//            verify(followRepository, never()).save(any());
//            verify(publisher, never()).publishEvent(any());
//        }
//
//    }
//
//    @Test
//    @DisplayName("팔로잉 취소 성공 테스트")
//    void unfollowUser_SUCCESS() throws Exception {
//
//        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) { // 로그인 사용자 Mock 으로 강제 주입
//            //given
//            String loginUserId = "user1";
//            String targetId = "user2";
//            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(loginUserId);
//
//            User user1 = User.builder()
//                    .id(loginUserId)
//                    .followingCount(1)
//                    .build();
//            User user2 = User.builder()
//                    .id(targetId)
//                    .followerCount(1)
//                    .build();
//
//            given(userRepository.findById(targetId)).willReturn(Optional.of(user2));
//            given(userRepository.findById(loginUserId)).willReturn(Optional.of(user1));
//
//            //when
//            userService.unFollowUser(targetId);
//            //then
//            assertThat(user1.getFollowingCount()).isEqualTo(0);
//            assertThat(user2.getFollowerCount()).isEqualTo(0);
//
//            // 리포지토리 삭제 메서드 호출 검증
//            verify(followRepository).deleteByFollower_IdAndFollowing_Id(loginUserId, targetId);
//
//            // 발행된 이벤트 내용이 올바른지 검증
//            verify(publisher).publishEvent(argThat((Object event) -> {
//                if (event instanceof FollowEvent followEvent) {
//                    return followEvent.followerId().equals(loginUserId) &&
//                            followEvent.followingId().equals(targetId) &&
//                            followEvent.actionType() == ActionType.REMOVE;
//                }
//                return false;
//            }));
//        }
//    }
//
//    @Test
//    @DisplayName("위스키 즐겨찾기 추가 성공 테스트")
//    void addWhisky_SUCCESS() throws Exception {
//
//        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
//            //given
//            // 사용자 mock
//            String loginUserId = "user1";
//            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(loginUserId);
//            User user = User.builder().id(loginUserId).build();
//            given(userRepository.findById(loginUserId)).willReturn(Optional.of(user));
//
//            String whiskyId = "whiskyId";
//            given(whiskyServiceClient.getUserFavorites(whiskyId)) // 외부 호출 mock 처리
//                    .willReturn(new WhiskyFavoritesResponse("whisky", "whiskyId", "talisker", "image"));
//
//            //when
//            userService.addWhiskyFavorites(whiskyId);
//            //then
//
//            // 카운트 검증
//            assertThat(user.getWhiskyCount()).isEqualTo(1);
//            // 저장됐느지 검증
//            verify(whiskyRepository).save(argThat(w ->
//                    w.getWhiskyId().equals(whiskyId) &&
//                            w.getUserId().equals(loginUserId)
//            ));
//            // 이벤트 발행 검증
//            verify(publisher).publishEvent(argThat((Object event) -> {
//                if (event instanceof UserWhiskyFavoritesEvent whiskyEvent) {
//                    return whiskyEvent.whiskyId().equals(whiskyId) &&
//                            whiskyEvent.userId().equals(loginUserId) &&
//                            whiskyEvent.actionType() == ActionType.ADD;
//                }
//                return false;
//            }));
//        }
//    }
//
//    @Test
//    @DisplayName("위스키 즐겨찾기 삭제 성공 테스트")
//    void deleteWhiskyFavorites_SUCCESS() throws Exception {
//
//        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
//            //given
//            // 사용자 mock
//            String loginUserId = "user1";
//            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(loginUserId);
//            User user = User.builder().id(loginUserId).whiskyCount(1).build();
//            given(userRepository.findById(loginUserId)).willReturn(Optional.of(user));
//
//            String whiskyId = "whiskyId";
//            WhiskyFavorites whiskyFavorites = WhiskyFavorites.builder().whiskyId(whiskyId).userId(loginUserId).build();
//            given(whiskyRepository.findByUserIdAndWhiskyId(loginUserId, whiskyId)).willReturn(Optional.of(whiskyFavorites));
//            //when
//            userService.deleteWhiskyFavorites(whiskyId);
//            //then
//            verify(whiskyRepository).delete(whiskyFavorites);
//            assertThat(user.getWhiskyCount()).isEqualTo(0);
//
//            // 이벤트 발행 검증
//            verify(publisher).publishEvent(argThat((Object event) -> {
//                if (event instanceof UserWhiskyFavoritesEvent whiskyEvent) {
//                    return whiskyEvent.userId().equals(loginUserId) &&
//                            whiskyEvent.whiskyId().equals(whiskyId) &&
//                            whiskyEvent.actionType() == ActionType.REMOVE;
//                }
//                return false;
//            }));
//        }
//    }
//}