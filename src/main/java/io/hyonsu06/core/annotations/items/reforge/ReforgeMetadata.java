package io.hyonsu06.core.annotations.items.reforge;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ReforgeMetadata {
    String ID();
    String lore() default "";
    int[] args() default {};
}
