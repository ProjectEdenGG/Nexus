package me.pugabyte.nexus.features.minigames.lobby;

import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwnerService;
import me.pugabyte.nexus.features.minigames.models.perks.common.TickablePerk;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.CitizensUtils.isNPC;

public class TickPerks {
	private static final PerkOwnerService service = new PerkOwnerService();

	public TickPerks() {
		Tasks.repeat(5, Minigames.PERK_TICK_DELAY, () -> Minigames.getWorld().getPlayers().forEach(player -> {
			Minigamer minigamer = PlayerManager.get(player);
			if ((minigamer.isPlaying() || isInRegion(player)) && !isNPC(player)) {
				PerkOwner perkOwner = service.get(player);
				perkOwner.getEnabledPerks().forEach((perkType, enabled) -> {
					if (enabled && perkType.getPerk() instanceof TickablePerk) {
						TickablePerk perk = (TickablePerk) perkType.getPerk();
						if (minigamer.isPlaying())
							perk.tick(minigamer);
						else
							perk.tick(player);
					}
				});
			}
		}));
	}

	public boolean isInRegion(Player player) {
		return Minigames.getWorldGuardUtils().isInRegion(player.getLocation(), Minigames.getLobbyRegion());
	}
}
