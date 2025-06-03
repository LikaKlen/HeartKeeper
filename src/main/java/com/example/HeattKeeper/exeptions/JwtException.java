package com.example.HeattKeeper.exeptions;

public class JwtException extends RuntimeException{
    public JwtException(String message) {
        super(message);
    }
}
