package io.hyonsu06.item.skills.test;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SkillTagged
@Skill(
        ID = "test_skill_5",
        description = ""
)
public class TestSkill5 implements SkillMethods {
    @SkillTagged
    @Override
    public void onSneak(Player player) {
        player.sendMessage(ChatColor.GOLD + "Sneak skill activated!");
    }
}
