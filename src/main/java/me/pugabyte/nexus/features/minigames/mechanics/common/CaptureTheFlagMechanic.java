package me.pugabyte.nexus.features.minigames.mechanics.common;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.mechanics.CaptureTheFlag;
import me.pugabyte.nexus.features.minigames.mechanics.OneFlagCaptureTheFlag;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.CaptureTheFlagMatchData;
import me.pugabyte.nexus.features.minigames.models.matchdata.Flag;
import me.pugabyte.nexus.features.minigames.models.matchdata.OneFlagCaptureTheFlagMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
import net.md_5.bungee.api.ChatColor;
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
						MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())
		)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		if ((ChatColor.DARK_BLUE + "[Minigame]").equalsIgnoreCase(sign.getLine(0))) {
			if ((ChatColor.GREEN + "Flag").equalsIgnoreCase(sign.getLine(1))) {
				onFlagInteract(minigamer, sign);
			}
		}
	}

	public abstract void onFlagInteract(Minigamer minigamer, Sign sign);

	public abstract void doFlagParticles(Match match);

	@EventHandler
	public void onRegionEvent(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion(), "kill")) return;

		// TODO Better abstraction
		Flag flag = null;
		Mechanic mechanic = minigamer.getMatch().getArena().getMechanic();
		if (mechanic instanceof CaptureTheFlag) {
			CaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
			flag = matchData.getFlagByCarrier(minigamer);
		} else if (mechanic instanceof OneFlagCaptureTheFlag) {
			OneFlagCaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
			flag = matchData.getFlag();
		}

		if (flag != null) {
			flag.drop(minigamer.getPlayer().getLocation());

			String flagName = null;
			if (mechanic instanceof CaptureTheFlag) {
				CaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
				matchData.removeFlagCarrier(minigamer);
				flagName = flag.getTeam().getColoredName() + "&3's";
			} else if (mechanic instanceof OneFlagCaptureTheFlag) {
				OneFlagCaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
				matchData.setFlagCarrier(null);
				flagName = "the";
			}

			String finalFlagName = flagName;
			Tasks.wait(5, () -> minigamer.getMatch().broadcast(minigamer.getColoredName() + " &3dropped " + finalFlagName + "&3's flag outside the map"));
		}
	}

	@EventHandler
	public void onMatchTimerTick(MatchTimerTickEvent event) {
		if (event.getMatch().isMechanic(this)) return;
		if (event.getTime() % 2 != 0) return;

		CaptureTheFlagMechanic mechanic = event.getMatch().getArena().getMechanic();
		mechanic.doFlagParticles(event.getMatch());
	}

}
