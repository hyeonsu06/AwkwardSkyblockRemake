package io.hyonsu06.item.skills;

import io.hyonsu06.core.managers.StatManager;
import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.enums.Stats;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import static io.hyonsu06.core.functions.getAnnotationArguments.getSkillAnnotation;
import static io.hyonsu06.core.functions.getPluginNameSpacedKey.getPDC;

@SkillTagged
@Skill(
        ID = "instant_shoot",
        name = "instant_shoot",
        description = "Shortbow: Instantly Shoots {0} arrows!",
        args = {
                3,
                2
        },
        simpleDescription = true
)
public class InstantShoot implements SkillMethods {
    @SkillTagged
    @Override
    public void onLeftClick(Player player) {
        int arrows = countArrowsInInventory(player);
        long[] args = getSkillAnnotation(getClass()).args();
        int cooldown = player.getCooldown(Material.BOW);
        double attackSpeed = 20 - (15 * (StatManager.getFinalStatMap().get(player.getUniqueId()).get(Stats.ATTACKSPEED) / 100));
        if (cooldown == 0) {
            if (arrows >= args[0]) {
                fireArrows(player, args[0], args[1]);
                player.getInventory().getItemInMainHand().damage(1, player);
                player.getInventory().removeItem(Material.ARROW.asItemType().createItemStack((int) args[0]));
                if (attackSpeed < 0) attackSpeed = 0;
                player.setCooldown(Material.BOW, (int) attackSpeed);
            }
        }
    }

    private int countArrowsInInventory(Player player) {
        int count = 0;

        // Loop through each item in the player's inventory
        for (ItemStack item : player.getInventory().getContents()) {
            // Check if the item is not null and matches the specified material
            if (item != null && item.getType() == Material.ARROW && item.getItemMeta().getPersistentDataContainer().get(getPDC("id"), PersistentDataType.STRING) == null) {
                count += item.getAmount(); // Add the amount of this item to the count
            }
        }
        return count; // Return the total count of the specified item
    }

    private void fireArrows(Player player, long numberOfArrows, double speed) {// Get the player's location
        Location playerLocation = player.getLocation().add(0, 1.5, 0);

        // Calculate the direction the player is looking
        Vector baseDirection = playerLocation.getDirection().normalize();

        // Calculate the total angle spread in radians
        double totalSpread = Math.toRadians(10); // Convert degrees to radians
        double halfSpread = totalSpread / 2;

        // Calculate the angle increment based on the number of arrows
        double angleIncrement = totalSpread / (numberOfArrows - 1); // Spread for each arrow

        for (int i = 0; i < numberOfArrows; i++) {
            // Calculate the angle for the current arrow
            double angle = halfSpread - (angleIncrement * i);

            // Create a new direction vector by rotating the base direction
            Vector offsetDirection = rotateVector(baseDirection, angle).normalize(); // Normalize to keep consistent speed

            // Create a new arrow at the player's location
            Arrow arrow = playerLocation.getWorld().spawn(playerLocation.add(baseDirection.multiply(1)), Arrow.class);

            // Set the arrow's velocity in the new direction with consistent speed
            arrow.setVelocity(offsetDirection.multiply(speed)); // Set the arrow's speed
            arrow.setShooter(player);

            // Optionally, you can set the arrow to be fire or critical
            // arrow.setFireTicks(100); // Uncomment to set the arrow on fire
            // arrow.setCritical(true); // Uncomment for critical hit

            // Optional: Adjust the spawn position for each arrow to avoid overlap
            playerLocation.add(baseDirection.multiply(0.5)); // Slightly offset the spawn location for each arrow
        }
    }

    private Vector rotateVector(Vector vector, double angle) {
        double x = vector.getX();
        double z = vector.getZ();

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Rotate the vector
        return new Vector((x * cos) - (z * sin), vector.getY(), (x * sin) + (z * cos));
    }
}
