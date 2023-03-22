package com.rstejskalprojects.reservationsystem.securityconfig;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.security.filter.JwtFilter;
import com.rstejskalprojects.reservationsystem.service.impl.ReservationServiceImpl;
import com.rstejskalprojects.reservationsystem.service.impl.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTokensControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    UserDetails superAdmin;
    UserDetails admin;
    UserDetails user;
    UserDetails otherUser;
    String loginRequest;

    private final String SUPER_ADMIN_USERNAME = "superadmin";
    private final String ADMIN_USERNAME = "randomadmin";
    private final String USER_USERNAME = "randomuser";
    private final String PASSWORD = "password";

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(jwtFilter) // newly added
                .build();

        superAdmin = new AppUser(1L,"fstname","lastname", SUPER_ADMIN_USERNAME, PASSWORD, UserRoleEnum.SUPER_ADMIN, false, true);
        admin = new AppUser(2L, "fstname","lastname", ADMIN_USERNAME, PASSWORD, UserRoleEnum.ADMIN, false, true);
        user = new AppUser(3L,"fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER, false, true);
        otherUser = new AppUser(14L, "fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER);
        loginRequest = "{\"username\": \"badusername\",\"password\":\"badusername\"}";
        Mockito.when(userDetailsService.loadUserByUsername(SUPER_ADMIN_USERNAME)).thenReturn(superAdmin);
        Mockito.when(userDetailsService.loadUserByUsername(ADMIN_USERNAME)).thenReturn(admin);
        Mockito.when(userDetailsService.loadUserByUsername(USER_USERNAME)).thenReturn(user);


    }
    // POST /api/v1/token/refresh

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testGetRefreshTokenAny() throws Exception {
        mockMvc.perform(post("/api/v1/token/refresh").servletPath("/api/v1/token/refresh")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(user, false)))
                .andExpect(status().isOk());
    }
}
