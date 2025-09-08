package io.github.srdejo.technical.test.service.impl;

import io.github.srdejo.technical.test.dtos.request.SignUpRequest;
import io.github.srdejo.technical.test.dtos.response.UserResponse;
import io.github.srdejo.technical.test.mappers.UserMapper;
import io.github.srdejo.technical.test.entities.User;
import io.github.srdejo.technical.test.exceptions.UserAlreadyExistsException;
import io.github.srdejo.technical.test.service.SignUpService;
import io.github.srdejo.technical.test.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImp jwtServiceImp;

    @Override
    public UserResponse register(SignUpRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new UserAlreadyExistsException("Ya existe un usuario con este email");
        });

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = UserMapper.toEntity(request, encodedPassword);
        userRepository.save(user);

        String token = jwtServiceImp.generateToken(user.getEmail());

        return UserMapper.toResponse(user, token);
    }
}
