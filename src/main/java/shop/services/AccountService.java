package shop.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.client.RestOperations;
import shop.configuration.captcha.CaptchaSettings;
import shop.configuration.captcha.GoogleResponse;
import shop.dto.account.GoogleAuthDto;
import shop.dto.account.LoginDto;
import shop.dto.account.AuthResponseDto;
import shop.dto.account.RegisterDto;
import shop.configuration.security.JwtService;
import shop.constants.Roles;
import shop.entities.UserEntity;
import shop.entities.UserRoleEntity;
import shop.repositories.RoleRepository;
import shop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shop.repositories.UserRoleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CaptchaSettings captchaSettings;
    private final RestOperations restTemplate;
    protected static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    public AuthResponseDto register(RegisterDto request) {
        var user = UserEntity.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .phone("093 839 43 23")
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        repository.save(user);
        var role = roleRepository.findByName(Roles.User);
        var ur = new UserRoleEntity().builder()
                .user(user)
                .role(role)
                .build();
        userRoleRepository.save(ur);

        var jwtToken = jwtService.generateAccessToken(user);
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponseDto login(LoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateAccessToken(user);
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponseDto googleAuth(GoogleIdToken.Payload request) {
        String email = request.getEmail();
        Optional<UserEntity> user = repository.findByEmail(email);
        if(user.isEmpty())
        {
            RegisterDto newUser = new RegisterDto().builder()
                    .email(email)
                    .firstname((String)request.get("given_name"))
                    .lastname((String)request.get("family_name"))
                    .password("0000000")
                    .build();
            return register(newUser);
        }
        var jwtToken = jwtService.generateAccessToken(user.get());
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public GoogleResponse recaptchaCheck(String token)
    {
        String url = String.format(RECAPTCHA_URL_TEMPLATE, captchaSettings.getSecret(), token);
        return restTemplate.getForObject(url, GoogleResponse.class);
    }
}