package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void kill(Minigamer minigamer) {
		minigamer.setRespawning(true);
		minigamer.clearState();
		minigamer.teleport(minigamer.getMatch().getArena().getRespawnLocation());
		minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2, false, false));
		BNCore.runTaskLater(100, () -> {
			if (!minigamer.getMatch().isEnded()) {
				minigamer.getTeam().spawn(Collections.singletonList(minigamer));
				minigamer.setRespawning(false);
			}
		});
	}

}
