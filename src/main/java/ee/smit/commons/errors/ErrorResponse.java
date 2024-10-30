package ee.smit.commons.errors;

import ee.smit.clients.api.Response;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;


@Data
@Setter(AccessLevel.PRIVATE)
public class ErrorResponse implements Response, ee.smit.controller.api.groups.Response {
   private final int code;
   private final String message;

   public ErrorResponse(int code, String message) {
      this.code = code;
      this.message = message;
   }
}
