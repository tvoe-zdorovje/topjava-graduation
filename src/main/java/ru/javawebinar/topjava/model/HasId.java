package ru.javawebinar.topjava.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Access(AccessType.FIELD)
public interface HasId<ID> extends Persistable<ID> {
    void setId(ID id);

    @Override
    default boolean isNew() {
        return getId()==null;
    }
}