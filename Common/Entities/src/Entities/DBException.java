package Entities;

/**
 * This class is used to represent database exceptions.
 *
 */
public class DBException extends Exception
{

    /**
     * Creates a new instance of DBException.
     */
    public DBException()
    {
        super();
    }




    /**
     * Creates a new instance of DBException with the specified message.
     *
     * @param message The message for the exception
     */
    public DBException(String message)
    {
        super(message);
    }




    /**
     * Creates a new instance of DBException with the specified message and cause.
     *
     * @param message The message for the exception
     * @param cause The cause of the exception
     */
    public DBException(String message, Throwable cause)
    {
        super(message, cause);
    }




    /**
     * Creates a new instance of DBException with the specified cause.
     *
     * @param cause The cause of the exception
     */
    public DBException(Throwable cause)
    {
        super(cause);
    }
}