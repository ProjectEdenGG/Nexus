package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@HideFromWiki
@Permission("command.block")
public class ShulkerSoundCommand extends CustomCommand {

	public ShulkerSoundCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void shulkerSound() {
		commandBlock();
		world().playSound(location(), Sound.ENTITY_SHULKER_AMBIENT, 10F, (float) RandomUtils.randomDouble(0, 2));
	}

	@Path("<player>")
	void shulkerSound(Player player) {
		commandBlock();
		player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_AMBIENT, 10F, (float) RandomUtils.randomInt(0, 2));
	}
}
