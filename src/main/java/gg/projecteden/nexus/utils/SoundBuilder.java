package gg.projecteden.nexus.utils;

import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.parchment.OptionalPlayer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SoundBuilder implements Cloneable {
	private String sound;
	private List<Player> receivers = new ArrayList<>();
	private Location location;
	private SoundCategory category = SoundCategory.MASTER;
	private MuteMenuItem muteMenuItem;
	private Function<Player, Float> volume = player -> 1F;
	private float pitch = 1F;
	private int delay = 0;

	private boolean singleton;
	private String context;
	private LocalDateTime expiration;

	public static final List<SoundCooldown<?>> COOLDOWNS = new ArrayList<>();

	// milliseconds
	public static final Map<String, Integer> SOUND_DURATIONS = new HashMap<>();

	@Data
	public static abstract class SoundCooldown<T> {
		private String context;
		private LocalDateTime expiration;

		public T context(String context) {
			this.context = context;
			return (T) this;
		}

		public T expiration(LocalDateTime expiration) {
			this.expiration = expiration;
			return (T) this;
		}

		public boolean isExpired() {
			return expiration.isBefore(LocalDateTime.now());
		}

		public void create() {
			COOLDOWNS.add(this);
		}

	}

	@Data
	@ToString(callSuper = true)
	public static class PlayerSoundCooldown extends SoundCooldown<PlayerSoundCooldown> {
		private UUID uuid;

		public PlayerSoundCooldown player(HasUniqueId player) {
			this.uuid = player.getUniqueId();
			return this;
		}

	}

	@Data
	@ToString(callSuper = true)
	public static class LocationSoundCooldown extends SoundCooldown<LocationSoundCooldown> {
		private Location location;

		public LocationSoundCooldown location(Location location) {
			this.location = location;
			return this;
		}
	}

	public SoundBuilder(Sound sound) {
		this.sound = sound.key().asString();
	}

	// use CustomSound when possible
	public SoundBuilder(String sound) {
		this.sound = sound;
	}

	public SoundBuilder(CustomSound sound) {
		this.sound = sound.getPath();
	}

	public SoundBuilder sound(Sound sound) {
		this.sound = sound.key().asString();
		return this;
	}

	public SoundBuilder everyone() {
		return receivers(OnlinePlayers.getAll().stream().map(Player::getPlayer).collect(Collectors.toList()));
	}

	public SoundBuilder receiver(@Nullable OptionalPlayer receiver) {
		if (receiver != null && receiver.getPlayer() != null)
			receivers(Collections.singletonList(receiver.getPlayer()));
		return this;
	}

	public SoundBuilder receivers(List<Player> receivers) {
		this.receivers.addAll(receivers);
		return this;
	}

	public SoundBuilder location(Location location) {
		this.location = location;
		return this;
	}

	public SoundBuilder location(Block block) {
		this.location = block.getLocation();
		return this;
	}

	public SoundBuilder location(Entity entity) {
		this.location = entity.getLocation();
		return this;
	}

	public SoundBuilder category(SoundCategory category) {
		this.category = category;
		return this;
	}

	public SoundBuilder muteMenuItem(MuteMenuItem muteMenuItem) {
		this.muteMenuItem = muteMenuItem;
		if (muteMenuItem != null && muteMenuItem.getDefaultVolume() != null)
			volume(muteMenuItem);
		return this;
	}

	public SoundBuilder volume(double volume) {
		return volume((float) volume);
	}

	public SoundBuilder volume(float volume) {
		this.volume = player -> Math.max(volume, 0.0F);
		return this;
	}

	public SoundBuilder volume(MuteMenuItem muteMenuItem) {
		return volume(player -> SoundUtils.getMuteMenuVolume(player, muteMenuItem));
	}

	public SoundBuilder volume(Function<Player, Float> volume) {
		this.volume = volume;
		return this;
	}

	public SoundBuilder pitch(double pitch) {
		return pitch((float) pitch);
	}

	public SoundBuilder pitchStep(int step) {
		return pitch(SoundUtils.getPitch(step));
	}

	public SoundBuilder pitch(float pitch) {
		this.pitch = MathUtils.clamp(pitch, 0.1F, 2.0F);
		return this;
	}

	public SoundBuilder delay(int delay) {
		this.delay = delay;
		return this;
	}

	public SoundBuilder singleton() {
		return singleton(true);
	}

	public SoundBuilder singleton(boolean singleton) {
		this.singleton = singleton;
		return this;
	}

	public SoundBuilder singleton(String context) {
		this.singleton = true;
		this.context = context;
		return this;
	}

	public SoundBuilder context(String context) {
		this.context = context;
		return this;
	}

	public SoundBuilder expiration(LocalDateTime expiration) {
		this.expiration = expiration;
		return this;
	}

	public SoundBuilder clone() {
		return new SoundBuilder(sound)
			.receivers(new ArrayList<>(receivers))
			.location(location == null ? null : location.clone())
			.category(category)
			.muteMenuItem(muteMenuItem)
			.pitch(pitch)
			.volume(volume)
			.delay(delay)
			.singleton(singleton)
			.context(context)
			.expiration(expiration);
	}

	public void play() {
		if (sound == null)
			throw new InvalidInputException("SoundBuilder: Sound cannot be null!");

		if (!sound.contains(":"))
			sound = "minecraft:" + sound;

		if (Nullables.isNullOrEmpty(receivers) && location != null)
			world();
		else
			players();
	}

	private void world() {
		Tasks.wait(delay, () -> {
			if (singleton) {
				for (LocationSoundCooldown cooldown : cooldowns(LocationSoundCooldown.class, context)) {
					if (!cooldown.getLocation().toBlockLocation().equals(location.toBlockLocation()))
						continue;

					return;
				}

				LocalDateTime expiration = expiration();
				if (expiration != null)
					new LocationSoundCooldown()
						.location(location)
						.context(context)
						.expiration(expiration)
						.create();
			}

			location.getWorld().playSound(location, sound, category, volume.apply(null), pitch);
		});
	}

	private void players() {
		for (Player player : receivers) {
			if (player == null || !player.isOnline())
				continue;

			player(player);
		}
	}

	private void player(Player player) {
		if (location != null && player.getWorld() != location.getWorld())
			return;

		if (muteMenuItem != null)
			if (MuteMenuUser.hasMuted(player, muteMenuItem))
				return;

		Tasks.wait(delay, () -> {
			if (singleton) {
				for (PlayerSoundCooldown cooldown : cooldowns(PlayerSoundCooldown.class, context)) {
					if (!cooldown.getUuid().equals(player.getUniqueId()))
						continue;

					return;
				}

				LocalDateTime expiration = expiration();
				if (expiration != null)
					new PlayerSoundCooldown()
						.player(player)
						.context(context)
						.expiration(expiration)
						.create();
			}

			if (!player.isOnline())
				return;

			Location origin = location == null ? player.getLocation() : location;
			player.playSound(origin, sound, category, volume.apply(player), pitch);
		});
	}

	private LocalDateTime expiration() {
		if (expiration != null)
			return expiration;

		if (!SOUND_DURATIONS.containsKey(sound))
			return null;

		final int duration = SOUND_DURATIONS.get(sound);
		return LocalDateTime.now().plus((long) (duration + (duration * (1 - pitch) * .95)), ChronoUnit.MILLIS);
	}

	public static <T extends SoundCooldown<?>> List<T> cooldowns(Class<T> clazz, String context) {
		COOLDOWNS.removeIf(SoundCooldown::isExpired);
		return COOLDOWNS.stream()
			.filter(cooldown -> Objects.equals(cooldown.getContext(), context))
			.filter(cooldown -> clazz.isAssignableFrom(cooldown.getClass()))
			.map(cooldown -> (T) cooldown)
			.toList();
	}

}
