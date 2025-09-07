package org.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refresh")
public class TokenController {

//    private final JwtService jwtService;
//
//    @GetMapping("/{id")
//    public ResponseEntity<?> getRefresh(@PathVariable Long id){
//
//        SuccessResponse response = new SuccessResponse(true,"refresh-token 발급 성공",)
//    }
}
