package ee.smit.commons.errors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

   @ExceptionHandler
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   @ResponseBody
   protected ErrorResponse processException(BadRequestException ex) {
      log.warn(ex.getMessage(), ex);
      return ex.getError();
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   protected ErrorResponse processException(InternalServerErrorException ex) {
      log.error(ex.getMessage(), ex);
      return ex.getError();
   }
}
