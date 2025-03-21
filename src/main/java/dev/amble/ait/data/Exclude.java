package dev.amble.ait.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exclude {
    Strategy strategy() default Strategy.ALL;

    enum Strategy {
        ALL, NETWORK, FILE
    }

    class Impl implements ExclusionStrategy {

        private final Strategy strategy;

        public Impl(Exclude.Strategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            Exclude exclude = field.getAnnotation(Exclude.class);

            if (exclude == null)
                return false;

            Exclude.Strategy excluded = exclude.strategy();
            return excluded == Exclude.Strategy.ALL || excluded == strategy;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
