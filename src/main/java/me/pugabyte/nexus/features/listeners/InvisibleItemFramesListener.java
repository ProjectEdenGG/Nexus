package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

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
			if (ItemUtils.isNullOrAir(itemFrame.getItem())) {
				itemFrame.setVisible(true);
				itemFrame.setGlowing(true);
			} else
				itemFrame.setVisible(false);
		}
	}

}
