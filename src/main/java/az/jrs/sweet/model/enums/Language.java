package az.jrs.sweet.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {

    AZE("az"),
    RU("ru"),
    ENG("en");

    private final String propertyName;
}

