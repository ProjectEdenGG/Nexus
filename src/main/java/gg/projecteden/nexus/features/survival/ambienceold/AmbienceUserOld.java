package gg.projecteden.nexus.features.survival.ambienceold;

import gg.projecteden.nexus.features.survival.ambienceold.sound.SoundPlayer;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
