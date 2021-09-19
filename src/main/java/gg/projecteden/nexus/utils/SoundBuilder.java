package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SoundBuilder implements Cloneable {
	private List<HasPlayer> receivers;
	private Location location;
	private String sound;
	private SoundCategory category = SoundCategory.MASTER;
	private float volume = 1.0F;
	private float pitch = 1.0F;
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

	public SoundBuilder receiver(HasPlayer reciever) {
		this.receivers = Collections.singletonList(reciever);
		return this;
	}

	public SoundBuilder receivers(List<HasPlayer> recievers) {
		this.receivers = recievers;
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

	public SoundBuilder volume(double volume) {
		return volume((float) volume);
	}

	public SoundBuilder volume(float volume) {
		volume = Math.max(volume, 0.0F);
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
		pitch = MathUtils.clamp(pitch, 0.1F, 2.0F);
		this.pitch = pitch;
		return this;
	}

	public SoundBuilder category(SoundCategory category) {
		this.category = category;
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
		return new SoundBuilder(this.sound)
			.receivers(new ArrayList<>(this.receivers))
			.location(this.location.clone())
			.category(this.category)
			.pitch(this.pitch)
			.volume(this.volume)
			.delay(this.delay)
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

				location.getWorld().playSound(location, sound, category, volume, pitch);
			});

		else {
			// Play sound to receivers
			for (HasPlayer receiver : receivers) {
				if (location != null && receiver.getPlayer().getWorld() != location.getWorld())
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
								.player(receiver.getPlayer())
								.context(cooldownContext)
								.expiration(expiration)
								.create();
					}

					Location origin = location == null ? receiver.getPlayer().getLocation() : location;
					receiver.getPlayer().playSound(origin, sound, category, volume, pitch);
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
