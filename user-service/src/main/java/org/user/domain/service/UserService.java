package org.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.common.exception.custom.ConflictException;
import org.common.exception.custom.DataNotFoundException;
import org.common.utils.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user.domain.dto.event.FollowEvent;
import org.user.domain.dto.event.UserWhiskyFavoritesEvent;
import org.user.domain.dto.request.AuthRegisterRequest;
import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.dto.request.UserPreferenceRequest;
import org.user.domain.dto.request.WhiskyFavoritesRequest;
import org.user.domain.dto.response.*;
import org.user.domain.entity.ActionType;
import org.user.domain.entity.Follow;
import org.user.domain.entity.User;
import org.user.domain.entity.WhiskyFavorites;
import org.user.domain.repository.FollowRepository;
import org.user.domain.repository.UserRepository;
import org.user.domain.repository.WhiskyFavoritesRepository;
import org.user.domain.service.client.AuthServiceClient;
import org.user.domain.service.client.WhiskyServiceClient;
import org.user.producer.KafkaEventListener;
import org.user.producer.KafkaProducer;

import javax.swing.text.html.Option;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthServiceClient authService;
    private final FollowRepository followRepository;
    private final WhiskyServiceClient whiskyService;
    private final ApplicationEventPublisher publisher;
    private final WhiskyFavoritesRepository whiskyFavoritesRepository;

    // 회원가입
    @Transactional
    public SignUpUserResponse createUserInfo(final CreateUserRequest userRequest){

        // Auth-service 로 email,password 가입 요청
        AuthRegisterRequest registerRequest = AuthRegisterRequest.toCreateUser(userRequest);
        AuthRegisterResponse registerResponse = authService.registerUser(registerRequest);

        if (!validateNickname(userRequest.nickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.signUpDtoToEntity(userRequest,registerResponse.userId());
        userRepository.save(user);

        return SignUpUserResponse.requestToResponse(userRequest, registerResponse.userId());
    }

    // 로그인 후 설문조사
    @Transactional
    public UserPreferenceResponse createPreference(final UserPreferenceRequest request){
        String userId = SecurityUtil.getCurrentUserId();
        User user = OptionalUtil.getOrElseThrow(userRepository.findById(userId), SuccessMessages.PREFERENCE_POST_SUCCESS);
        user.updatePreferences(request);
        return UserPreferenceResponse.from(user);
    }

    // 사용자 정보 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(final String userId){
        String loginUserId = SecurityUtil.getCurrentUserId();
        UserResponse response = userRepository.getUserInfo(userId,loginUserId);
        if(response == null){
            throw new DataNotFoundException(ErrorMessages.USER_NOT_FOUND);
        }
        return response;
    }

    // 정보 조회 후 받은 userId 로 팔로잉 추가
    @Transactional
    public void followUser(final String userId){
        String loginUserId = SecurityUtil.getCurrentUserId();
        User loginUser = OptionalUtil.getOrElseThrow(userRepository.findById(loginUserId),SuccessMessages.USER_RETRIEVE_SUCCESS);
        User targetUser = OptionalUtil.getOrElseThrow(userRepository.findById(userId),SuccessMessages.USER_RETRIEVE_SUCCESS);

        if(followRepository.existsByFollower_IdAndFollowing_Id(loginUser.getId(), targetUser.getId())){
            throw new ConflictException("이미 팔로우한 사용자입니다.");
        }
        // todo 팔로잉 했을 때 팔로잉 당한 사람에게 알람
        Follow follow = Follow.of(loginUser,targetUser);
        loginUser.incrementFollowingCount();
        targetUser.incrementFollowerCount();

        followRepository.save(follow);

        FollowEvent event = FollowEvent.toEvent(loginUserId, targetUser.getId(),ActionType.ADD);
        publisher.publishEvent(event);
    }

    // 팔로잉 취소
    @Transactional
    public void unFollowUser(final String userId){
        String loginUserId = SecurityUtil.getCurrentUserId();
        User loginUser = OptionalUtil.getOrElseThrow(userRepository.findById(loginUserId),SuccessMessages.USER_RETRIEVE_SUCCESS);
        User targetUser = OptionalUtil.getOrElseThrow(userRepository.findById(userId),SuccessMessages.USER_RETRIEVE_SUCCESS);

        loginUser.decrementFollowingCount();
        targetUser.decrementFollowerCount();

        followRepository.deleteByFollower_IdAndFollowing_Id(loginUserId,userId);

        FollowEvent event = FollowEvent.toEvent(loginUserId,userId,ActionType.REMOVE);
        publisher.publishEvent(event);
    }

    // 위스키 즐겨찾기 추가
    @Transactional
    public String addWhiskyFavorites(final WhiskyFavoritesRequest request){
        String userId = SecurityUtil.getCurrentUserId();
        // whisky-service 에서 가져옴
        WhiskyFavoritesResponse whiskyFavoritesResponse = whiskyService.getUserFavorites(request.whiskyId());
        // 엔티티 저장
        WhiskyFavorites whiskyFavorites = WhiskyFavorites.toEntity(whiskyFavoritesResponse,userId);
        whiskyFavorites.increaseCount();
        whiskyFavoritesRepository.save(whiskyFavorites);
        // 이벤트 발행(트랜잭션 COMMIT 후 이벤트 발행)
        UserWhiskyFavoritesEvent event = UserWhiskyFavoritesEvent.fromResponse(whiskyFavoritesResponse,userId, ActionType.ADD);
        publisher.publishEvent(event);
        return whiskyFavorites.getWhiskyId();
    }

    // 위스키 즐겨찾기 삭제
    @Transactional
    public void deleteWhiskyFavorites(final WhiskyFavoritesRequest request){
        String userId = SecurityUtil.getCurrentUserId();
        WhiskyFavorites whiskyFavorites = OptionalUtil.getOrElseThrow(whiskyFavoritesRepository.findByUserIdAndWhiskyId(userId,request.whiskyId()),SuccessMessages.WHISKY_FAVORITES_RETRIEVE_SUCCESS);
        whiskyFavorites.decreaseCount();
        whiskyFavoritesRepository.delete(whiskyFavorites);

        UserWhiskyFavoritesEvent event = UserWhiskyFavoritesEvent.fromRequest(request,userId,ActionType.REMOVE);
        publisher.publishEvent(event);
    }

    // 사용자 삭제 (탈퇴)
    @Transactional
    public void deleteUser(final String userId){
        User user = OptionalUtil.getOrElseThrow(userRepository.findById(userId),"존재하지 않은 사용자입니다.");
        userRepository.delete(user);
    }


    // 닉네임 중복 확인
    public boolean validateNickname(final String nickname){
        return !userRepository.existsByNickname(nickname); // 사용중인 닉네임이면 false
    }


}
