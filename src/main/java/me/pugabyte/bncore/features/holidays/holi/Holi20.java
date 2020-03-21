package me.pugabyte.bncore.features.holidays.holi;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.FireworkLauncher;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Holi20 implements Listener {

	Map<Player, Integer> dyeBombCooldown = new HashMap<>();

	public Holi20() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void throwDyeBomb(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		if (!event.getAction().name().contains("RIGHT_CLICK")) return;
		if (!event.getMaterial().equals(Material.MAGMA_CREAM)) return;

		String itemName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
		if (!itemName.equalsIgnoreCase("Dye Bomb")) return;
		if (!event.getItem().getItemMeta().getLore().contains(StringUtils.colorize("&bHoli20 Event Item"))) return;

		Player player = event.getPlayer();
		CooldownService cooldownService = new CooldownService();
		try {
			cooldownService.check(player, "throwDyeBomb", 2 * 20);
		} catch (CooldownException e) {
			return;
		}

		Location location = player.getLocation().add(0, 1.5, 0);
		location.add(player.getLocation().getDirection());

		Snowball snowball = (Snowball) player.getWorld().spawnEntity(location, EntityType.SNOWBALL);
		snowball.setVelocity(location.getDirection().multiply(1.5));
		snowball.setShooter(player);
		snowball.setCustomName("DyeBomb");

		player.playSound(player.getLocation(), Sound.ENTITY_EGG_THROW, 0.5F, 1F);
	}

	// TODO: 50% of the time do a single random solid color instead
	@EventHandler
	public void onDyeBombHit(ProjectileHitEvent event) {
		if (event.getEntity() == null) return;
		if (!event.getEntityType().equals(EntityType.SNOWBALL)) return;
		if (event.getEntity().getCustomName() == null) return;
		if (!event.getEntity().getCustomName().equalsIgnoreCase("DyeBomb")) return;
		if (event.getHitBlock() == null) {
			if (event.getHitEntity() == null)
				return;
		}

		Vector vel = event.getEntity().getVelocity().normalize().multiply(0.1);
		Location hitLoc = event.getEntity().getLocation().subtract(vel);

		FireworkLauncher.random(hitLoc).detonateAfter(0).power(0).type(FireworkEffect.Type.BURST).launch();
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		Block eventBlock = event.getBlockClicked();
		Location loc = eventBlock.getLocation();
		WorldGuardUtils WGUtils = new WorldGuardUtils(loc.getWorld());
		for (ProtectedRegion region : WGUtils.getRegionsAt(eventBlock.getLocation())) {
			if (region.getId().contains("quest_water")) {
				event.getPlayer().getInventory().remove(new ItemStack(Material.BUCKET, 1));
//				event.getItemStack().setAmount(event.getItemStack().getAmount() - 1);
				Utils.giveItem(event.getPlayer(), new ItemStack(Material.WATER_BUCKET, 1));
				event.setCancelled(true);
			}
		}
	}
}
