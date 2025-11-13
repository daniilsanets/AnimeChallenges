package sanets.dev.animechallenges.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sanets.dev.animechallenges.dto.LoginRequestDto;
import sanets.dev.animechallenges.dto.SignUpRequestDto;
import sanets.dev.animechallenges.service.AuthService;
import sanets.dev.animechallenges.service.JwtService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Test
    void signup_shouldReturnCreated_whenRequestIsValid() throws Exception{
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setPassword("test@example.com");
        requestDto.setPassword("password123");

        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)

        ).andExpect(status().isCreated()) ;
    }

    @Test
    void login_shouldReturnOkAndToken_whenRequestIsValid() throws Exception {
        String username = "testuser";
        String password = "password123";
        String expectedToken = "fake-jwt-token";

        LoginRequestDto  requestDto = new LoginRequestDto();
        requestDto.setUsernameOrEmail(username);
        requestDto.setPassword(password);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        when(authService.login(username, password)).thenReturn(expectedToken);

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(expectedToken));
    }
}
