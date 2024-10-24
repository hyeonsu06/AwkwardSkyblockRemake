package io.hyonsu06.core.annotations.tags;

public @interface RecipeTagged {
    boolean requireTag() default false;
}
