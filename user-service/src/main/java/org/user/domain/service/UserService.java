package org.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.common.exception.custom.ConflictException;
import org.common.utils.OptionalUtil;
import org.springframework.stereotype.Service;
import org.user.domain.dto.request.AuthRegisterRequest;
import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.dto.response.AuthRegisterResponse;
import org.user.domain.dto.response.SignUpUserResponse;
import org.user.domain.dto.response.UserResponse;
import org.user.domain.entity.User;
import org.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthServiceClient authService;

    // 회원가입
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

    // 내 정보 조회
    public UserResponse getMyInfo(final String userId){
        User user = OptionalUtil.getOrElseThrow(userRepository.findById(userId),"존재하지 않는 사용자입니다.");
        return User.userResponseToDto(user);
    }

    // 닉네임 중복 확인
    public boolean validateNickname(final String nickname){
        return !userRepository.existsByNickname(nickname); // 사용중인 닉네임이면 false
    }

    // 사용자 삭제 (탈퇴)
    public void deleteUser(final String userId){
        User user = OptionalUtil.getOrElseThrow(userRepository.findById(userId),"존재하지 않은 사용자입니다.");
        userRepository.delete(user);
    }
}
