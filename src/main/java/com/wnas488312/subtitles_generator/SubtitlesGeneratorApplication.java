package com.wnas488312.subtitles_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SubtitlesGeneratorApplication {
	public static void main(String[] args) {
		SpringApplication.run(SubtitlesGeneratorApplication.class, args);
	}
}
