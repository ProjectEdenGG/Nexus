package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InvisibleItemFramesListener implements Listener {

	@EventHandler
	public void onSplashPotion(LingeringPotionSplashEvent event) {
		List<PotionEffectType> potionEffectTypes = new ArrayList<>();
		event.getEntity().getEffects().forEach(e -> potionEffectTypes.add(e.getType()));
		if (!potionEffectTypes.contains(PotionEffectType.INVISIBILITY)) return;
		List<Entity> entities = Arrays.stream(event.getEntity().getNearbyEntities(2, 2, 2).toArray(Entity[]::new)).filter(e -> e.getType() == EntityType.ITEM_FRAME).collect(Collectors.toList());

		// Cancel if in a region
		if (!new WorldGuardUtils(event.getEntity().getWorld()).getRegionsAt(event.getEntity().getLocation()).isEmpty())
			return;

		// Get the iFrame plugin's instance to make them work
		Plugin iFramePlugin = Bukkit.getPluginManager().getPlugin("SurvivalInvisiframes");
		if (iFramePlugin == null || !iFramePlugin.isEnabled()) return;
		NamespacedKey key = new NamespacedKey(iFramePlugin, "invisible");
		for (Entity entity : entities) {
			ItemFrame itemFrame = (ItemFrame) entity;
			itemFrame.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
			if (Nullables.isNullOrAir(itemFrame.getItem())) {
				itemFrame.setVisible(true);
				itemFrame.setGlowing(true);
			} else
				itemFrame.setVisible(false);
		}
	}

	@EventHandler
	public void onDye(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		final Player player = event.getPlayer();

		if (player.isSneaking())
			return;

		if (!Rank.of(player).isStaff() && WorldGroup.of(player) != WorldGroup.STAFF)
			return;

		if (entity.getType() != EntityType.ITEM_FRAME)
			return;

		if (!entity.isGlowing())
			return;

		ItemStack dye = player.getInventory().getItem(event.getHand());

		if (dye == null || !MaterialTag.DYES.isTagged(dye.getType()))
			return;

		final ColorType color = ColorType.of(dye.getType());

		if (color == null)
			return;

		DyeColor dyeColor = color.getDyeColor();

		if (dyeColor == null)
			return;

		event.setCancelled(true);

		final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		final String teamId = "ifc-" + dyeColor.name().toLowerCase();

		Team team = scoreboard.getTeam(teamId);
		if (team == null)
			team = scoreboard.registerNewTeam(teamId);

		team.color(color.getNamedColor());
		team.addEntry(entity.getUniqueId().toString());
	}

}
