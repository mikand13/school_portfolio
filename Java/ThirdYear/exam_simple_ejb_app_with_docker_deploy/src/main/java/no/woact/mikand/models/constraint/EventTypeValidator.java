package no.woact.mikand.models.constraint;

import no.woact.mikand.models.EventType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * This class defines the validator for eventtypes, and checks that the eventtype sent from browser is valid.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class EventTypeValidator implements ConstraintValidator<ValidEventTypes, EventType> {
    @Override
    public void initialize(ValidEventTypes validEventTypes) {

    }

    @Override
    public boolean isValid(EventType eventType, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.asList(EventType.values()).contains(eventType);
    }
}
