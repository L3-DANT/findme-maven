package exceptions;

/**
 * Exception thrown in DAOs when trying to inserting data that's already in the database
 */
public class DuplicateDataException extends Exception{

    /**
     * Creates the exception with message
     * @param s the message
     * @see Exception#Exception(String)
     */
    public DuplicateDataException(String s) {
        super(s);
    }
}
