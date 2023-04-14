package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@HideFromWiki
@Permission(Group.ADMIN)
public class ShulkerSoundCommand extends CustomCommand {

	public ShulkerSoundCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Player the shulker sound to a player")
	void shulkerSound(Player player) {
		commandBlock();
		player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_AMBIENT, 10F, (float) RandomUtils.randomInt(0, 2));
	}
}
