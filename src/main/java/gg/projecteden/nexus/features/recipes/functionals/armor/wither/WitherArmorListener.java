package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class WitherArmorListener implements Listener {

	public WitherArmorListener() {
		for (Player player : PlayerUtils.getOnlinePlayers()) {
			if (hasFullSet(player)) {
				player.setAllowFlight(true);
			}
		}
		startDoubleJumpTask();
	}

	public boolean hasFullSet(Player player) {
		PlayerInventory inv = player.getInventory();
		if (!isWitherArmor(inv.getHelmet())) return false;
		if (!isWitherArmor(inv.getChestplate())) return false;
		if (!isWitherArmor(inv.getLeggings())) return false;
		return isWitherArmor(inv.getBoots());
	}

	public boolean isWitherArmor(ItemStack item) {
		if (ItemUtils.isNullOrAir(item)) return false;
		NBTItem nbtItem = new NBTItem(item);
		if (!nbtItem.hasKey("wither-armor"))
			return false;
		return nbtItem.getBoolean("wither-armor");
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDamager() instanceof Projectile)) return;
		if (!hasFullSet(player)) return;
		if (player.getHealth() > 5) return;
		event.setDamage(event.getFinalDamage() * .25);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR) {
			handleEvent(event.getPlayer());
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player player) {
			handleEvent(player);
		}
	}

	private void handleEvent(Player player) {
		if (!hasFullSet(player)) return;
		if (!ItemUtils.isNullOrAir(player.getInventory().getItemInMainHand())) return;
		if (!new WorldGuardUtils(player).getRegionsAt(player.getLocation()).isEmpty()) return;
		if (!new CooldownService().check(player.getUniqueId(), "wither-armor-attack", TimeUtils.TickTime.SECOND.x(3))) return;
		shootSkull(player, true);
		Tasks.wait(5, () -> shootSkull(player, false));
		Tasks.wait(10, () -> shootSkull(player, false));
	}

	private void shootSkull(Player player, boolean accurate) {
		WitherSkull wc = player.launchProjectile(WitherSkull.class);
		Vector velocity = player.getEyeLocation().getDirection();
		if (!accurate)
			velocity.add(new Vector(RandomUtils.randomDouble(-.2, .2), RandomUtils.randomDouble(-.2, .2), RandomUtils.randomDouble(-.2, .2)));
		wc.setVelocity(velocity.multiply(.6));
		wc.setIsIncendiary(false);
		wc.setYield(0f);
		Tasks.wait(TimeUtils.TickTime.SECOND.x(1.5), wc::remove);
	}

	@EventHandler
	public void onEquipArmor(PlayerArmorChangeEvent event) {
		if (!isWitherArmor(event.getNewItem())) return;
		Player player = event.getPlayer();
		if (!hasFullSet(player)) return;
		player.setAllowFlight(true);
		SpeedCommand.resetSpeed(player, true);
	}

	@EventHandler
	public void onUnequipArmor(PlayerArmorChangeEvent event) {
		Player player = event.getPlayer();
		if (!player.getAllowFlight()) return;
		if (player.getGameMode() == GameMode.CREATIVE) return;
		if (Rank.of(player).isStaff()) return;
		if (!isWitherArmor(event.getOldItem())) return;
		if (isWitherArmor(event.getNewItem())) return;
		player.setAllowFlight(false);
	}

	@EventHandler
	public void onWitherSkullDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof WitherSkull witherSkull)) return;
		if (!Arrays.asList(EntityType.ARMOR_STAND, EntityType.ITEM_FRAME).contains(event.getEntity().getType()) && !(event.getEntity() instanceof Item)) return;
		if (!(witherSkull.getShooter() instanceof Player)) return;
		event.setCancelled(true);
	}

	private void startDoubleJumpTask() {
		Tasks.repeat(1, 1, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!hasFullSet(player)) continue;
				if (player.getGameMode() == GameMode.CREATIVE) continue;
				if (!player.isFlying()) continue;

				player.setAllowFlight(false);
				player.setFlying(false);

				Vector vector = player.getEyeLocation().getDirection();
				vector.multiply(1.15);
				vector.setY(Math.abs(vector.getY()));
				vector.setY(vector.getY() + 0.2);

				player.setVelocity(vector);
				player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.8f, 0.7f);
				Tasks.wait(TimeUtils.TickTime.SECOND.x(10), () -> player.setAllowFlight(true));
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!hasFullSet(event.getPlayer())) return;
		event.getPlayer().setAllowFlight(true);
		SpeedCommand.resetSpeed(event.getPlayer(), true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!hasFullSet(event.getPlayer())) return;
		event.getPlayer().setAllowFlight(false);
	}

}