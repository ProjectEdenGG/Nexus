package me.pugabyte.nexus.features.minigames.mechanics.common;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.nexus.utils.MaterialTag;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public abstract class CaptureTheFlagMechanic extends BalancedTeamMechanic {

	@Override
	public boolean useAlternativeRegen() {
		return true;
	}

	protected abstract void onFlagInteract(Minigamer minigamer, Sign sign);

	protected abstract void doFlagParticles(Match match);

	protected abstract void onEnterKillRegion(Minigamer minigamer);

	protected boolean canPickupFlag(Minigamer minigamer, Sign sign) {
		if (minigamer.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
			if (sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Flag")) {
				minigamer.send(ChatColor.RED + "You must be unarmed to interact with flags!");
			}
			return false;
		}
		return true;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());

		if (!(
				minigamer.isPlaying(this) &&
						event.getAction() == Action.RIGHT_CLICK_BLOCK &&
						event.getClickedBlock() != null &&
						event.getHand() != null &&
						event.getHand().equals(EquipmentSlot.HAND) &&
						MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType()) &&
						canPickupFlag(minigamer, (Sign) event.getClickedBlock().getState())
		)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		if ((ChatColor.DARK_BLUE + "[Minigame]").equalsIgnoreCase(sign.getLine(0)))
			if ((ChatColor.GREEN + "Flag").equalsIgnoreCase(sign.getLine(1))) {
				onFlagInteract(minigamer, sign);
			}
	}

	@EventHandler
	public void onRegionEvent(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion(), "kill")) return;

		onEnterKillRegion(minigamer);
	}

	@EventHandler
	public void onMatchTimerTick(MatchTimerTickEvent event) {
		if (!event.getMatch().isMechanic(this)) return;
		if (event.getTime() % 2 != 0) return;

		doFlagParticles(event.getMatch());
	}

}
