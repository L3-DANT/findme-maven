package exceptions;

/**
 * Exception thrown when a resource is not found in database
 */
public class NotFoundException extends Exception{

    public NotFoundException(String s){
        super(s);
    }
}
