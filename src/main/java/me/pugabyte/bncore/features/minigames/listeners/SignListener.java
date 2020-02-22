package me.pugabyte.bncore.features.minigames.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.Utils.colorize;

@NoArgsConstructor
public class SignListener implements Listener {
	public static String header = colorize("&l< &1Minigames &0&l>");

	@EventHandler
	public void onClickOnSign(PlayerInteractEvent event) {
		if (!Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK).contains(event.getAction())) return;
		if (!Arrays.asList(Material.SIGN_POST, Material.WALL_SIGN).contains(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
		if (!event.getPlayer().getWorld().equals(Minigames.getGameworld())) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		if (header.equals(sign.getLine(0))) {
			if ((ChatColor.GREEN + "Join").equalsIgnoreCase(sign.getLine(1))) {
				try {
					Arena arena = ArenaManager.find(sign.getLine(2));
					PlayerManager.get(event.getPlayer()).join(arena);
				} catch (Exception ex) {
					event.getPlayer().sendMessage(colorize(Minigames.PREFIX + ex.getMessage()));
				}
			}
			if ((ChatColor.GREEN + "Quit").equalsIgnoreCase(sign.getLine(1))) {
				PlayerManager.get(event.getPlayer()).quit();
			}
		}
	}

}
