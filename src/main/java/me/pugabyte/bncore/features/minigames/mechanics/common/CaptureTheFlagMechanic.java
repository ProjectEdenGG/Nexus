package me.pugabyte.bncore.features.minigames.mechanics.common;

import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.CaptureTheFlagMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.Flag;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public abstract class CaptureTheFlagMechanic extends BalancedTeamMechanic {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());

		if (!(
				minigamer.isPlaying(this) &&
				event.getAction() == Action.RIGHT_CLICK_BLOCK &&
				event.getClickedBlock() != null &&
				event.getHand() != null &&
				event.getHand().equals(EquipmentSlot.HAND) &&
				minigamer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR &&
				!MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())
		)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		if ((ChatColor.DARK_BLUE + "[Minigame]").equalsIgnoreCase(sign.getLine(0))) {
			if ((ChatColor.GREEN + "Flag").equalsIgnoreCase(sign.getLine(1))) {
				onFlagInteract(minigamer, sign);
			}
		}
	}

	public abstract void onFlagInteract(Minigamer minigamer, Sign sign);

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer minigamer = event.getMinigamer();
		CaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
		Flag carriedFlag = matchData.getFlagByCarrier(minigamer);
		if (carriedFlag != null) {
			carriedFlag.drop(minigamer.getPlayer().getLocation());

			matchData.removeFlagCarrier(minigamer);
		}

		super.onDeath(event);
	}

}
