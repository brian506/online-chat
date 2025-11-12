package org.user.api;

import lombok.RequiredArgsConstructor;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.dto.response.SignUpUserResponse;
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
    // todo 사용자 정보 입력하고 나서 만약에 에러가 날때 nickname 저장한거 롤백 고려
    @GetMapping("/check-nickname/{nickname}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> duplicateNickname(@PathVariable String nickname){
        boolean isDuplicated = userService.validateNickname(nickname);
        SuccessResponse response = new SuccessResponse(true,"닉네임 중복 확인 성공",isDuplicated);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    // 내 정보 조회
    @GetMapping("/my-page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal StompPrincipal me){
        ChatUserResponse userResponse = userService.getMyInfo(me.getUserId());
        SuccessResponse response = new SuccessResponse(true,"내 정보 조회 성공",userResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
