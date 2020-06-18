package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.givePoints;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.isAtBearFair;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.send;

public class EasterEggs implements Listener {
	BearFairService service = new BearFairService();
	private static final String easterEgg = "ba3b7698-589c-3326-90ff-4862853a5c24";
	private static final int total = 10;
	private static String foundOne = "TODO: You found a treasure chest!";
	private static String duplicate = "TODO: You already found this one!";
	private static String foundAll = "TODO: You found all treasure chests!";

	/*
	Easter Egg Spots:
			-939 137 -1624
			-1155 141 -1777
			-902 133 -1637
			-991 135 -1621
			-1095 155 -1559
			-1101 137 -1651
			-944 119 -1895
			-1050 129 -1913
			-1020 139 -1702
			-1050 127 -1660
			-852 113 -1755
	 */

	public EasterEggs() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onRightClickSkull(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!MaterialTag.SKULLS.isTagged(block.getType())) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;

		Skull skull = (Skull) block.getState();
		if (skull.getOwningPlayer() == null) return;
		if (!easterEgg.equals(skull.getOwningPlayer().getUniqueId().toString())) return;

		Location playerLoc = player.getLocation();
		Location blockLoc = block.getLocation();
		BearFairUser bfUser = service.get(player);
		List<Location> foundLocs = bfUser.getEasterEggsLocs();
		if (foundLocs.contains(blockLoc)) {
			send(duplicate, player);
			player.playSound(playerLoc, Sound.ENTITY_VILLAGER_NO, 2F, 1F);
			return;
		}

		foundLocs.add(blockLoc);
		if (givePoints) {
			bfUser.givePoints(1, true); // TODO: Determine amount of points, random?
			service.save(bfUser);
		}

		if (foundLocs.size() == total) {
			send(foundAll, player);
			player.playSound(playerLoc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 2F, 1F);
		} else {
			send(foundOne, player);
			player.playSound(playerLoc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2F, 2F);
			player.playSound(playerLoc, Sound.BLOCK_BEACON_POWER_SELECT, 2F, 2F);
		}
	}

}
