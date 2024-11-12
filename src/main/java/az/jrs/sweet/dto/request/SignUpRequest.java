package az.jrs.sweet.dto.request;

import az.jrs.sweet.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class SignUpRequest {

    String username;

    String email;

    String password;

    Role role;

}
