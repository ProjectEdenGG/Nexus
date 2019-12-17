package me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer;

import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void kill(Minigamer minigamer) {
		if (minigamer.isRespawning()) return;
		minigamer.clearState();
		if (minigamer.getMatch().getArena().getRespawnLocation() != null) {
			minigamer.setRespawning(true);
			minigamer.teleport(minigamer.getMatch().getArena().getRespawnLocation());
			minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2, false, false));
			Utils.wait(100, () -> {
				if (!minigamer.getMatch().isEnded()) {
					minigamer.getTeam().spawn(minigamer);
					minigamer.setRespawning(false);
				}
			});
		} else {
			if (!minigamer.getMatch().isEnded())
				minigamer.getTeam().spawn(minigamer);
		}
	}

}
