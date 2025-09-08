package io.github.srdejo.technical.test.service;

public interface JwtService {

    /**
     * Generates a JWT token signed with HS256 for the given user email.
     * <p>
     * The token contains:
     * <ul>
     *   <li><b>subject</b>: the user's email</li>
     *   <li><b>issuedAt</b>: the date and time when the token was generated</li>
     *   <li><b>expiration</b>: the expiration date, set to 1 hour after issuance</li>
     * </ul>
     *
     * @param email the user’s email address to be set as the token's {@code subject}.
     * @return a signed JWT token valid for 1 hour.
     */
    String generateToken(String email);

    /**
     * Extracts the user email (stored as {@code subject}) from a signed JWT token.
     * <p>
     * The token is validated against the configured signing key. If valid, the claim
     * {@code sub} (subject) is returned, which represents the user’s email.
     * </p>
     *
     * @param token the signed JWT token from which the subject (email) should be extracted.
     * @return the email stored in the {@code subject} claim of the token.
     * @throws io.jsonwebtoken.JwtException if the token is invalid, malformed,
     *                                      cannot be verified, or has expired.
     */
    String extractEmail(String token);
}
