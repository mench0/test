package com.tms.profilemanager.app.entity.modification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import java.util.HashSet;
import java.util.Set;

@Data
@Embeddable
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SetModification<T> {

    @Type(type = "json")
    Set<T> value =  new HashSet<>();

    boolean ignored;

}
