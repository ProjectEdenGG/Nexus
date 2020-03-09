package me.pugabyte.bncore.features.holidays.holi;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.FireworkLauncher;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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

	// Will need to be 1.13 ified
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		if (!(event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))) return;
		Block eventBlock = event.getBlock();
		Location loc = eventBlock.getLocation();
		WorldGuardUtils WGUtils = new WorldGuardUtils(loc.getWorld());

		for (ProtectedRegion region : WGUtils.getRegionsAt(loc)) {
			if (region.getId().contains("regen")) {
				String[] regionSplit = region.getId().split("_");
				String regenBlockStr = regionSplit[2];
				String[] regenBlockSplit = regenBlockStr.split("-");
				int regenBlockID = Integer.parseInt(regenBlockSplit[0]);
				int regenBlockData = Integer.parseInt(regenBlockSplit[1]);

				int eventBlockID = eventBlock.getTypeId();
				int eventBlockData = eventBlock.getData();

				if (regenBlockID == eventBlockID && regenBlockData == eventBlockData) {
					regenBlock(loc, eventBlock.getType(), eventBlock.getData(), 30);
					break;
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	public void regenBlock(Location location, Material material, byte data, int seconds) {
		Tasks.wait(seconds * 20, () -> {
			Block block = location.getBlock();
			block.setType(material);
			block.setData(data);
		});
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
