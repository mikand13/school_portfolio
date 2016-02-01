package no.woact.mikand.models;

import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class defines the usertype enum for the user model.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public enum UserType {
    STUDENT, TEACHER;

    private static final Function<Enum<UserType>, SelectItem> userTypeExtractor = t -> new SelectItem(t, t.name());

    public static List<SelectItem> getAllUserTypesAsSelectItem() {
        return Arrays.asList(UserType.values()).stream()
                .map(UserType::extractUserType)
                .collect(Collectors.toList());
    }

    private static SelectItem extractUserType(UserType userType) {
        return userTypeExtractor.apply(userType);
    }
}
