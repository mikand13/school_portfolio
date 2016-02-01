package no.woact.mikand.models.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This class defines the validator for passwords on the usermodel.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@SuppressWarnings("WeakerAccess")
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword validPassword) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if(password == null)
            return false;

        boolean containsUpperCase = password.matches(".*([A-Z]).*");
        boolean containsLowerCase = password.matches(".*([a-z]).*");
        boolean containsNumber = password.matches(".*([0-9]).*");
        return (containsUpperCase && containsLowerCase && containsNumber);
    }
}
