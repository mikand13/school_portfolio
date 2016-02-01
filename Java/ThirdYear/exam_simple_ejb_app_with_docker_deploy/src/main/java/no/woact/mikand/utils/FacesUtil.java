package no.woact.mikand.utils;

import no.woact.mikand.models.Event;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Set;

/**
 * This class has util methods for Facesmessages.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class FacesUtil {
    public static void addViolationsToFacesMessages(Set<ConstraintViolation<Event>> violations) {
        violations.stream()
                .map(ConstraintViolation::getMessage)
                .map(FacesMessage::new)
                .forEach(fm -> FacesContext.getCurrentInstance().addMessage(null, fm));
    }

    public static void createMessage(String id, String message) {
        FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(message));
    }

    public static String makeCreateMessage(String user) {
        return user + " successfully created!";
    }

    public static void doRedirect(String type, String url) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getFlash()
                    .put("created", FacesUtil.makeCreateMessage(type));
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
