package ee.smta.api.error;


import lombok.Getter;


@Getter
public class InternalServerErrorException extends RuntimeException {
   private static final long serialVersionUID = -5226600966257972085L;

   private final ErrorResponse error;

   public InternalServerErrorException() {
      super("Unknown error");
      error = new ErrorResponse(500, getMessage());
   }

   public InternalServerErrorException(int errorCode, String message) {
      super(message);
      error = new ErrorResponse(errorCode, message);
   }
}
