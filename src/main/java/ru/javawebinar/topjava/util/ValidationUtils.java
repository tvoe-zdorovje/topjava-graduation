package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.HasId;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;

import java.util.Objects;

public class ValidationUtils {

    public static <ID> void checkNew(HasId<ID> entity) {
        if (!entity.isNew()) {
            throw new IllegalRequestDataException(entity + " must be new (id=null)");
        }
    }

    public static <ID> void assureIdConsistent(HasId<ID> entity, ID id) {
        if (entity.isNew()) {
            entity.setId(id);
        } else {
            if (!Objects.equals(entity.getId(), id)) throw new IllegalRequestDataException(entity + " must be with id=" + id);
        }
    }

}