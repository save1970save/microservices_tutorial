package it.swanswan.OrderService.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomErrorException extends RuntimeException{

    public String message;

    public CustomErrorException(String message){
        this.message = message;
    }

    // Overrides Exception's getMessage()
    @Override
    public String getMessage(){
        return message;
    }

}