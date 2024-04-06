package com.dreamsol.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Dreamsol-API"), servers = @Server(url = "http://localhost:8088"))
@SpringBootApplication
public class ApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

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
}
