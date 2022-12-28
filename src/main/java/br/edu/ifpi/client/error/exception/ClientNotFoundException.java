package br.edu.ifpi.client.error.exception;

public class ClientNotFoundException extends ResourceNotFoundException{
    public ClientNotFoundException(String message){
        super(message);
    }
}
