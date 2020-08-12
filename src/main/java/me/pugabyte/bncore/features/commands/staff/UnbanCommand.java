package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.delayedban.DelayedBan;
import me.pugabyte.bncore.models.delayedban.DelayedBanService;
import org.bukkit.OfflinePlayer;

@Fallback("litebans")
public class UnbanCommand extends CustomCommand {

	public UnbanCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void run() {
		fallback();
	}

	@Path("<player>")
	public void unban(OfflinePlayer player) {
		DelayedBanService delayedBanService = new DelayedBanService();
		if (delayedBanService.hasQueuedBan(player)) {
			DelayedBan delayedBan = delayedBanService.get(player.getUniqueId());
			delayedBanService.deleteSync(delayedBan);
			send(DelayedBanCommand.PREFIX + player.getName() + "'s delayed ban removed");
		}

		fallback();
	}

}
