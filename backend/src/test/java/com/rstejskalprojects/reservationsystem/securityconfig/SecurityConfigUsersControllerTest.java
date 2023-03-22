package com.rstejskalprojects.reservationsystem.securityconfig;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.security.filter.JwtFilter;
import com.rstejskalprojects.reservationsystem.service.impl.UserDetailsServiceImpl;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigUsersControllerTest {

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

    private final String SUPER_ADMIN_USERNAME = "superadmin";
    private final String ADMIN_USERNAME = "admin";
    private final String USER_USERNAME = "user";
    private final String PASSWORD = "password";

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(jwtFilter)
                .build();

        superAdmin = new AppUser("fstname","lastname", SUPER_ADMIN_USERNAME, PASSWORD, UserRoleEnum.SUPER_ADMIN);
        admin = new AppUser("fstname","lastname", ADMIN_USERNAME, PASSWORD, UserRoleEnum.ADMIN);
        user = new AppUser("fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER);

        Mockito.when(userDetailsService.loadUserByUsername(SUPER_ADMIN_USERNAME)).thenReturn(superAdmin);
        Mockito.when(userDetailsService.loadUserByUsername(ADMIN_USERNAME)).thenReturn(admin);
        Mockito.when(userDetailsService.loadUserByUsername(USER_USERNAME)).thenReturn(user);
    }



    // PUT /api/v1/users/{userId}/role
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testUpdateUserRoleSuperAdmin() throws Exception {
        String token = jwtUtil.generateToken(superAdmin, false);
        String requestBody = "{ \"role\": \"ROLE_ADMIN\" }";
        mockMvc.perform(put("/api/v1/users/3/role").servletPath("/api/v1/users/3/role")
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testUpdateUserRoleAdmin() throws Exception {
        String token = jwtUtil.generateToken(admin, false);
        String requestBody = "{ \"role\": \"ROLE_ADMIN\" }";
        mockMvc.perform(put("/api/v1/users/3/role").servletPath("/api/v1/users/3/role")
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testUpdateUserRoleUser() throws Exception {
        String token = jwtUtil.generateToken(user, false);
        String requestBody = "{ \"role\": \"ROLE_ADMIN\" }";
        mockMvc.perform(put("/api/v1/users/3/role").servletPath("/api/v1/users/3/role")
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // GET /api/v1/users


    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testGetUsersSuperAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users").servletPath("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testGetUsersAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users").servletPath("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testGetUsersUser() throws Exception {
        mockMvc.perform(get("/api/v1/users").servletPath("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwtUtil.generateToken(user, false)))
                .andExpect(status().isForbidden());
    }

    // POST /api/v1/users
    @Test
    public void testCreateUserAny() throws Exception {
        String jsonBody = "{\"firstName\": \"radim\",\"lastName\": \"stejskal\", \"email\": \"email@email.com\",\"password\": \"newpassword\"}";
        mockMvc.perform(post("/api/v1/users").servletPath("/api/v1/users")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    // POST /api/v1/users/password-reset
    @Test
    public void testPasswordResetAny() throws Exception {
        String jsonBody = "{\"email\": \"some@email.com\"}";
        mockMvc.perform(post("/api/v1/users/password-reset").servletPath("/api/v1/users/password-reset")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    // DELETE /api/v1/users/{userId}

    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testDeleteUserSuperAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1000000").servletPath("/api/v1/users/1000000").header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testDeleteUserAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1000000").servletPath("/api/v1/users/1000000").header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testDeleteUserUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1000000").servletPath("/api/v1/users/1000000").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    // PUT /api/v1/users/{userId}/ban-status
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testBanUserSuperAdmin() throws Exception {
        String jsonBody = "{\"banned\": true}";
        mockMvc.perform(put("/api/v1/users/1000000/ban-status").servletPath("/api/v1/users/1000000/ban-status").header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false))
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testBanUserAdmin() throws Exception {
        String jsonBody = "{\"banned\": true}";
        mockMvc.perform(put("/api/v1/users/1000000/ban-status").servletPath("/api/v1/users/1000000/ban-status").header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false))
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testBanUserUser() throws Exception {
        String jsonBody = "{\"banned\": true}";
        mockMvc.perform(put("/api/v1/users/1000000/ban-status").servletPath("/api/v1/users/1000000/ban-status").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    //PUT /api/v1/users/{userId}/role

    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testRoleUserSuperAdmin() throws Exception {
        String jsonBody = "{\"role\": \"ROLE_ADMIN\"}";
        mockMvc.perform(put("/api/v1/users/1000000/role").servletPath("/api/v1/users/1000000/role").header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false))
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testRoleUserAdmin() throws Exception {
        String jsonBody = "{\"role\": \"ROLE_ADMIN\"}";
        mockMvc.perform(put("/api/v1/users/1000000/role").servletPath("/api/v1/users/1000000/role").header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false))
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testRoleUserUser() throws Exception {
        String jsonBody = "{\"role\": \"ROLE_ADMIN\"}";
        mockMvc.perform(put("/api/v1/users/1000000/role").servletPath("/api/v1/users/1000000/role").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
