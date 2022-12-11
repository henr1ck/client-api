package br.edu.ifpi.client.error.exception;

public class InvalidHeaderException extends RuntimeException{
    public InvalidHeaderException(String message){
        super(message);
    }
}
