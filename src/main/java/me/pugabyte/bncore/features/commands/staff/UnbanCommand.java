package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.delayedban.DelayedBan;
import me.pugabyte.bncore.models.delayedban.DelayedBanService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@Fallback("litebans")
@Permission("group.moderator")
//@Redirect(from = {"/ipunban", "/unbanip", "/tempunban", "/tmpban", "/ltempban",
//		"/lunban", "/pardon", "/pardonip", "/pardon-ip"}, to = "/bncore:unban")
public class UnbanCommand extends CustomCommand {

	public UnbanCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void run() {
		fallback();
	}

	@Path("<player>")
	public void unban(@Arg(tabCompleter = OfflinePlayer.class) String playerName) {
		OfflinePlayer player = Bukkit.getPlayer(playerName);
		if (player != null && player.hasPlayedBefore()) {
			DelayedBanService delayedBanService = new DelayedBanService();
			if (delayedBanService.hasQueuedBan(player)) {
				DelayedBan delayedBan = delayedBanService.get(player.getUniqueId());
				delayedBanService.deleteSync(delayedBan);
				send(DelayedBanCommand.PREFIX + player.getName() + "'s delayed ban removed");
			}
		}

		fallback();
	}

}
