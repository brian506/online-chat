package org.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateChatUserRequest;
import org.chat.domain.dto.response.ChatUserResponse;
import org.chat.domain.dto.response.DifferentUserResponse;
import org.chat.domain.service.ChatUserService;
import org.chat.security.StompPrincipal;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/chat/users")
@RequiredArgsConstructor
public class ChatUserController {

    private final ChatUserService userService;

    // 사용자 정보 입력 생성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createUserInfo(@AuthenticationPrincipal StompPrincipal me,
                                            @RequestBody CreateChatUserRequest request){
        String nickname = userService.createUserInfo(request, me.getUserId());
        SuccessResponse response = new SuccessResponse(true,"사용자 정보 생성 성공",nickname);
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
    // 이름(실명)으로 다른 사용자 찾기
    @GetMapping("/find-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findUser(@RequestParam("username") String username){
        DifferentUserResponse userResponse = userService.findUser(username);
        SuccessResponse response = new SuccessResponse(true,"사용자 조회 성공",userResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
