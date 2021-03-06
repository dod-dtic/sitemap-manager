package mil.dtic.sitemaps.management.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller to handle any kind of uncaught exception. This will ensure an
 * exception (runtime) which percolates up through the rest controller will
 * result in the called receiving a 500 error rather than any kind of exception
 * details.
 *
 * Source: http://stackoverflow.com/a/23580906/5334997
 *
 */
@ControllerAdvice
public class ExceptionHandlerController {
    
    /**
     * Default handler for exceptions
     * @param request HTTP request resulting in exception
     * @param e Exception to handle
     * @return ResponseEntity to send to user
     */
    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ResponseEntity<String> defaultErrorHandler(HttpServletRequest request, Exception e) {
		return new ResponseEntity<String>("an error has occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}