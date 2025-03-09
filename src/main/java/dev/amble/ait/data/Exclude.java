package dev.amble.ait.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exclude {
    Strategy strategy() default Strategy.ALL;

    enum Strategy {
        ALL, NETWORK, FILE
    }
}
