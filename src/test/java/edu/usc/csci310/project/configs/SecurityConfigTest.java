package edu.usc.csci310.project.configs;

import edu.usc.csci310.project.service.LoginAttemptService;
import edu.usc.csci310.project.service.ParkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.profiles.active=test"})
public class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private ParkService parkService;

    @Test
    public void filterChainBeanIsRegistered() {
        SecurityFilterChain chain = applicationContext.getBean(SecurityFilterChain.class);
        assertNotNull(chain, "SecurityFilterChain bean should be present in the ApplicationContext");
    }

    @Test
    void beanChecks() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder passwordEncoder = config.passwordEncoder();
        LoginAttemptService loginAttemptService = config.loginAttemptService();
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder, "PasswordEncoder should be an instance of BCryptPasswordEncoder");
        assertInstanceOf(LoginAttemptService.class, loginAttemptService, "loginAttemptService should be an instance of LoginAttemptService");
    }
}