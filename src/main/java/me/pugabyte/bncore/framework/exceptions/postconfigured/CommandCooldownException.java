package me.pugabyte.bncore.framework.exceptions.postconfigured;

import me.pugabyte.bncore.models.cooldown.CooldownService;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CommandCooldownException extends PostConfiguredException {

	public CommandCooldownException(OfflinePlayer player, String type) {
		this(player.getUniqueId(), type);
	}

	public CommandCooldownException(UUID uuid, String type) {
		super("You can run this command again in &e" + new CooldownService().getDiff(uuid, type));
	}

}
