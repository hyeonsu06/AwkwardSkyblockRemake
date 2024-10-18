package io.hyonsu06.item.skills.test;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SkillTagged
@Skill(
        ID = "test_skill_4",
        description = ""
)
public class TestSkill4 implements SkillMethods {
    @SkillTagged
    @Override
    public void onSneakLeftClick(Player player) {
        player.sendMessage(ChatColor.GOLD + "Sneak Left Click skill activated!");
    }
}
