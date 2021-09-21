package com.shinley.mysecurity.validation;

import com.shinley.mysecurity.annotation.ValidPassword;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.passay.*;
import org.passay.spring.SpringMessageResolver;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private final SpringMessageResolver springMessageResolver;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        val validator = new PasswordValidator(springMessageResolver, Arrays.asList(
                new LengthRule(8, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                new WhitespaceRule()
        ));
        val result = validator.validate(new PasswordData(s));
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(String.join(",", validator.getMessages(result)));
        return result.isValid();
    }
}
