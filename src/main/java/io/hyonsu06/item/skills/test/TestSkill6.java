package io.hyonsu06.item.skills.test;

import io.hyonsu06.core.annotations.skills.Skill;
import io.hyonsu06.core.annotations.tags.SkillTagged;
import io.hyonsu06.core.interfaces.SkillMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SkillTagged
@Skill(
        ID = "test_skill_6",
        description = ""
)
public class TestSkill6 implements SkillMethods {
    @SkillTagged
    @Override
    public void onJump(Player player) {
        player.sendMessage(ChatColor.GOLD + "Jump skill activated!");
    }
}
