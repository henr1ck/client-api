package br.edu.ifpi.client.error.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(){
        this("Resource not found!");
    }
}
