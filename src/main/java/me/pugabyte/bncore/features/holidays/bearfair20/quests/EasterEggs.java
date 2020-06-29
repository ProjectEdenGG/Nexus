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

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.*;

public class EasterEggs implements Listener {
	BearFairService service = new BearFairService();
	private static final String easterEgg = "ba3b7698-589c-3326-90ff-4862853a5c24";
	private static final int total = 15;
	private static String foundOne = "You found a secret treasure chest! There are still more to find throughout the islands.";
	private static String duplicate = "You already found this one.";
	private static String foundAll = "The final treasure chest has been found! You are a champion of treasure hunting. Congratulations!";

	/*
	Easter Egg Spots:
			-939 137 -1624 (under tree next to tent)
			-1155 141 -1777 (under house at MGN)
			-1152 110 -1749 (under volcano)
			-902 133 -1637 (under fisherman)
			-991 135 -1621 (roller coaster bit)
			-1095 155 -1559 (observatory)
			-1101 137 -1651 (basement by farm)
			-944 119 -1895 (mansion blood sewer)
			-1050 129 -1913 (pugmas cave)
			-1020 139 -1702 (quarry, diorite)
			-1050 127 -1660 (quarry, gravel)
			-852 113 -1755 (SDU, under)
			-828 161 -1797 (SDU, above)
			-965 115 -1710 (lapis mine)
			-977 135 -1528 (boat)
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

		if (foundLocs.size() >= total) {
			send(foundAll, player);
			player.playSound(playerLoc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 2F, 1F);
		} else {
			send(foundOne, player);
			player.playSound(playerLoc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2F, 2F);
			player.playSound(playerLoc, Sound.BLOCK_BEACON_POWER_SELECT, 2F, 2F);
		}
	}

}
