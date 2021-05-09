package me.pugabyte.nexus.features.quests.ambience;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.quests.ambience.sound.SoundPlayer;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class AmbienceUser {
	@NonNull
	Player player;
	BlockScanner blockScanner;
	SoundPlayer soundPlayer;
	Variables variables;


}