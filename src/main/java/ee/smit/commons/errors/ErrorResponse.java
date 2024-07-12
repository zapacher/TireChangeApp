package ee.smit.commons.errors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;


@Data
@Setter(AccessLevel.PRIVATE)
public class ErrorResponse {
   private final int code;
   private final String message;

   public ErrorResponse(int code, String message) {
      this.code = code;
      this.message = message;
   }
}
