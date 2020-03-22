package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.features.chat.koda.Koda;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Restrictions implements Listener {
	String prefix = Koda.getLocalFormat();

	@EventHandler
	public void onInteractWithFire(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		if (Utils.isNullOrAir(event.getItem()))
			return;

		Material itemType = event.getItem().getType();
		if (!(itemType.equals(Material.FLINT_AND_STEEL) || itemType.equals(Material.FIREBALL)))
			return;

		Player player = event.getPlayer();
		Material clickedMaterial = event.getClickedBlock().getType();
		if (WorldGroup.get(player.getWorld()).equals(WorldGroup.CREATIVE))
			if (clickedMaterial.equals(Material.TNT))
				event.setCancelled(true);

		if (!(clickedMaterial.equals(Material.OBSIDIAN) || clickedMaterial.equals(Material.NETHERRACK)))
			if (!player.hasPermission("use.fire")) {
				event.setCancelled(true);
				player.sendMessage(StringUtils.colorize(prefix + "Sorry, but you are not a high enough rank to light fire! Please create a &c/ticket &fto ask a staff member to light fire for you"));
			}
	}

	@EventHandler
	public void onPlaceLava(PlayerBucketEmptyEvent event) {
		Material material = event.getBucket();
		if (!material.equals(Material.LAVA_BUCKET))
			return;

		Player player = event.getPlayer();
		if (!player.hasPermission("use.fire")) {
			event.setCancelled(true);
			player.sendMessage(StringUtils.colorize(prefix + "Hey " + player.getName() + "! I noticed that you are trying to place lava. Unfortunately lava placing is disabled for Member and below due to grief and safety issues"));
			Tasks.wait(20, () -> player.sendMessage(StringUtils.colorize(prefix + "However, a staff member will be happy to place it for you. Please create a &c/ticket &fand a staff member will be with your shortly! :)")));
		}
	}

	@EventHandler
	public void onPlaceTNT(BlockPlaceEvent event) {
		Material material = event.getBlock().getType();
		if (!material.equals(Material.TNT))
			return;

		Player player = event.getPlayer();
		if (!player.hasPermission("use.fire")) {
			event.setCancelled(true);
			player.sendMessage(StringUtils.colorize(prefix + "Sorry, but you can't use TNT! You must be Member or above"));
		}
	}

	@EventHandler
	public void onCraftTNT(CraftItemEvent event) {
		if (!event.getRecipe().getResult().getType().equals(Material.TNT))
			return;

		Player player = (Player) event.getWhoClicked();
		if (!player.hasPermission("use.fire")) {
			event.setCancelled(true);
			player.sendMessage(StringUtils.colorize(prefix + "Sorry, but you can't use TNT! You must be Member or above"));
		}
	}

	@EventHandler
	public void onInteractHoldingEnderCrystal(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (player.hasPermission("use.fire"))
			return;

		if (!Utils.isNullOrAir(event.getItem()) && event.getItem().getType().equals(Material.END_CRYSTAL)) {
			event.setCancelled(true);
			player.sendMessage(StringUtils.colorize(prefix + "Sorry, but you can't use Ender Crystals! You must be Member or above"));
		}
	}

	@EventHandler
	public void onDamageEnderCrystal(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Arrow)
			if (((Arrow) damager).getShooter() instanceof Player)
				damager = ((Player) ((Arrow) damager).getShooter()).getPlayer();

		if (!event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL))
			return;

		if (!damager.hasPermission("use.fire")) {
			event.setCancelled(true);
			damager.sendMessage(StringUtils.colorize(prefix + "Sorry, but you can't use Ender Crystals! You must be Member or above"));
		}

	}

	@EventHandler
	public void onInteractHoldingSpawnEgg(PlayerInteractEvent event) {
		if (Utils.isNullOrAir(event.getItem()))
			return;

		if (!event.getItem().getType().equals(Material.MONSTER_EGG))
			return;

		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		if (!event.getClickedBlock().getType().equals(Material.MOB_SPAWNER))
			return;

		if (!event.getPlayer().hasPermission("group.seniorstaff"))
			event.setCancelled(true);
	}


	@EventHandler
	public void onPlaceBed(BlockPlaceEvent event) {
		Material material = event.getBlock().getType();
		if (!material.equals(Material.BED_BLOCK))
			return;

		Player player = event.getPlayer();
		String worldName = player.getWorld().getName().toLowerCase();
		if (!(worldName.endsWith("nether") || worldName.endsWith("the_end")))
			return;

		if (!player.hasPermission("group.staff")) {
			event.setCancelled(true);
			player.sendMessage(StringUtils.colorize(prefix + "Sorry, but you can't place beds here! They will go boom!"));
		}
	}
}
