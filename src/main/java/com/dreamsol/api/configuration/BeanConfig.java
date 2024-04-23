package com.dreamsol.api.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

	@Bean("Message")
	public HashMap<String, String> getMap() {
		return new HashMap<String, String>();
	}

	@Bean("UserMap")
	@Primary
	public Map<String, String> getUserDtoMap() {
		Map<String, String> userDataMapping = new HashMap<>();
		userDataMapping.put("Name", "name");
		userDataMapping.put("Email", "email");
		userDataMapping.put("Mobile", "mobile");
		userDataMapping.put("Department", "department");
		userDataMapping.put("UserType", "usertype");
		return userDataMapping;
	}

	@Bean("DepartmentMap")
	public Map<String, String> getDepartmentDtoMap() {
		Map<String, String> departmentDataMapping = new HashMap<>();
		departmentDataMapping.put("DepartmentName", "departmentName");
		departmentDataMapping.put("DepartmentCode", "departmentCode");
		return departmentDataMapping;
	}

	@Bean("UserTypeMap")
	public Map<String, String> getUserTypeDtoMap() {
		Map<String, String> userTypeDataMapping = new HashMap<>();
		userTypeDataMapping.put("UserTypeName", "UserTypeName");
		return userTypeDataMapping;
	}

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
