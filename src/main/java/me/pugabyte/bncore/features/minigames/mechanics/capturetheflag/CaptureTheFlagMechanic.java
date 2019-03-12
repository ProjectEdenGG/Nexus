package me.pugabyte.bncore.features.minigames.mechanics.capturetheflag;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.pugabyte.bncore.features.minigames.Minigames.getPlayerManager;

public abstract class CaptureTheFlagMechanic extends BalancedTeamMechanic {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Minigamer minigamer = getPlayerManager().get(event.getPlayer());

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN) {
				if (minigamer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
					Sign sign = (Sign) event.getClickedBlock().getState();

					if ((ChatColor.DARK_BLUE + "[Minigame]").equalsIgnoreCase(sign.getLine(0))) {
						if ((ChatColor.GREEN + "Flag").equalsIgnoreCase(sign.getLine(1))) {
							onFlagInteract(minigamer, sign);
						}
					}
				}
			}
		}
	}

	public abstract void onFlagInteract(Minigamer minigamer, Sign sign);

	public abstract void dropFlag(PlayerDeathEvent event);
}
