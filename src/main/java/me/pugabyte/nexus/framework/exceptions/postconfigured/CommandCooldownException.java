package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.models.cooldown.CooldownService;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

public class CommandCooldownException extends PostConfiguredException {

	public CommandCooldownException(OfflinePlayer player, String type) {
		this(player.getUniqueId(), type);
	}

	public CommandCooldownException(UUID uuid, String type) {
		super("You can run this command again in &e" + new CooldownService().getDiff(uuid, type));
	}

	public CommandCooldownException(LocalDateTime when) {
		super("You can run this command again in &e" + timespanDiff(when));
	}

}
