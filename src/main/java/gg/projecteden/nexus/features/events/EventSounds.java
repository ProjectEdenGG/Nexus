package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.parchment.OptionalPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;

@Getter
@RequiredArgsConstructor
public enum EventSounds {
	VILLAGER_NO(new SoundBuilder(Sound.ENTITY_VILLAGER_NO).volume(0.5));

	private final SoundBuilder sound;

	public void play(OptionalPlayer player) {
		sound.clone().receiver(player).play();
	}

}
