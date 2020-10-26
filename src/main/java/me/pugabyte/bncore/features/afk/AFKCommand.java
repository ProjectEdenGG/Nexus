package me.pugabyte.bncore.features.afk;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.chat.events.MinecraftChatEvent;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PrivateChannel;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Aliases("away")
@NoArgsConstructor
public class AFKCommand extends CustomCommand implements Listener {

	public AFKCommand(CommandEvent event) {
		super(event);
	}

	@Path("[autoreply...]")
	void afk(String autoreply) {
		AFKPlayer player = AFK.get(player());

		if (!isNullOrEmpty(autoreply))
			player.setMessage(autoreply);

		if (player.isAfk())
			if (isNullOrEmpty(autoreply))
				player.notAfk();
			else
				player.forceAfk(player::message);
		else
			player.forceAfk(player::afk);
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		AFKPlayer player = AFK.get(event.getChatter().getPlayer());
		if (player.isAfk())
			player.notAfk();
		else
			player.update();

		if (event.getChannel() instanceof PrivateChannel) {
			for (Chatter recipient : event.getRecipients()) {
				if (!recipient.getOfflinePlayer().isOnline()) continue;
				if (!Utils.canSee(player.getPlayer(), recipient.getPlayer())) return;
				AFKPlayer to = AFK.get(recipient.getPlayer());
				if (AFK.get(to.getPlayer()).isAfk()) {
					Tasks.wait(3, () -> {
						if (!(event.getChatter().getPlayer().isOnline() && to.getPlayer().isOnline())) return;

						String message = "&e* " + to.getPlayer().getName() + " is AFK";
						if (to.getMessage() != null)
							message += ": &3" + to.getMessage();
						send(event.getChatter().getPlayer(), message);
					});
				}
			}
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Tasks.wait(3, () -> {
			if (!event.getPlayer().isOnline()) return;

			AFKPlayer player = AFK.get(event.getPlayer());
			if (player.isAfk() && !player.isForceAfk())
				player.notAfk();
			else
				player.update();
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		AFK.remove(event.getPlayer());
	}

}
