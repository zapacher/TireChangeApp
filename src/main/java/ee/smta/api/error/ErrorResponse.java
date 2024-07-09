package ee.smta.api.error;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;


/**
 * ErrorResponse in used for bi-directional error communication to/from softswiss
 */
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
