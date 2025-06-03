package com.example.HeattKeeper;

import com.example.HeattKeeper.configs.FileStorageProperties;
import com.example.HeattKeeper.models.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class HeattKeeperApplication {
	public static User currentUser;

	public static void main(String[] args) {
		SpringApplication.run(HeattKeeperApplication.class, args);
	}

}
