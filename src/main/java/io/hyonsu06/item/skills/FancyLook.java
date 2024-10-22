package io.hyonsu06.item.skills;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.hyonsu06.core.functions.checkAndSpendMana.isQualified;
import static io.hyonsu06.core.functions.getAnnotationArguments.getSkillAnnotation;

@SkillTagged
@Skill(
        ID = "fancy_look",
        name = "fancy_look",
        description = "You look so fancy, i guess?",
        simpleDescription = true
)
public class FancyLook implements SkillMethods {
    @SkillTagged
    @Override
    public void passive(Player player) {
        Map<UUID, Double> angle = new HashMap<>(); // Initial angle
        final double radius = 2.0; // Distance from the player
        final double speed = 0.1; // Speed of rotation
        Skill skill = getSkillAnnotation(getClass());
        if (isQualified(player, skill.cost())) {
            angle.putIfAbsent(player.getUniqueId(), 0d);

            double x = radius * Math.cos(angle.get(player.getUniqueId()));
            double z = radius * Math.sin(angle.get(player.getUniqueId()));
            Location particleLocation = player.getLocation().add(x, 1, z); // Add 1 to the Y-axis for height

            // Spawn the particle
            player.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1);

            // Increment the angle for the next position
            angle.put(player.getUniqueId(), angle.get(player.getUniqueId()) + speed);

            // Reset the angle to prevent overflow
            if (angle.get(player.getUniqueId()) >= Math.PI * 2) {
                angle.put(player.getUniqueId(), 0d);
            }

        }
    }
}
