package gg.projecteden.nexus.features.events.y2021.bearfair21;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import me.lexikiq.HasPlayer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

public class BearFair21Restrictions implements Listener {

	public BearFair21Restrictions() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemFrameBreak(EntityDamageByEntityEvent event) {
		if (BearFair21.isNotAtBearFair(event.getEntity()))
			return;

		EntityType breakType = event.getEntityType();
		if (!breakType.equals(EntityType.ITEM_FRAME) && !breakType.equals(EntityType.PAINTING))
			return;

		Entity damager = event.getDamager();
		if (!(damager instanceof Player) && !(damager instanceof Projectile))
			return;

		if (damager instanceof Projectile) {
			Entity shooter = (Entity) ((Projectile) damager).getShooter();
			if (shooter instanceof Player) {
				if (WorldGuardFlagUtils.hasBypass((HasPlayer) shooter))
					return;
			}
		} else {
			if (WorldGuardFlagUtils.hasBypass((HasPlayer) damager))
				return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onInteractWithVillager(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (BearFair21.isNotAtBearFair(entity)) return;
		if (!(entity instanceof Villager)) return;
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onMcMMOXpGainEvent(McMMOPlayerXpGainEvent event) {
		if (BearFair21.isNotAtBearFair(event.getPlayer())) return;
		event.setRawXpGained(0F);
		event.setCancelled(true);
	}

	@EventHandler
	public void onTameEntity(EntityTameEvent event) {
		if (BearFair21.isNotAtBearFair(event.getEntity())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onLecternTakeBook(PlayerTakeLecternBookEvent event) {
		Location loc = event.getLectern().getBlock().getLocation();
		if (BearFair21.isNotAtBearFair(loc)) return;

		event.setCancelled(true);
		event.getPlayer().closeInventory();
	}

	@EventHandler
	public void onMcMMOFishing(McMMOPlayerFishingEvent event) {
		if (BearFair21.isNotAtBearFair(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockDropItemEvent(BlockDropItemEvent event) {
		if (BearFair21.isNotAtBearFair(event.getBlock()))
			return;

		event.getItems().forEach(item -> {
			Material type = item.getItemStack().getType();
			if (type.equals(Material.WHEAT_SEEDS) || type.equals(Material.BEETROOT_SEEDS))
				item.remove();
		});
	}

	@EventHandler
	public void onWitherRoseDamageEvent(EntityDamageEvent event) {
		if (BearFair21.isNotAtBearFair(event.getEntity()))
			return;

		if (event.getCause().equals(DamageCause.WITHER))
			event.setCancelled(true);
	}
}
