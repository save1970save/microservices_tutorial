package it.swanswan.InventoryService.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collection;

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