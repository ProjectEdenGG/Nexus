package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.lexikiq.HasPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	public SoundBuilder clone() {
		return new SoundBuilder(sound)
			.receivers(new ArrayList<>(receivers))
			.location(location.clone())
			.category(category)
			.muteMenuItem(muteMenuItem)
			.pitch(pitch)
			.volume(volume)
			.delay(delay);
	}

	public void play() {
		if (sound == null)
			throw new InvalidInputException("SoundBuilder: Sound cannot be null!");

		if (Utils.isNullOrEmpty(receivers) && location != null)
			// play sound in world
			Tasks.wait(delay, () -> location.getWorld().playSound(location, sound, category, volume.apply(null), pitch.apply(null)));
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
					if (!player.isOnline())
						return;

					Location origin = location;
					if (origin == null)
						origin = player.getLocation();

					Location finalOrigin = origin;
					player.playSound(finalOrigin, sound, category, volume.apply(player), pitch.apply(player));
				});
			}
		}
	}
}
