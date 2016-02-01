package no.woact.mikand.models.constraint;

import no.woact.mikand.models.Event;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This class validates that the endtime of an even is later than the starttime.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class DatesValidator implements ConstraintValidator<ValidDates, Event> {
    @Override
    public void initialize(ValidDates validDates) {}

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext constraintValidatorContext) {
        return !(event.getStartTime() == null || event.getEndTime() == null) &&
                event.getStartTime().compareTo(event.getEndTime()) <= 0;
    }
}
