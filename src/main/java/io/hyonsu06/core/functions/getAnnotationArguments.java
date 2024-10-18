package io.hyonsu06.core.functions;

import io.hyonsu06.core.annotations.items.reforge.ReforgeMetadata;
import io.hyonsu06.core.annotations.skills.Skill;

public class getAnnotationArguments {
    public static Skill getSkillAnnotation(Class<?> clazz) {
        return clazz.getAnnotation(Skill.class);
    }

    public static ReforgeMetadata getReforgeAnnotation(Class<?> clazz) {
        return clazz.getAnnotation(ReforgeMetadata.class);
    }
}
