package me.pugabyte.nexus.features.afk;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.events.MinecraftChatEvent;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.afk.AFKPlayer;
import me.pugabyte.nexus.models.afk.AFKSettings;
import me.pugabyte.nexus.models.afk.AFKSettingsService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PrivateChannel;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Aliases("away")
@NoArgsConstructor
public class AFKCommand extends CustomCommand implements Listener {

	public AFKCommand(CommandEvent event) {
		super(event);
	}

	@Path("[autoreply...]")
	@Cooldown(@Part(value = Time.SECOND, x = 5))
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

	@Path("settings mobTargeting [enable]")
	void mobTargeting(Boolean enable) {
		AFKSettingsService service = new AFKSettingsService();
		AFKSettings afkSettings = service.get(player());
		if (enable == null)
			enable = !afkSettings.isMobTargeting();

		afkSettings.setMobTargeting(enable);
		service.save(afkSettings);
		send(PREFIX + "Mobs " + (enable ? "&awill" : "&cwill not") + " &3target you while you are AFK");
	}

	@Path("settings broadcasts [enable]")
	void hideBroadcasts(Boolean enable) {
		AFKSettingsService service = new AFKSettingsService();
		AFKSettings afkSettings = service.get(player());
		if (enable == null)
			enable = !afkSettings.isBroadcasts();

		afkSettings.setBroadcasts(enable);
		service.save(afkSettings);
		send(PREFIX + "Your own AFK broadcasts are now " + (enable ? "&ashown" : "&chidden") + " &3from other players");
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		AFKPlayer player = AFK.get(event.getChatter().getOnlinePlayer());
		if (player.isAfk())
			player.notAfk();
		else
			player.update();

		if (event.getChannel() instanceof PrivateChannel) {
			for (Chatter recipient : event.getRecipients()) {
				if (!recipient.getOfflinePlayer().isOnline()) continue;
				if (!PlayerUtils.canSee(player.getPlayer(), recipient.getOnlinePlayer())) return;
				AFKPlayer to = AFK.get(recipient.getOnlinePlayer());
				if (AFK.get(to.getPlayer()).isAfk()) {
					Tasks.wait(3, () -> {
						if (!(event.getChatter().getOnlinePlayer().isOnline() && to.getPlayer().isOnline())) return;

						String message = "&e* " + Nickname.of(to.getPlayer()) + " is AFK";
						if (to.getMessage() != null)
							message += ": &3" + to.getMessage();
						send(event.getChatter(), message);
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

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityTarget(final EntityTargetLivingEntityEvent event) {
		if (event.getEntity().getType() == EntityType.EXPERIENCE_ORB)
			return;

		if (event.getTarget() instanceof Player) {
			Player player = (Player) event.getTarget();
			if (AFK.get(player).isTimeAfk()) {
				AFKSettings afkSettings = new AFKSettingsService().get(player);
				if (!afkSettings.isMobTargeting())
					event.setCancelled(true);
			}
		}
	}

}
