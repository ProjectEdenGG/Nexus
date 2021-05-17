package me.pugabyte.nexus.features.commands.staff.admin;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Permission("command.block")
public class ShulkerSoundCommand extends CustomCommand {

	public ShulkerSoundCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void shulkerSound(Player player) {
		commandBlock();
		player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_AMBIENT, 10F, (float) RandomUtils.randomInt(0, 2));
	}
}
