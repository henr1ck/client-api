package br.edu.ifpi.client.exception;

public class InvalidHeaderException extends RuntimeException{
    public InvalidHeaderException(String message){
        super(message);
    }
}
