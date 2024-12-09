package az.jrs.sweet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class SignInRequest {

    String email;

    String password;

}
