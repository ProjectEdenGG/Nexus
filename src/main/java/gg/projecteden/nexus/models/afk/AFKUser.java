package gg.projecteden.nexus.models.afk;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.commands.PushCommand;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.afk.events.NowAFKEvent;
import gg.projecteden.nexus.models.back.Back;
import gg.projecteden.nexus.models.back.BackService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Data
@Entity(value = "afk_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class AFKUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private boolean afk;
	private String message;
	private LocalDateTime time;
	private Location location;
	private boolean forceAfk;

	private Map<AFKSetting, Boolean> settings = new ConcurrentHashMap<>();

	private void save() {
		new AFKUserService().save(this);
	}

	private static WorldGuardUtils worldGuardUtils;

	public boolean isLimbo() {
		if (!isOnline())
			return false;

		if (worldGuardUtils == null)
			worldGuardUtils = new WorldGuardUtils("server");

		if (!worldGuardUtils.isInRegion(getOnlinePlayer().getLocation(), "limbo"))
			return false;

		return true;
	}

	public void limbo() {
		if (!isOnline() || isLimbo())
			return;

		Nexus.log("[AFK] Sending " + getNickname() + " to limbo");

		final Player player = getOnlinePlayer();
		forceAfk = true;
		WarpType.STAFF.get("limbo").teleportAsync(player).thenRun(() -> {
			update();
			PushCommand.set(uuid, false);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 1, false, false, false));
			forceAfk = false;
			save();
		});

		sendMessage(AFK.PREFIX + "You have been sent to AFK limbo. Move around to exit");
	}

	private CompletableFuture<Boolean> teleport;

	public void unlimbo() {
		if (!isLimbo() || teleport != null)
			return;

		Nexus.log("[AFK] Returning " + getNickname() + " from limbo");

		final Player player = getOnlinePlayer();
		final BackService backService = new BackService();
		final Back back = backService.get(player);
		final Location location = back.getLocations().isEmpty() ? null : back.getLocations().remove(0);

		if (location == null) {
			Nexus.severe("[AFK] Back location for " + getNickname() + " is null");
			teleport = WarpType.NORMAL.get("spawn").teleportAsync(getOnlinePlayer(), TeleportCause.PLUGIN);
		} else {
			teleport = player.teleportAsync(location, TeleportCause.PLUGIN);
			backService.save(back);
		}

		teleport.thenRun(() -> {
			afk = false;
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			notAfk();
			teleport = null;
		});
	}

	@Getter
	@AllArgsConstructor
	public enum AFKSetting {
		MOB_TARGETING(
			false,
			"Disable mobs targeting you while you are AFK",
			"Must be AFK for longer than 4 minutes",
			value -> "&3Mobs " + (value ? "&awill" : "&cwill not") + " &3target you while you are AFK"),
		MOB_SPAWNING(
			false,
			"Disable mobs spawning near you while you are AFK. Helps with server lag and spawn rates for active players",
			"Must be AFK for longer than 4 minutes",
			value -> "&3Mobs " + (value ? "&awill" : "&cwill not") + " &3spawn near you while you are AFK"),
		BROADCASTS(
			true,
			"Hides your AFK broadcasts from other players",
			null,
			value -> "&3Your own AFK broadcasts are now " + (value ? "&ashown" : "&chidden") + " &3from other players"),
		;

		private final boolean defaultValue;
		private final String description;
		private final String descriptionExtra;
		private final Function<Boolean, String> message;
	}

	public boolean getSetting(AFKSetting setting) {
		return settings.getOrDefault(setting, setting.defaultValue);
	}

	public void setSetting(AFKSetting setting, boolean value) {
		if (value == setting.defaultValue)
			settings.remove(setting);
		else
			settings.put(setting, value);
	}

	public void setMessage(String message) {
		this.message = stripColor(message);
	}

	public void setTime() {
		this.time = LocalDateTime.now();
	}

	public boolean isNotAfk() {
		return !afk;
	}

	public boolean isTimeAfk() {
		return time != null && time.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 240;
	}

	public boolean isNotTimeAfk() {
		return !isTimeAfk();
	}

	public boolean hasBeenAfkFor(int ticks) {
		return time != null && time.isBefore(LocalDateTime.now().minusSeconds(ticks / 20));
	}

	public void setLocation() {
		Player player = getPlayer();
		if (player != null)
			this.location = player.getLocation().clone();
	}

	public void forceAfk(Runnable action) {
		setForceAfk(true);
		action.run();
		Tasks.wait(TickTime.SECOND.x(10), () -> {
			setLocation();
			setForceAfk(false);
		});
	}

	public void update() {
		setTime();
		setLocation();
	}

	public void afk() {
		setAfk(true);

		new NowAFKEvent(this).callEvent();

		message();
		broadcast();
	}

	public void notAfk() {
		if (isAfk())
			unlimbo();
		setAfk(false);
		setMessage(null);
		update();

		new NotAFKEvent(this).callEvent();

		message();
		broadcast();
	}

	public void message() {
		if (afk)
			sendMessage("&7* You are now AFK" + (message == null ? "" : ". Your auto-reply message is set to:\n &e" + message));
		else
			sendMessage("&7* You are no longer AFK");
	}

	private void broadcast() {
		if (!isOnline() || !getSetting(AFKSetting.BROADCASTS))
			return;

		Component broadcast = new JsonBuilder("&7* &e" + getNickname() + " &7is " + (afk ? "now" : "no longer") + " AFK").build();
		PlayerUtils.getOnlinePlayers().forEach(_player -> {
			final Player player = getPlayer();
			if (!PlayerUtils.canSee(_player, player))
				return;
			if (PlayerUtils.isSelf(_player, player))
				return;
			if (MuteMenuUser.hasMuted(_player, MuteMenuItem.AFK))
				return;

			_player.sendMessage(player, broadcast, MessageType.CHAT);
		});
	}

	public void reset() {
		afk = false;
		message = null;
		time = null;
		location = null;
		forceAfk = false;
	}

}
