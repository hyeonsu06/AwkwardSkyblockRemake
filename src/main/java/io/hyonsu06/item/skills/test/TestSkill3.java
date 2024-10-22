package io.hyonsu06.item.skills.test;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SkillTagged
@Skill(
        ID = "test_skill_3",
        name = "test_3",
        description = ""
)
public class TestSkill3 implements SkillMethods {
    @SkillTagged
    @Override
    public void onSneakRightClick(Player player) {
        player.sendMessage(ChatColor.GOLD + "Sneak Right Click skill activated!");
    }
}
