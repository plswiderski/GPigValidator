package org.bitbucket.pablo127.gpigvalidator;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@EqualsAndHashCode
public class ValidationTestObject {

    @NotNull
    private Integer id;

    @Size(min = 3)
    private String name;

    @Size(max = 5)
    private String text;

    @Email
    private String email;

    @NotNull
    @Size(min = 2, max = 2)
    private String specialField;
}