package gg.projecteden.nexus.features.mcmmo.mastery.skills.tridents;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PoseidonsTrident implements Listener {

	public static final ItemStack ITEM = new ItemBuilder(Material.TRIDENT)
		.model(ItemModelType.POSEIDONS_TRIDENT)
		.name("&ePoseidon's Trident")
		.lore("&eShift Right Click &3to invoke Poseidon's blessing")
		.lore("&3Grants Conduit Power for 60 seconds")
		.lore("&3Pacifies Guardians and prevents mining Fatigue")
		.build();

	private static final long DURATION_TICKS = TickTime.MINUTE.get();
	private final Set<UUID> blessings = new HashSet<>();

	private boolean isPoseidonsTrident(ItemStack item) {
		return ItemUtils.isModelMatch(item, ITEM);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onActivate(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if (isPoseidonsTrident(player.getInventory().getItemInMainHand())) {
			event.setUseInteractedBlock(Event.Result.DENY);
			event.setUseItemInHand(Event.Result.DENY);

			if (event.getHand() == EquipmentSlot.HAND)
				activate(player);
		} else if (event.getHand() == EquipmentSlot.OFF_HAND && isPoseidonsTrident(event.getItem())) {
			event.setUseInteractedBlock(Event.Result.DENY);
			event.setUseItemInHand(Event.Result.DENY);
			activate(player);
		}
	}

	private void activate(Player player) {
		UUID id = player.getUniqueId();
		if (blessings.contains(id))
			return;

		blessings.add(id);

		player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
		player.addPotionEffect(new PotionEffect(
			PotionEffectType.CONDUIT_POWER,
			(int) DURATION_TICKS,
			0,
			true,
			false,
			true
		));

		for (var entity : player.getNearbyEntities(64, 64, 64)) {
			if (entity instanceof Guardian guardian && player.equals(guardian.getTarget()))
				guardian.setTarget(null);
		}

		Tasks.wait(DURATION_TICKS, () -> blessings.remove(id));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGuardianTarget(EntityTargetLivingEntityEvent event) {
		if (!(event.getEntity() instanceof Guardian)) return;
		if (!(event.getTarget() instanceof Player player)) return;
		if (!blessings.contains(player.getUniqueId())) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMiningFatigue(EntityPotionEffectEvent event) {
		PotionEffect effect = event.getNewEffect();
		if (effect == null) return;
		if (!effect.getType().equals(PotionEffectType.MINING_FATIGUE)) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!blessings.contains(player.getUniqueId())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		clearBlessing(event.getPlayer());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		blessings.remove(event.getEntity().getUniqueId());
	}

	private void clearBlessing(Player player) {
		if (!blessings.contains(player.getUniqueId())) return;
		blessings.remove(player.getUniqueId());
		player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
	}

	static {
		Nexus.registerListener(new PoseidonsTrident());
	}

}
