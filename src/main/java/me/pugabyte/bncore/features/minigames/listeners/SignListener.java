package me.pugabyte.bncore.features.minigames.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@NoArgsConstructor
public class SignListener implements Listener {
	public static final String HEADER = "< Minigames >";

	@EventHandler
	public void onClickOnSign(PlayerInteractEvent event) {
		if (!Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK).contains(event.getAction())) return;
		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
		if (!Minigames.isMinigameWorld(event.getPlayer().getWorld())) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		if (HEADER.equals(stripColor(sign.getLine(0)))) {
			switch (stripColor(sign.getLine(1).toLowerCase())) {
				case "join":
					try {
						Arena arena = ArenaManager.find(sign.getLine(2));
						PlayerManager.get(event.getPlayer()).join(arena);
					} catch (Exception ex) {
						event.getPlayer().sendMessage(colorize(Minigames.PREFIX + ex.getMessage()));
					}
					break;
				case "quit":
					PlayerManager.get(event.getPlayer()).quit();
					break;
				case "lobby":
					Utils.runCommand(event.getPlayer(), "warp minigames");
					break;
				case "force start":
					Utils.runCommandAsOp(event.getPlayer(), "newmgm start");
					break;
			}
		}
	}

}
