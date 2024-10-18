package io.hyonsu06.core.functions;

import io.hyonsu06.core.annotations.tags.ItemTagged;
import io.hyonsu06.core.annotations.tags.ReforgeTagged;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import org.reflections.Reflections;

import java.util.Set;

public class getClasses {
    private static Set<Class<?>> itemReflections;
    private static Set<Class<?>> skillReflections;
    private static Set<Class<?>> reforgeReflections;

    public static Set<Class<?>> getItemClasses() {
        if (itemReflections == null) {
            itemReflections = new Reflections("io.hyonsu06.item").getTypesAnnotatedWith(ItemTagged.class);
        }
        return itemReflections;
    }

    public static Set<Class<?>> getSkillClasses() {
        if (skillReflections == null) {
            skillReflections = new Reflections("io.hyonsu06.item.skills").getTypesAnnotatedWith(SkillTagged.class);
        }
        return skillReflections;
    }

    public static Set<Class<?>> getReforgeClasses() {
        if (reforgeReflections == null) {
            reforgeReflections = new Reflections("io.hyonsu06.item.reforges").getTypesAnnotatedWith(ReforgeTagged.class);
        }
        return reforgeReflections;
    }
}
