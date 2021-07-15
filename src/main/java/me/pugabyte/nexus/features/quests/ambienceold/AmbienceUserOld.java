package me.pugabyte.nexus.features.quests.ambienceold;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.quests.ambienceold.sound.SoundPlayer;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class AmbienceUserOld {
	@NonNull
	Player player;
	BlockScanner blockScanner;
	SoundPlayer soundPlayer;
	VariablesOld variables;


}
