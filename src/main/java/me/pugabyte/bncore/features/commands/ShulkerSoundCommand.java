package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils.RandomUtils;
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
