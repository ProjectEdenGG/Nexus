package gg.projecteden.nexus.framework.exceptions.postconfigured;

import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.utils.TimeUtils.Timespan;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommandCooldownException extends PostConfiguredException {

	public CommandCooldownException(OfflinePlayer player, String type) {
		this(player.getUniqueId(), type);
	}

	public CommandCooldownException(UUID uuid, String type) {
		super("You can run this command again in &e" + new CooldownService().getDiff(uuid, type));
	}

	public CommandCooldownException(LocalDateTime when) {
		super("You can run this command again in &e" + Timespan.of(when).format());
	}

}
