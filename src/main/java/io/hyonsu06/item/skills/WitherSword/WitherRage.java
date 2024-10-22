package io.hyonsu06.item.skills.WitherSword;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static io.hyonsu06.core.functions.NumberTweaks.asPercentageMultiplier;
import static io.hyonsu06.core.functions.getAnnotationArguments.getSkillAnnotation;
import static io.hyonsu06.core.functions.setSkillMapOfEntity.setBonus;

@SkillTagged
@Skill(
        ID = "wither_rage",
        name = "Wither Rage",
        description = "Grants {0} ferocity and {1}% damage to all mobs - {2}% additional for withers for {3} seconds.",
        cooldown = 1200,
        cost = 500,
        args = {
                200,
                50,
                75,
                5
        }
)
public class WitherRage implements SkillMethods {
    @SkillTagged
    @Override
    public void onRightClick(Player player) {
        Skill skill = getSkillAnnotation(getClass());
        double bonus = skill.args()[0];
        long duration = skill.args()[3];
        setBonus(player, skill.ID(), Stats.FEROCITY, bonus, duration);
    }

    @SkillTagged
    @Override
    public double onHit(Player player, Entity entity, double damage) {
        Skill skill = getSkillAnnotation(getClass());
        double bonus1 = skill.args()[1];
        double bonus2 = skill.args()[2];

        damage *= asPercentageMultiplier(bonus1);
        if (entity.getType().equals(EntityType.WITHER)) damage *= asPercentageMultiplier(bonus2);

        return damage;
    }
}
