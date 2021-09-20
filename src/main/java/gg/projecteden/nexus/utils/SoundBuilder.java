package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.lexikiq.HasPlayer;
import me.lexikiq.HasUniqueId;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SoundBuilder implements Cloneable {
	private String sound;
	private List<HasPlayer> receivers = new ArrayList<>();
	private Location location;
	private SoundCategory category = SoundCategory.MASTER;
	private MuteMenuItem muteMenuItem;
	private Function<HasPlayer, Float> volume = player -> 1F;
	private Function<HasPlayer, Float> pitch = player -> 1F;
	private int delay = 0;

	private boolean singleton;
	private String cooldownContext;
	private LocalDateTime cooldownExpiration;

	public static final List<SoundCooldown<?>> COOLDOWNS = new ArrayList<>();

	// milliseconds
	public static final Map<String, Integer> SOUND_DURATIONS = new HashMap<>();

	@Data
	public static abstract class SoundCooldown<T> {
		private String sound;
		private String context;
		private LocalDateTime expiration;

		public T sound(Sound sound) {
			this.sound = sound.key().asString();
			return (T) this;
		}

		public T sound(String sound) {
			this.sound = sound;
			return (T) this;
		}

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

	public SoundBuilder(String sound) {
		this.sound = sound;
	}

	public SoundBuilder sound(Sound sound) {
		this.sound = sound.key().asString();
		return this;
	}

	public SoundBuilder everyone() {
		return receivers(OnlinePlayers.getAll().stream().map(Player::getPlayer).collect(Collectors.toList()));
	}

	public SoundBuilder receiver(HasPlayer receiver) {
		return receivers(Collections.singletonList(receiver));
	}

	public SoundBuilder receivers(List<HasPlayer> receivers) {
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

	public SoundBuilder volume(Function<HasPlayer, Float> volume) {
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
		this.pitch = player -> MathUtils.clamp(pitch, 0.1F, 2.0F);
		return this;
	}

	public SoundBuilder pitch(Function<HasPlayer, Float> pitch) {
		this.pitch = pitch;
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

	public SoundBuilder cooldownContext(String context) {
		this.cooldownContext = context;
		return this;
	}

	public SoundBuilder cooldownExpiration(LocalDateTime expiration) {
		this.cooldownExpiration = expiration;
		return this;
	}

	public SoundBuilder clone() {
		return new SoundBuilder(sound)
			.receivers(new ArrayList<>(receivers))
			.location(location.clone())
			.category(category)
			.muteMenuItem(muteMenuItem)
			.pitch(pitch)
			.volume(volume)
			.delay(delay)
			.cooldownContext(cooldownContext)
			.cooldownExpiration(cooldownExpiration);
	}

	public void play() {
		if (sound == null)
			throw new InvalidInputException("SoundBuilder: Sound cannot be null!");

		if (!sound.contains(":"))
			sound = "minecraft:" + sound;

		System.out.println(this);

		if (Utils.isNullOrEmpty(receivers) && location != null)
			// play sound in world
			Tasks.wait(delay, () -> {
				if (singleton) {
					for (SoundCooldown<?> soundCooldown : cooldowns()) {
						if (!(soundCooldown instanceof LocationSoundCooldown cooldown))
							continue;

						if (!cooldown.getLocation().toBlockLocation().equals(location))
							continue;

						if (cooldown.getContext() == null) {
							if (cooldownContext != null)
								continue;
						} else
						if (!cooldown.getContext().equals(cooldownContext))
							continue;

						return;
					}

					LocalDateTime expiration = expiration();
					if (expiration != null)
						new LocationSoundCooldown()
							.sound(sound)
							.location(location)
							.context(cooldownContext)
							.expiration(expiration)
							.create();
				}

				location.getWorld().playSound(location, sound, category, volume.apply(null), pitch.apply(null))
			});
		else {
			// Play sound to receivers
			for (HasPlayer hasPlayer : receivers) {
				Player player = hasPlayer.getPlayer();
				if (!player.isOnline())
					continue;

				if (location != null && player.getWorld() != location.getWorld())
					continue;

				if (muteMenuItem != null)
					if (MuteMenuUser.hasMuted(player, muteMenuItem))
						continue;

				Tasks.wait(delay, () -> {
					if (singleton) {
						for (SoundCooldown<?> soundCooldown : cooldowns()) {
							if (!(soundCooldown instanceof PlayerSoundCooldown cooldown))
								continue;

							if (!cooldown.getUuid().equals(receiver.getPlayer().getUniqueId()))
								continue;

							if (cooldown.getContext() == null) {
								if (cooldownContext != null)
									continue;
							} else if (!cooldown.getContext().equals(cooldownContext))
								continue;

							return;
						}

						LocalDateTime expiration = expiration();
						if (expiration != null)
							new PlayerSoundCooldown()
								.sound(sound)
								.player(player)
								.context(cooldownContext)
								.expiration(expiration)
								.create();
					}

					if (!player.isOnline())
						return;

					Location origin = location == null ? player.getLocation() : location;
					player.playSound(origin, sound, category, volume.apply(player), pitch.apply(player));
				});
			}
		}
	}

	private LocalDateTime expiration() {
		if (cooldownExpiration != null)
			return cooldownExpiration;

		if (!SOUND_DURATIONS.containsKey(sound))
			return null;

		return LocalDateTime.now().plus((long) (SOUND_DURATIONS.get(sound) * pitch), ChronoUnit.MILLIS);
	}

	public static List<SoundCooldown<?>> cooldowns() {
		COOLDOWNS.removeIf(SoundCooldown::isExpired);
		return COOLDOWNS;
	}

}
