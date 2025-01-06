package gg.projecteden.nexus.features.achievements.listeners;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

@Disabled
public class MiscListener implements Listener {

	@EventHandler
	public void onSleep(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();

		Achievement.SLEEPY.check(player);
	}

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (event.getResult() != Result.ALLOW) return;
		int amount = event.getRecipe().getResult().getAmount();

		Achievement.CRAFTING_KING.check(player, amount);
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		Material material = event.getItem().getItemStack().getType();

		if (MaterialTag.ITEMS_MUSIC_DISCS.isTagged(material))
			Achievement.THE_COLLECTOR.check(player, material.toString().replaceFirst("RECORD", ""));
	}

	@EventHandler
	public void onClickOnSign(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getClickedBlock() != null) {
			Block block = event.getClickedBlock();
			if (MaterialTag.SIGNS.isTagged(block.getType())) {
				Sign sign = (Sign) block.getState();
				String line1 = StringUtils.stripColor(sign.getLine(0));

				Set<String> regions = new WorldGuardUtils(player).getRegionNamesAt(player.getLocation());

				if (regions.contains("spawn"))
					if (line1.contains("[Sign War]"))
						Achievement.JOINING_THE_WAR.check(player);
				if (regions.contains("wallsofgrace"))
					if (line1.contains("Schmileyfache!"))
						Achievement.HIDDEN_WORDS.check(player);
			}
		}
	}
}
