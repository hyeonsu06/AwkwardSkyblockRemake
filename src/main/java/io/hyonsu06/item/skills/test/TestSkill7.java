package io.hyonsu06.item.skills.test;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@SkillTagged
@Skill(
        ID = "test_skill_7",
        name = "test_7",
        description = ""
)
public class TestSkill7 implements SkillMethods {
    @SkillTagged
    @Override
    public double onHit(Player player, Entity target, double damage) {
        player.sendMessage(ChatColor.GOLD + "Hit skill activated!");
        return -1;
    }
}
