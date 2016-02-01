package no.woact.mikand.models.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDates {
    String message() default "Endtime must be later than starttime!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
