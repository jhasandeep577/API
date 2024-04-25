package com.dreamsol.api.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.dreamsol.api.security.CustomAccessDeniedHandler;
import com.dreamsol.api.security.JwtAutenticationEntryPoint;
import com.dreamsol.api.security.JwtFilter;
import com.dreamsol.api.services.EndPointUtility;

@Configuration
@EnableWebMvc
@Component
public class SecurityConfig {
    @Autowired
    CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private JwtAutenticationEntryPoint entryPoint;
    @Autowired
    private JwtFilter filter;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserDetailsService userDetailService;
   
    EndPointUtility endPointUtility = new EndPointUtility();

    public HttpSecurity security;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        this.security = http;
        security.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                                .permitAll());
        security.exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        security.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }

    private String[] getAuthorizedUrls() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            List<String> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .map(auth -> {
                        return auth.getAuthority();
                    }).collect(Collectors.toList());
                    return endPointUtility.getAuthorizedUrls(auths);
          }
   return new String[0];
}

    private String[] getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .map(Object::toString)
                        .toArray(String[]::new)
                : new String[0];
    }

    public void updateSecurity() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            try {
                security.authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                                .permitAll()
                                .requestMatchers(this.getAuthorizedUrls())
                                .hasAnyAuthority(this.getAuthorities()));
                System.out.println("<------- Security Updated successfully ------>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, List<String>> getPermissionandRoleMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("Admin", getApifromKey().keySet().stream().map(key -> key).collect(Collectors.toList()));
        map.put("User", List.of("UserSheetFormat", "UserSheetValidate", "UserFile", "UserGet", "UserDelete",
                "UserUpdate", "UserSaveSheet", "UserDownloadSheet"));
        map.put("All", getApifromKey().keySet().stream().map(key -> key).collect(Collectors.toList()));
        map.put("READ",
                List.of("DepartmentDownloadSheet", "UserDownloadSheet", "UserTypeDownloadSheet", "UserPermissionGet",
                        "UserPermissionGetAll", "UserTypeGetUsers", "UserTypeGet", "UserTypeGetAll",
                        "UserTypeGetAllUsers", "UserGetAll", "UserGet", "UserSheetFormat", "DepartmentGetAllUsers",
                        "DepartmentGetAll", "DepartmentGet", "DepartmentGetUsers"));
        return map;
    }

    public Map<String, String> getApifromKey() {
        Map<String, String> apiMap = new HashMap<>();
        apiMap.put("DepartmentGetAllUsers", "/Department/fetch-all-Departments-with-Users");
        apiMap.put("DepartmentGetAll", "/Department/fetch-all-Departments");
        apiMap.put("DepartmentCreate", "/Department/create-Department");
        apiMap.put("DepartmentDelete", "/Department/delete-Department/*");
        apiMap.put("DepartmentUpdate", "/Department/update-Department/*");
        apiMap.put("DepartmentDownloadSheet", "/Department/download-Department-Excel-Sheet");
        apiMap.put("DepartmentGet", "/Department/fetch-Department/*");
        apiMap.put("DepartmentGetUsers", "/Department/fetch-Department-with-Users");
        apiMap.put("DepartmentSaveSheet", "/Department/save-Department-Excel-Data");
        apiMap.put("UserGetAll", "/User/fetch-all-Users");
        apiMap.put("UserDelete", "/User/delete-User/*");
        apiMap.put("UserUpdate", "/User/update-User/*");
        apiMap.put("UserDownloadSheet", "/User/download-User-Excel-Sheet");
        apiMap.put("UserGet", "/User/fetch-User/*");
        apiMap.put("UserSaveSheet", "/User/save-User-Excel-Data");
        apiMap.put("UserSheetValidate", "/User/validate-Excel-Data");
        apiMap.put("UserSheetFormat", "/User/download-Excel-Format");
        apiMap.put("UserFile", "/User/download/*");
        apiMap.put("UserTypeGetAllUsers", "/User-Type/fetch-all-UserTypes-with-Users");
        apiMap.put("UserTypeGetAll", "/User-Type/fetch-all-UserTypes");
        apiMap.put("UserTypeDelete", "/User-Type/delete-UserType/*");
        apiMap.put("UserTypeUpdate", "/User-Type/update-UserType/*");
        apiMap.put("UserTypeCreate", "/User-Type/create-UserType");
        apiMap.put("UserTypeDownloadSheet", "/User-Type/download-UserType-Excel-Sheet");
        apiMap.put("UserTypeGet", "/User-Type/fetch-UserType/*");
        apiMap.put("UserTypeGetUsers", "/User-Type/fetch-UserType-with-Users");
        apiMap.put("UserTypeSaveSheet", "/User-Type/save-UserType-Excel-Data");
        apiMap.put("UserPermissionGetAll", "/User-Permission/fetch-all-Permissions");
        apiMap.put("UserPermissionGet", "/User-Permission/fetch-permission/*");
        apiMap.put("UserPermissionDelete", "/User-Permission/delete-UserPermission/*");
        apiMap.put("UserPermissionUpdate", "/User-Permission/update-UserPermission/*");
        apiMap.put("UserPermissionCreate", "/User-Permission/create-UserPermission");
        apiMap.put("UserPermissionAddEndPoint", "/User-Permission/add-Endpoint");
        return apiMap;
    }
}
