package exceptions;

/**
 * Exception thrown when a resource is not found in database
 */
public class NotFoundException extends Exception{

    /**
     * Creates the exception with message
     * @param s the message
     * @see Exception#Exception(String)
     */
    public NotFoundException(String s){
        super(s);
    }
}
