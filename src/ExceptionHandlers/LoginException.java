
package ExceptionHandlers;


public class LoginException extends RuntimeException {

    public LoginException(String errorMessage, Throwable err) {
        super(errorMessage);
    }
    
    
}
