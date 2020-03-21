package me.pugabyte.bncore.features.afk;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chatold.herochat.HerochatAPI;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class AFKListener implements Listener {

	public AFKListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onChat(ChannelChatEvent event) {
		AFKPlayer player = AFK.get(event.getSender().getPlayer());
		if (player.isAfk())
			player.notAfk();
		else
			player.update();

		if (event.getResult() != Chatter.Result.ALLOWED) return;

		if (event.getChannel().getName().toLowerCase().contains("convo")) {
			List<Chatter> recipients = HerochatAPI.getRecipients(event.getSender(), event.getChannel());
			if (recipients.size() == 1) {
				AFKPlayer to = AFK.get(recipients.get(0).getPlayer());
				if (to.isAfk()) {
					Tasks.wait(3, () -> {
						if (!(event.getSender().getPlayer().isOnline() && to.getPlayer().isOnline())) return;

						String message = "&e* " + to.getPlayer().getName() + " is AFK";
						if (to.getMessage() != null)
							message += ": &3" + to.getMessage();
						event.getSender().getPlayer().sendMessage(colorize(message));
					});
				}

				// TODO: Herochat doesn't throw an event if the sender cannot see the recipient
				// if (!Utils.canSee(event.getSender().getPlayer(), to.getPlayer()))
				//	to.getPlayer().sendMessage(colorize("&3&l[&bVanish PM&3&l] &eFrom &3" + event.getSender().getPlayer().getName() + " &b&l> &e" + event.getMessage()));
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
		});
	}

}
