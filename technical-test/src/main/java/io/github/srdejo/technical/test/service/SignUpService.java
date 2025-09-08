package io.github.srdejo.technical.test.service;

import io.github.srdejo.technical.test.dtos.request.SignUpRequest;
import io.github.srdejo.technical.test.dtos.response.UserResponse;

import javax.validation.Valid;

public interface SignUpService {
    /**
     * Registers a new user in the system.
     * <p>
     * The method performs the following steps:
     * <ul>
     *   <li>Validates that no user already exists with the given email.</li>
     *   <li>Encodes the provided password using the configured {@code PasswordEncoder}.</li>
     *   <li>Creates and persists a new {@link User} entity from the request data.</li>
     *   <li>Generates a JWT token for the newly created user.</li>
     * </ul>
     * </p>
     *
     * @param request the sign-up request containing user information (email, password, name, phones, etc.).
     * @return a {@link UserResponse} containing the persisted user details and a JWT token.
     * @throws UserAlreadyExistsException if a user with the given email is already registered.
     */
    UserResponse register(@Valid SignUpRequest request);
}
