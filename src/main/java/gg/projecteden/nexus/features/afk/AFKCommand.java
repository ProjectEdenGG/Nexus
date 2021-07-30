package gg.projecteden.nexus.features.afk;

import gg.projecteden.nexus.features.chat.events.MinecraftChatEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown.Part;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DescriptionExtra;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.afk.AFKUser;
import gg.projecteden.nexus.models.afk.AFKUser.AFKSetting;
import gg.projecteden.nexus.models.afk.AFKUserService;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PrivateChannel;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Aliases("away")
@NoArgsConstructor
public class AFKCommand extends CustomCommand implements Listener {
	private static final AFKUserService service = new AFKUserService();

	public AFKCommand(CommandEvent event) {
		super(event);
	}

	@Path("[autoreply...]")
	@Cooldown(@Part(value = Time.SECOND, x = 5))
	void afk(String autoreply) {
		AFKUser user = AFK.get(player());

		if (!isNullOrEmpty(autoreply))
			user.setMessage(autoreply);

		if (user.isAfk())
			if (isNullOrEmpty(autoreply))
				user.notAfk();
			else
				user.forceAfk(user::message);
		else
			user.forceAfk(user::afk);
	}

	@Path("settings mobTargeting [enable]")
	@Description("Disable mobs targeting you while you are AFK")
	@DescriptionExtra("Must be AFK for longer than 4 minutes")
	void mobTargeting(Boolean enable) {
		AFKUser user = service.get(player());
		if (enable == null)
			enable = !user.getSetting(AFKSetting.MOB_TARGETING);

		user.setSetting(AFKSetting.MOB_TARGETING, enable);
		service.save(user);
		send(PREFIX + "Mobs " + (enable ? "&awill" : "&cwill not") + " &3target you while you are AFK");
	}

	@Path("settings mobSpawning [enable]")
	@Description("Disable mobs spawning near you while you are AFK. Helps with server lag and spawn rates for active players")
	@DescriptionExtra("Must be AFK for longer than 4 minutes")
	void mobSpawning(Boolean enable) {
		error("Temporarily disabled");

		AFKUser user = service.get(player());
		if (enable == null)
			enable = !user.getSetting(AFKSetting.MOB_SPAWNING);

		user.setSetting(AFKSetting.MOB_SPAWNING, enable);
		service.save(user);
		send(PREFIX + "Mobs " + (enable ? "&awill" : "&cwill not") + " &3spawn near you while you are AFK");
	}

	@Path("settings broadcasts [enable]")
	void hideBroadcasts(Boolean enable) {
		AFKUser user = service.get(player());
		if (enable == null)
			enable = !user.getSetting(AFKSetting.BROADCASTS);

		user.setSetting(AFKSetting.BROADCASTS, enable);
		service.save(user);
		send(PREFIX + "Your own AFK broadcasts are now " + (enable ? "&ashown" : "&chidden") + " &3from other players");
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		AFKUser user = AFK.get(event.getChatter().getOnlinePlayer());
		if (user.isAfk())
			user.notAfk();
		else
			user.update();

		if (event.getChannel() instanceof PrivateChannel) {
			for (Chatter recipient : event.getRecipients()) {
				if (!recipient.getOfflinePlayer().isOnline()) continue;
				if (!PlayerUtils.canSee(user.getPlayer(), recipient.getOnlinePlayer())) return;
				AFKUser to = AFK.get(recipient.getOnlinePlayer());
				if (to.isAfk()) {
					Tasks.wait(3, () -> {
						if (!(event.getChatter().getOnlinePlayer().isOnline() && to.getOnlinePlayer().isOnline())) return;

						String message = "&e* " + Nickname.of(to.getOnlinePlayer()) + " is AFK";
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

			AFKUser user = AFK.get(event.getPlayer());
			if (user.isAfk() && !user.isForceAfk())
				user.notAfk();
			else
				user.update();
		});
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		service.edit(event.getUniqueId(), AFKUser::reset);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		service.edit(event.getPlayer(), AFKUser::reset);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityTarget(final EntityTargetLivingEntityEvent event) {
		if (event.getEntity().getType() == EntityType.EXPERIENCE_ORB)
			return;

		if (event.getTarget() instanceof Player player) {
			if (AFK.get(player).isTimeAfk()) {
				AFKUser user = service.get(player);
				if (!user.getSetting(AFKSetting.MOB_TARGETING))
					event.setCancelled(true);
			}
		}
	}

	/*

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntitySpawn(final CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.NATURAL)
			return;

		Entity entity = event.getEntity();
		if (!isHostile(entity))
			return;

		if (isActivatedEntity(entity))
			return;

		event.setCancelled(true);
	}

	static {
		Tasks.repeat(Time.MINUTE.x(5), Time.MINUTE, () -> {
			for (World world : WorldGroup.SURVIVAL.getWorlds()) {
				if (world.getEnvironment() != Environment.NORMAL)
					continue;

				for (Entity entity : world.getEntities()) {
					if (!isHostile(entity))
						continue;

					if (isActivatedEntity(entity))
						continue;

					entity.remove();
				}
			}
		});
	}

	private static boolean isActivatedEntity(Entity entity) {
		int mobSpawnRange = (getMobSpawnRange(entity.getLocation().getWorld()) + 1) * 16;

		if (!StringUtils.isNullOrEmpty(entity.getCustomName()))
			return true;

		if (entity.getVehicle() != null)
			return true;

		Collection<Player> players = entity.getLocation().getNearbyPlayers(mobSpawnRange, 999, mobSpawnRange);

		for (Player player : players)
			if (AFK.get(player).isNotTimeAfk())
				return true;

		for (Player player : players)
			if (new AFKSettingsService().get(player).isMobSpawning())
				return true;

		return false;
	}

	*/

}
