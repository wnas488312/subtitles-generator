package com.wnas.subtitles_generator.api.model.response;

/**
 * Response used in endpoints when no specific data needs to be returned.
 * @param status    Basic text message.
 */
public record BasicOkResponse(String status) {

    /**
     * Response with default basic message (Ok).
     * @return  Response object.
     */
    public static BasicOkResponse ok() {
        return new BasicOkResponse("Ok");
    }
}
