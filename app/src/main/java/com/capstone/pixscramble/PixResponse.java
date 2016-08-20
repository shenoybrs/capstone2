package com.capstone.pixscramble;

import java.util.List;

public class PixResponse {
    public List<Pix> pixs;
    public ResponseError error;

    public static class ResponseError {
        public int code;
        public String message;
    }
}
