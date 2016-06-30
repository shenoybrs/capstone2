package com.capstone.offerbank;

import java.util.List;

public class AppResponse {



    public List<App> apps;


    public ResponseError error;

    public static class ResponseError {
        public int code;
        public String message;
    }
}
