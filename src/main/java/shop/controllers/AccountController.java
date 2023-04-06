package shop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.dto.account.GoogleAuthDto;
import shop.dto.account.LoginDto;
import shop.dto.account.AuthResponseDto;
import shop.dto.account.RegisterDto;
import shop.google.GoogleAuthService;
import shop.services.AccountService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/google-auth")
    public ResponseEntity<AuthResponseDto> googleLogin(@RequestBody GoogleAuthDto googleAuth) {
        try {
            var result = googleAuthService.verify(googleAuth.getToken());
            return ResponseEntity.ok(service.googleAuth(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @RequestBody RegisterDto request
    ) {
        var recaptchaResult = service.recaptchaCheck(request.getReCaptchaToken());
        if(!recaptchaResult.isSuccess())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var auth = service.register(request);
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody LoginDto request
    ) {
        var recaptchaResult = service.recaptchaCheck(request.getReCaptchaToken());
        if(!recaptchaResult.isSuccess())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var auth = service.login(request);
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(auth);
    }
}