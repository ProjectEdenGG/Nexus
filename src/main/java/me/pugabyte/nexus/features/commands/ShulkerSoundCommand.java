package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ShulkerSoundCommand extends CustomCommand {

	public ShulkerSoundCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void shulkerSound(Player player) {
		commandBlock();
		float pitch = RandomUtils.randomInt(0, 2);
		player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_AMBIENT, 10F, pitch);
	}
}
