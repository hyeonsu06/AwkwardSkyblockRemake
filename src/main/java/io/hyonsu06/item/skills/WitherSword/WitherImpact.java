package io.hyonsu06.item.skills.WitherSword;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.enums.DamageType;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static io.hyonsu06.core.functions.customCauseAndAttacker.causeAndAttacker;
import static io.hyonsu06.core.functions.getAnnotationArguments.getSkillAnnotation;
import static io.hyonsu06.core.functions.getDamageCause.getCause;
import static io.hyonsu06.core.functions.getMagicDamage.magicDamage;

@SkillTagged
@Skill(
        ID = "wither_impact",
        name = "Wither Impact",
        description = "Teleporting {1} blocks forward, dealing {0} damage to all entities in {2} blocks.",
        cooldown = 5,
        cost = 100,
        damageType = DamageType.MAGIC,
        args = {
                100000,
                10,
                4
        }
)
public class WitherImpact implements SkillMethods {
    @SkillTagged
    @Override
    public void onRightClick(Player player) {
        Skill skill = getSkillAnnotation(getClass());
        long damage = skill.args()[0];
        long distance = skill.args()[1];
        long range = skill.args()[2];
        org.bukkit.damage.DamageType cause = getCause(skill.damageType());

        // Teleport player
        player.teleport(player.getLocation().add(0, 1.25, 0));
        Vector direction = player.getLocation().getDirection().normalize();
        for (int i = 0; i <= distance; i++) {
            Location newPosition = player.getLocation().add(direction);
            if (!newPosition.getBlock().isPassable()) break;
            player.teleport(newPosition);
        }

        // Play effect
        World w = player.getWorld();
        w.spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 1);
        w.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);

        // Actual damage
        for (Entity e : player.getNearbyEntities(range, range, range))
            if (e instanceof LivingEntity entity)
                entity.damage(magicDamage(player, damage), causeAndAttacker(cause, player, entity));

        player.getInventory().getItemInMainHand().damage(1, player);
    }
}
