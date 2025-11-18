package org.user.api;

import lombok.RequiredArgsConstructor;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.dto.request.UserPreferenceRequest;
import org.user.domain.dto.response.SignUpUserResponse;
import org.user.domain.dto.response.UserPreferenceResponse;
import org.user.domain.dto.response.UserResponse;
import org.user.domain.service.UserService;


@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> createUserInfo(@RequestBody final CreateUserRequest request){
        SignUpUserResponse userResponse = userService.createUserInfo(request);
        SuccessResponse response = new SuccessResponse(true,"사용자 정보 생성 성공",userResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 사용자 별명 중복 확인
    @GetMapping("/check-nickname/{nickname}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> duplicateNickname(@PathVariable String nickname){
        boolean isDuplicated = userService.validateNickname(nickname);
        SuccessResponse response = new SuccessResponse(true,"닉네임 중복 확인 성공",isDuplicated);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 선호도 조사
    //todo 응답 dto 로 굳이?
    @PatchMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPreference(@RequestBody final UserPreferenceRequest request){
        UserPreferenceResponse preferenceResponse = userService.createPreference(request);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.PREFERENCE_POST_SUCCESS,preferenceResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 내 정보 조회
    @GetMapping("/my-page/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyInfo(@PathVariable String userId){
        UserResponse userResponse = userService.getMyInfo(userId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.USER_RETRIEVE_SUCCESS,userResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 팔로잉
    @PostMapping("/follows")
    @PreAuthorize(("isAuthenticated()"))
    public ResponseEntity<?> followUser(@PathVariable String userId){
        userService.followUser(userId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.FOLLOW_CREATE_SUCCESS,null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 언팔로잉
    @DeleteMapping("/unfollows")
    @PreAuthorize(("isAuthenticated()"))
    public ResponseEntity<?> unFollowUser(@PathVariable String userId){
        userService.unFollowUser(userId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.UNFOLLOW_SUCCESS,null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
