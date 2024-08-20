package com.wnas488312.subtitles_generator.api.endpoint;

import com.wnas488312.subtitles_generator.api.model.request.InputProcessRequest;
import com.wnas488312.subtitles_generator.api.model.response.InputProcessResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputProcessEndpointTest {
    private static final String INPUT_TO_PROCESS = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore
            et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
            cillum dolore eu fugiat nulla pariatu
            """;

    @Test
    void processInputTest() {
        InputProcessResponse response = new InputProcessEndpoint().processInput(new InputProcessRequest(INPUT_TO_PROCESS));

        assertThat(response).isNotNull();
        assertThat(response.message()).hasSize(9);
    }
}