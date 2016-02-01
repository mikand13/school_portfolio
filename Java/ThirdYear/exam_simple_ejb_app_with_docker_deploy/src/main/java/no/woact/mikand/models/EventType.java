package no.woact.mikand.models;

import javax.faces.model.SelectItem;
import java.util.function.Function;

/**
 * This enum determines the different types of events.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public enum EventType {
    LECTURE, EXERCISE;

    private static final Function<Enum<EventType>, SelectItem> userTypeExtractor = t -> new SelectItem(t, t.name());

    public static SelectItem extractEventType(EventType eventType) {
        return userTypeExtractor.apply(eventType);
    }
}
