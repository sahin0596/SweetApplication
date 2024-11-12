package az.jrs.sweet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MailRequest {

    String to;

    List<String> cc;

    String subject;

    String message;
}
