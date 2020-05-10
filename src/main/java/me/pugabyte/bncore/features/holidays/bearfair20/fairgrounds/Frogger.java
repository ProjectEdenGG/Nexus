package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

// TODO - Animation
public class Frogger implements Listener {

	private static String froggerRg = BearFair20.mainRg + "_frogger";
	private static String winRg = froggerRg + "_win";
	private static String damageRg = froggerRg + "_damage";
	private static Location respawnLoc = new Location(BearFair20.world, -856.5, 138, -1623.5, -180, 0);
	private static boolean animateBool = false;

	public Frogger() {
		BNCore.registerListener(this);
		animationTask();
	}

	public void animationTask() {
		// TODO: Waiting on WorldEdit Schematic API support
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String regionId = event.getRegion().getId();
		Player player = event.getPlayer();
		if (regionId.equalsIgnoreCase(froggerRg)) {
			if (animateBool)
				return;
			animateBool = true;
		} else if (regionId.equalsIgnoreCase(damageRg)) {
			String cheatingMsg = BearFair20.isCheatingMsg(player);
			if (cheatingMsg != null && !cheatingMsg.contains("wgedit")) {
				player.teleport(respawnLoc);
				player.sendMessage("Don't cheat, turn " + cheatingMsg + " off!");
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
			}
		} else if (regionId.equalsIgnoreCase(winRg)) {
			player.teleport(respawnLoc);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10F, 2F);
			BearFair20.givePoints(player, 1);

		}
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(froggerRg)) {
			int size = WGUtils.getPlayersInRegion(froggerRg).size();
			if (size == 0)
				animateBool = false;
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();
		if (!WGUtils.isInRegion(player.getLocation(), damageRg)) return;

		if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
		event.setDamage(0);
		player.setFireTicks(0);
		player.teleport(respawnLoc);
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10F, 1F);

	}

}
