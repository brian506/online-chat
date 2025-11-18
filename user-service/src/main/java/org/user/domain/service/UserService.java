package org.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.common.exception.custom.ConflictException;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.common.utils.SuccessMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user.domain.dto.request.AuthRegisterRequest;
import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.dto.request.UserPreferenceRequest;
import org.user.domain.dto.response.AuthRegisterResponse;
import org.user.domain.dto.response.SignUpUserResponse;
import org.user.domain.dto.response.UserPreferenceResponse;
import org.user.domain.dto.response.UserResponse;
import org.user.domain.entity.Follow;
import org.user.domain.entity.User;
import org.user.domain.repository.FollowRepository;
import org.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthServiceClient authService;
    private final FollowRepository followRepository;

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

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserResponse getMyInfo(final String userId){
        String loginUserId = SecurityUtil.getCurrentUserId();
        User user = OptionalUtil.getOrElseThrow(userRepository.findById(userId),"존재하지 않는 사용자입니다.");

        long followerCount  = followRepository.countByFollower_Id(userId); // 팔로워수
        long followingCount = followRepository.countByFollowing_Id(userId); // 팔로잉수
        boolean followingByMe = followRepository
                .existsByFollower_IdAndFollowing_Id(loginUserId, userId); // 내가 팔로잉 하는 사람인지

        return UserResponse.userResponseToDto(user,followerCount,followingCount,followingByMe);
    }

    // 정보 조회 후 받은 userId 로 팔로잉 추가
    @Transactional
    public void followUser(final String userId){
        String loginUserId = SecurityUtil.getCurrentUserId();
        User loginUser = OptionalUtil.getOrElseThrow(userRepository.findById(loginUserId),SuccessMessages.USER_RETRIEVE_SUCCESS);
        User targetUser = OptionalUtil.getOrElseThrow(userRepository.findById(userId),SuccessMessages.USER_RETRIEVE_SUCCESS);

        if(!followRepository.existsByFollower_IdAndFollowing_Id(loginUser.getId(), targetUser.getId())){
            throw new ConflictException("이미 팔로우한 사용자입니다.");
        }
        // todo 팔로잉 했을 때 팔로잉 당한 사람에게 알람
        Follow follow = Follow.of(loginUser,targetUser);
        followRepository.save(follow);
    }

    // 팔로잉 취소
    @Transactional
    public void unFollowUser(final String userId){
        String loginUserId = SecurityUtil.getCurrentUserId();
        followRepository.deleteByFollower_IdAndFollowing_Id(loginUserId,userId);
    }

    // 닉네임 중복 확인
    public boolean validateNickname(final String nickname){
        return !userRepository.existsByNickname(nickname); // 사용중인 닉네임이면 false
    }

    // 사용자 삭제 (탈퇴)
    @Transactional
    public void deleteUser(final String userId){
        User user = OptionalUtil.getOrElseThrow(userRepository.findById(userId),"존재하지 않은 사용자입니다.");
        userRepository.delete(user);
    }
}
