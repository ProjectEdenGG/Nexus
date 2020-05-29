package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
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

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.BFProtectedRg;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

public class EasterEggs implements Listener {
	private static final String easterEgg = "ba3b7698-589c-3326-90ff-4862853a5c24";
	BearFairService service = new BearFairService();
	private static final int total = 9;

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
		if (!player.getWorld().equals(BearFair20.world)) return;
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(BFProtectedRg)) return;

		Skull skull = (Skull) block.getState();
		if (skull.getOwningPlayer() == null) return;
		if (!easterEgg.equals(skull.getOwningPlayer().getUniqueId().toString())) return;

		Location playerLoc = player.getLocation();
		Location blockLoc = block.getLocation();
		BearFairUser bfUser = service.get(player);
		List<Location> foundLocs = bfUser.getEasterEggsLocs();
		if (foundLocs.contains(blockLoc)) {
			player.sendMessage("TODO: You already found this one.");
			player.playSound(playerLoc, Sound.ENTITY_VILLAGER_NO, 2F, 1F);
			return;
		}

		foundLocs.add(blockLoc);
		service.save(bfUser);

		if (foundLocs.size() == total) {
			player.sendMessage("TODO: You found all treasure chests!");
			player.playSound(playerLoc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 2F, 1F);
		} else {
			player.playSound(playerLoc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2F, 2F);
			player.playSound(playerLoc, Sound.BLOCK_BEACON_POWER_SELECT, 2F, 2F);
		}
	}

}
