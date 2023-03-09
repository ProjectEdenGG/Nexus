package gg.projecteden.nexus.features.radar;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Disabled
@HideFromWiki
@Permission(Group.ADMIN)
public class AACNotifyCommand extends CustomCommand {

	public AACNotifyCommand(CommandEvent event) {
		super(event);
	}

	private final static Map<UUID, Integer> ingameCounts = new HashMap<>();
	private final static Map<UUID, Integer> discordCounts = new HashMap<>();
	private final static Map<UUID, Integer> totalCounts = new HashMap<>();

	@Path("<player> <message...>")
	void notify(Player player, String reason) {
		String name = player.getName();

		WorldGroup worldGroup = WorldGroup.of(player);
		int ping = player.getPing();
		double tps = Bukkit.getTPS()[1];

		if (ping < 300 && tps >= 15) {
			String message = "&a" + name + " &f" + reason
					.replace("{worldgroup}", camelCase(worldGroup))
					.replace("{ping}", String.valueOf(ping))
					.replace("{tps}", new DecimalFormat("0.00").format(tps));

			UUID uuid = player.getUniqueId();
			totalCounts.put(uuid, totalCounts.getOrDefault(uuid, 0) + 1);
			if (new CooldownService().check(player, "aac-notify-ingame", TickTime.SECOND.x(15))) {
				String ingame = message;
				if (ingameCounts.getOrDefault(uuid, 0) > 0)
					ingame += (" &c(" + ingameCounts.get(uuid) + " more...)");
				Broadcast.staffIngame().message("&7&l[&cRadar&7&l] " + ingame).send();
				ingameCounts.remove(uuid);
			} else
				ingameCounts.put(uuid, ingameCounts.getOrDefault(uuid, 0) + 1);

			if (new CooldownService().check(player, "aac-notify-discord", TickTime.MINUTE)) {
				String discord = message;
				if (discordCounts.getOrDefault(uuid, 0) > 0)
					discord += " (" + discordCounts.get(uuid) + " more...)";
				Broadcast.staffDiscord().prefix("Radar").message(discord).send();
				discordCounts.remove(uuid);
			} else
				discordCounts.put(uuid, discordCounts.getOrDefault(uuid, 0) + 1);

			if (Rank.getOnlineMods().stream().allMatch(nerd -> AFK.get(player.getPlayer()).isTimeAfk()))
				if (totalCounts.getOrDefault(uuid, 0) > 20)
					runCommandAsConsole("ban " + player.getName() + " 1d You have been automatically banned " +
							"by our anti cheat. Hacking is not allowed! (C: " + totalCounts.get(uuid) + ")");
		}
	}

}
