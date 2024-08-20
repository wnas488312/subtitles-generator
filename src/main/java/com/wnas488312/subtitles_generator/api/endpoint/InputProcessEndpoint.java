package com.wnas488312.subtitles_generator.api.endpoint;

import com.wnas488312.subtitles_generator.api.InputProcessApi;
import com.wnas488312.subtitles_generator.api.model.request.InputProcessRequest;
import com.wnas488312.subtitles_generator.api.model.response.InputProcessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class InputProcessEndpoint implements InputProcessApi {
    private static final int PREFERRED_CHARS_IN_LINE = 35;
    private static final int MAX_CHARS_IN_LINE = 45;
    private static final int MAX_WORD_SIZE = 20;

    /**
     * {@inheritDoc}
     */
    @Override
    public InputProcessResponse processInput(@RequestBody InputProcessRequest request) {
        String userInput = request.input();
        String[] split = userInput.split(" ");

        List<String> response = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<String> splitList = new ArrayList<>(Arrays.asList(split));

        for (int i = 0; i < splitList.size(); i++) {
            String item = splitList.get(i);

            if (sb.length() + item.length() >= MAX_CHARS_IN_LINE && item.length() >= MAX_WORD_SIZE) {
                String firstHalfOfAWord = String.format("%s-", item.substring(0, MAX_CHARS_IN_LINE));
                sb.append(firstHalfOfAWord);
                response.add(sb.toString().trim());
                sb = new StringBuilder();

                String secondHalfOfAWord = item.substring(MAX_CHARS_IN_LINE);
                splitList.add(i + 1, secondHalfOfAWord);
            } else if (sb.length() >= PREFERRED_CHARS_IN_LINE || item.endsWith(",") || item.endsWith(".")) {
                sb.append(item);
                response.add(sb.toString().trim());
                sb = new StringBuilder();
            } else {
                sb.append(item);
                sb.append(" ");
            }
        }

        return new InputProcessResponse(response);
    }
}
