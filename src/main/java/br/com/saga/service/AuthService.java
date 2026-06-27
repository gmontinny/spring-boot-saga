package br.com.saga.service;

import br.com.saga.domain.entity.RefreshToken;
import br.com.saga.domain.entity.RoleEntity;
import br.com.saga.domain.entity.UserEntity;
import br.com.saga.dto.request.LoginRequest;
import br.com.saga.dto.request.RefreshTokenRequest;
import br.com.saga.dto.request.RegisterRequest;
import br.com.saga.dto.response.AuthResponse;
import br.com.saga.exception.BusinessException;
import br.com.saga.repository.RefreshTokenRepository;
import br.com.saga.repository.RoleRepository;
import br.com.saga.repository.UserRepository;
import br.com.saga.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        RoleEntity defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new BusinessException("Role padrão não encontrada"));

        var user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(defaultRole))
                .build();

        userRepository.save(user);
        return generateTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        return generateTokens(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        var refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Refresh token inválido"));

        if (refreshToken.isExpired()) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new BusinessException("Refresh token expirado");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return generateTokens(refreshToken.getUser());
    }

    private AuthResponse generateTokens(UserEntity user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    private String createRefreshToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        var refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
