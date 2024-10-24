package io.hyonsu06.item.skills;

import io.hyonsu06.core.managers.entity.EntityManager;
import io.hyonsu06.core.managers.StatManager;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.enums.DamageType;
import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static io.hyonsu06.core.functions.customCauseAndAttacker.causeAndAttacker;
import static io.hyonsu06.core.functions.getAnnotationArguments.getSkillAnnotation;
import static io.hyonsu06.core.functions.getDamageCause.getCause;
import static io.hyonsu06.core.functions.setImmuneTime.setNoDamageTicks;
import static io.hyonsu06.core.functions.setSkillMapOfEntity.setBonus;

@SkillTagged
@Skill(
        ID = "salvation",
        name = "Salvation",
        description = "Can be casted after landing {1} hits. " +
                "Shooting a beam for {0} blocks, penetrating all enemies in path dealing {4} damage, " +
                "scaling with crit chance. Also grants {2} Bonus Attack Speed for {3} seconds (Does not stack).",
        damageType = DamageType.MAGIC,
        args = {
                7,
                5,
                50,
                10,
                10000
        }
)
public class Salvation implements SkillMethods {
    @SkillTagged
    @Override
    public void onSneakLeftClick(Player player) {
        if (player.isSneaking()) {
            Skill skill = getSkillAnnotation(getClass());
            int hits;
            try {
                hits = EntityManager.getRangedHits().get(player.getUniqueId());
            } catch (Exception ignored) {
                hits = 0;
            }
            if (hits >= skill.args()[1]) {
                setBonus(player, skill.ID(), Stats.ATTACKSPEED, skill.args()[2], (int) skill.args()[3] * 20);
                double mul = StatManager.getFinalStatMap().get(player.getUniqueId()).get(Stats.CRITCHANCE);

                EntityManager.getRangedHits().put(player.getUniqueId(), 0);
                Vector direction = player.getLocation().getDirection().normalize();
                Location loc = player.getLocation().add(0, 1.5, 0);
                long blocks = skill.args()[0];
                List<Entity> victims = new ArrayList<>();
                for (double i = 0; i < blocks; i += 0.1) {
                    loc.add(direction);
                    loc.getWorld().spawnParticle(Particle.DUST, loc, 1, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 2));

                    for (Entity e : loc.getNearbyEntities(.25, .25, .25)) {
                        if (!victims.contains(e)) {
                            if (e instanceof LivingEntity le) {
                                if (!le.equals(player)) {
                                    double damage = skill.args()[4] * (1 + (mul / 100));
                                    le.damage(damage, causeAndAttacker(getCause(skill.damageType()), player, le));
                                    setNoDamageTicks(le, 0);
                                    victims.add(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
