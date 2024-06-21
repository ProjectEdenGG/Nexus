package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.parchment.HasPlayer;
import org.bukkit.Sound;

public class Pugmas24Quests {

	public static void sound_obtainItem(HasPlayer player) {
		new SoundBuilder(Sound.ENTITY_PLAYER_LEVELUP).receiver(player).volume(0.5).pitch(2.0).play();
	}

	public static void sound_completeQuest(HasPlayer player) {
		new SoundBuilder(Sound.UI_TOAST_CHALLENGE_COMPLETE).receiver(player).volume(0.5).play();
	}

	public static void sound_villagerNo(HasPlayer player) {
		new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).volume(0.5).play();
	}
}
