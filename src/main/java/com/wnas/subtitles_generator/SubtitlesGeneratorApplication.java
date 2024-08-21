package com.wnas.subtitles_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
@SpringBootApplication
@EnableScheduling
public class SubtitlesGeneratorApplication {
	public static void main(String[] args) {
		SpringApplication.run(SubtitlesGeneratorApplication.class, args);
	}
}
