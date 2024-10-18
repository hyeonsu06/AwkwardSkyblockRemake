package io.hyonsu06.item.skills;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import static io.hyonsu06.core.functions.NumberTweaks.asPercentageMultiplier;
import static io.hyonsu06.core.functions.getAnnotationArguments.getSkillAnnotation;

@SkillTagged
@Skill(
        ID = "wither_damage_bonus",
        description = "Deal {0}% damage to Withers.",
        args = {
                50
        },
        simpleDescription = true
)
public class WitherDamageBonus implements SkillMethods {
    @SkillTagged
    @Override
    public double onHit(Player player, Entity target, double damage) {
        if (target instanceof Wither) return damage * asPercentageMultiplier(getSkillAnnotation(getClass()).args()[0]);
        else return -1;
    }
}
