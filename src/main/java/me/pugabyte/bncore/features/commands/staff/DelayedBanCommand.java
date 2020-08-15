package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.delayedban.DelayedBan;
import me.pugabyte.bncore.models.delayedban.DelayedBanService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@Aliases({"delayedbans"})
@Permission("group.staff")
@NoArgsConstructor
public class DelayedBanCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("DelayedBan");
	DelayedBanService service = new DelayedBanService();
	DelayedBan delayedBan;

	public DelayedBanCommand(CommandEvent event) {
		super(event);
	}

	@Path("list")
	public void listBans() {
		if (service.getAll().size() == 0)
			error("There are no delayed bans queued");

		send(PREFIX + "Queued delayed bans:");
		for (Object o : service.getAll()) {
			delayedBan = (DelayedBan) o;
			String playerName = delayedBan.getOfflinePlayer().getName();
			String staffName = Utils.getPlayer(delayedBan.getUuid_staff()).getName();
			String reason = delayedBan.getReason();
			String duration = delayedBan.getDuration();
			send(" &8- &e" + playerName + "&3: banned by &e" + staffName + " &3for &e" + reason + " &3for &e" + duration);
		}
	}

	@Path("remove <player>")
	public void removeBan(OfflinePlayer player) {
		delayedBan = service.get(player);
		if (!service.hasQueuedBan(player))
			error(player.getName() + " doesn't have a delayed ban queued");

		service.deleteSync(delayedBan);
		send(PREFIX + player.getName() + "'s delayed ban removed");
	}

	@Path("clearDatabase")
	@Permission("group.admin")
	public void clearDatabase() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
		send(PREFIX + "Database cleared");
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		if (!service.hasQueuedBan(event.getPlayer()))
			return;

		Tasks.wait(10, () -> { // Necessary wait
			delayedBan = service.get(event.getPlayer());
			String playerName = delayedBan.getOfflinePlayer().getName();
			String reason = delayedBan.getReason();
			String duration = delayedBan.getDuration();

			Utils.runCommandAsConsole("ban " + playerName + " " + duration + " " + reason);
			service.deleteSync(delayedBan);
		});
	}
}
