package org.emiloanwithbill.annotations;

import java.lang.reflect.Field;

public class PasswordValidator {

    public static void validate(Object obj) {

        for (Field field : obj.getClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(PasswordRegex.class)) {

                PasswordRegex annotation = field.getAnnotation(PasswordRegex.class);
                String regex = annotation.regex();

                field.setAccessible(true);

                String value;
                try {
                    value = (String) field.get(obj);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to read password field", e);
                }

                if (value == null) {
                    throw new IllegalArgumentException("Password cannot be null");
                }

                if (!value.matches(regex)) {
                    throw new IllegalArgumentException(annotation.message());
                }
            }
        }
    }
}
