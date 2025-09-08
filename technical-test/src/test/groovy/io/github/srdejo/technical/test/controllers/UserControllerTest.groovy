package io.github.srdejo.technical.test.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.srdejo.technical.test.dtos.request.LoginRequest
import io.github.srdejo.technical.test.dtos.request.PhoneRequest
import io.github.srdejo.technical.test.dtos.request.SignUpRequest
import io.github.srdejo.technical.test.entities.User
import io.github.srdejo.technical.test.repositories.UserRepository
import org.spockframework.spring.SpringBean
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import javax.annotation.Resource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSpec extends Specification {

    @Resource
    MockMvc mockMvc
    @Resource
    ObjectMapper objectMapper
    @Resource
    UserRepository userRepository

    def "should register user successfully"() {
        given: "a valid sign up request"
        def request = new SignUpRequest(
                email: "testuser@example.com",
                password: "abcDef12",
                name: "Test User",
                phones: [new PhoneRequest(12345678L, 1, "+57")]
        )

        when: "the request is sent to the API"
        def result = mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "the response is created"
        result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').exists())
                .andExpect(jsonPath('$.token').exists())
                .andExpect(jsonPath('$.active').value(true))

        and: "the user is persisted in the DB"
        userRepository.findByEmail("testuser@example.com").isPresent()
    }

    def "should fail if email invalid"() {
        given: "a request with invalid email"
        def request = new SignUpRequest(email: "bademail@domain", password: "abcDef12")

        when: "the request is sent"
        def result = mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "validation error is returned"
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error[0].detail').value("El correo debe tener formato válido"))
    }

    def "should fail if password invalid"() {
        given: "a request with invalid password"
        def request = new SignUpRequest(email: "valid@email.com", password: "password")

        when: "the request is sent"
        def result = mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "validation error is returned"
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error[0].detail').value("La contraseña no cumple con el formato requerido"))
    }

    def "should fail if user already exists"() {
        given: "a persisted user"
        def existing = new User(id: UUID.randomUUID(), email: "dupe@example.com", password: "abcDef12")
        userRepository.save(existing)

        and: "a sign up request with same email"
        def request = new SignUpRequest(email: "dupe@example.com", password: "abcDef12")

        when: "the request is sent"
        def result = mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "conflict error is returned"
        result.andExpect(status().isConflict())
                .andExpect(jsonPath('$.error[0].detail').value("Ya existe un usuario con este email"))
    }

    def "should login successfully"() {
        given: "a registered user"
        def signUp = new SignUpRequest(email: "login@example.com", password: "abcDef12", name: "Test User")
        mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isCreated())

        and: "valid login credentials"
        def login = new LoginRequest(email: "login@example.com", password: "abcDef12")

        when: "the login request is sent"
        def result = mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))

        then: "login succeeds with token"
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.token').exists())
                .andExpect(jsonPath('$.lastLogin').exists())
    }

    def "should login with token successfully"() {
        given: "a registered user"
        def signUp = new SignUpRequest(email: "login2@example.com", password: "abcDef12", name: "Test User")
        def loginResponse = mockMvc.perform(post("/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.token').exists())
                .andExpect(jsonPath('$.lastLogin').exists())
                .andReturn()
                .response
                .contentAsString

        and: "extracting the token"
        def jsonNode = objectMapper.readTree(loginResponse)
        def token = jsonNode.get("token").asText()

        when: "the login with token is called"
        def result = mockMvc.perform(get("/user/login")
                .header("Authorization", "Bearer $token"))

        then: "login succeeds"
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.id').exists())
                .andExpect(jsonPath('$.token').exists())
                .andExpect(jsonPath('$.lastLogin').exists())
                .andExpect(jsonPath('$.active').value(true))
    }

    def "should fail login without token"() {
        when: "login is called without token"
        def result = mockMvc.perform(get("/user/login")
                .header("Authorization", "Jwt "))

        then: "bad request error is returned"
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error[0].detail').value("Token no proporcionado o inválido"))
    }
}
