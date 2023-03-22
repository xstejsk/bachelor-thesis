package com.rstejskalprojects.reservationsystem.securityconfig;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.model.UserToken;
import com.rstejskalprojects.reservationsystem.security.filter.JwtFilter;
import com.rstejskalprojects.reservationsystem.service.impl.RegistrationServiceImpl;
import com.rstejskalprojects.reservationsystem.service.impl.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.service.impl.UserTokenService;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigConfirmationTokensControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private RegistrationServiceImpl registrationService;

    @MockBean
    private UserTokenService userTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    UserDetails superAdmin;
    UserDetails admin;
    UserDetails user;
    UserToken userToken;

    private final String SUPER_ADMIN_USERNAME = "superadmin";
    private final String ADMIN_USERNAME = "admin";
    private final String USER_USERNAME = "user";
    private final String PASSWORD = "password";
    private final String forgotPasswordRequest = "{ \"email\": \"email@email.com\"}";
    private final String confirmationTokenRequest = "{ \"token\": \"token\"}";

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .addFilter(jwtFilter)
                .build();

        superAdmin = new AppUser("fstname","lastname", SUPER_ADMIN_USERNAME, PASSWORD, UserRoleEnum.SUPER_ADMIN);
        admin = new AppUser("fstname","lastname", ADMIN_USERNAME, PASSWORD, UserRoleEnum.ADMIN);
        user = new AppUser("fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER);
        userToken = new UserToken();
        Mockito.doNothing().when(registrationService).resendRegistrationEmail(Mockito.any());
        Mockito.doNothing().when(registrationService).resendRegistrationEmail(Mockito.any());
        Mockito.doNothing().when(userTokenService).confirmToken(Mockito.any());
        Mockito.when(userTokenService.getToken(Mockito.any())).thenReturn(userToken);
        Mockito.when(userDetailsService.loadUserByUsername(SUPER_ADMIN_USERNAME)).thenReturn(superAdmin);
        Mockito.when(userDetailsService.loadUserByUsername(ADMIN_USERNAME)).thenReturn(admin);
        Mockito.when(userDetailsService.loadUserByUsername(USER_USERNAME)).thenReturn(user);
    }

    // POST /api/v1/confirmation/resend-confirmation

    @Test
    public void testResendRegistrationTokenAny() throws Exception {
        mockMvc.perform(post("/api/v1/confirmations/resend-confirmation").servletPath("/api/v1/confirmations/resend-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(forgotPasswordRequest))
                .andExpect(status().isOk());
    }

    // PUT /api/v1/confirmation/resend-confirmation

    @Test
    public void testSubmitTokenAny() throws Exception {
        mockMvc.perform(put("/api/v1/confirmations/submit-token").servletPath("/api/v1/confirmations/submit-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(confirmationTokenRequest))
                .andExpect(status().isOk());
    }
}
