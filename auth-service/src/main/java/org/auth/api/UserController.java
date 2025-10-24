package org.auth.api;

import lombok.RequiredArgsConstructor;
import org.auth.domain.service.UserService;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/logout/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(@PathVariable String userId){
        userService.logout(userId);
        SuccessResponse response = new SuccessResponse(true,"사용자 로그아웃 성공",null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/batch-status")
    public ResponseEntity<?> activateUsersBatch(@RequestBody List<UUID> userIds){
        userService.activateUsersBatch(userIds);
        SuccessResponse response = new SuccessResponse(true,"사용자 상태 변경 성공",null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
