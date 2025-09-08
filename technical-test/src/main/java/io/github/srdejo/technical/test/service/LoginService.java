package io.github.srdejo.technical.test.service;

import io.github.srdejo.technical.test.dtos.request.LoginRequest;
import io.github.srdejo.technical.test.dtos.response.UserResponse;

public interface LoginService {
    /**
     * Authenticates a user using their email and password credentials.
     * <p>
     * The method verifies that:
     * <ul>
     *   <li>A user with the given email exists.</li>
     *   <li>The provided password matches the stored (encoded) password.</li>
     * </ul>
     * If authentication succeeds, the user's {@code lastLogin} timestamp is updated,
     * a new JWT token is generated, and a {@link UserResponse} is returned.
     * </p>
     *
     * @param request the login request containing email and password.
     * @return a {@link UserResponse} with updated user information and a new JWT token.
     * @throws RuntimeException if the user is not found or the credentials are invalid.
     */
    UserResponse login(LoginRequest request);
    /**
     * Authenticates a user using a previously issued JWT token.
     * <p>
     * The token is validated and the user email is extracted from its {@code subject} claim.
     * The method then retrieves the user from the repository. If successful, the user's
     * {@code lastLogin} timestamp is updated, a new JWT token is generated, and a
     * {@link UserResponse} is returned.
     * </p>
     *
     * @param token a valid JWT token containing the user's email as the subject.
     * @return a {@link UserResponse} with updated user information and a new JWT token.
     * @throws RuntimeException if the token is invalid or the user cannot be found.
     */
    UserResponse loginWithToken(String token);
}
