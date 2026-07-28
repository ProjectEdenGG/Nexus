package gg.projecteden.nexus.features.mcmmo.resetnew.skills.archery;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.enchants.DoubleTapEnchant;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.IBackpack;
import gg.projecteden.nexus.utils.ArrowSnapshot;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.InventoryUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Quiver implements IBackpack, Listener {

	private static final ItemStack item = new ItemBuilder(Material.PAPER).model("survival/mcmmoreset/skills/archery/quiver").maxStackSize(1).name("&eQuiver").build();

	public static ItemStack get() {
		return item;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	public static void checkEmpty(ItemStack backpack, List<ItemStack> contents) {
		if (contents.stream().noneMatch(Nullables::isNotNullOrAir) && backpack.getType() == Material.SPECTRAL_ARROW)
			backpack.setType(Material.PAPER);
		else if (contents.stream().anyMatch(Nullables::isNotNullOrAir) && backpack.getType() == Material.PAPER)
			backpack.setType(Material.SPECTRAL_ARROW);
	}

	static {
		Nexus.registerListener(new Quiver());
	}

	// This approach is stupid... but paper is stupider so it has to be done
	// Bows take the arrow _before_ the EntityShootBowEvent is called
	// I have to get it from the ready event to know what specific slot they are pulling from
	private static final Map<UUID, Integer> QUIVER_SLOT_MAP = new HashMap<>();

	@EventHandler
	public void onPrepare(PlayerReadyArrowEvent event) {
		Player player = event.getPlayer();
		int slot = -1;
		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack item = player.getInventory().getContents()[i];
			if (Nullables.isNullOrAir(item)) continue;
			if (ItemUtils.isModelMatch(item, event.getArrow(), false)) {
				List<ItemStack> itemContents = ItemUtils.getNBTContentsOfNonInventoryItem(item, 9);
				List<ItemStack> consumableContents = ItemUtils.getNBTContentsOfNonInventoryItem(event.getArrow(), 9);
				if (Nullables.isNullOrEmpty(itemContents) || Nullables.isNullOrEmpty(consumableContents))
					continue;

				if (Objects.equals(itemContents, consumableContents))
					slot = i;
			}
		}
		if (slot == -1) return;
		QUIVER_SLOT_MAP.put(player.getUniqueId(), slot);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onShootArrow(EntityShootBowEvent event) {
		if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
		if (!(arrow.getShooter() instanceof Player player)) return;
		if (event.getBow() == null || event.getBow().getType() != Material.BOW) return;
		if (event.getBow().getEnchantments().containsKey(Enchantment.INFINITY)) return;
		if (!ItemUtils.isModelMatch(event.getConsumable(), getItem(), false)) return;

		ArrowSnapshot snapshot = ArrowSnapshot.of(arrow);

		int slot = QUIVER_SLOT_MAP.getOrDefault(player.getUniqueId(), -1);
		if (slot == -1) return;

		ItemStack arrowItemToShoot = retrieveArrow(event.getConsumable());
		if (arrowItemToShoot == null) return; // this shouldn't happen at this point... but just in case

		Class<? extends AbstractArrow> arrowClass = arrowItemToShoot.getType() == Material.SPECTRAL_ARROW ? SpectralArrow.class : Arrow.class;
		AbstractArrow spawnArrow = snapshot.world().spawnArrow(
			arrow.getLocation(),
			arrow.getVelocity(),
			snapshot.speed(),
			1,
			arrowClass
		);
		arrow.remove();

		spawnArrow.setShooter(snapshot.shooter());
		spawnArrow.setDamage(snapshot.damage());
		spawnArrow.setCritical(snapshot.critical());
		spawnArrow.setPierceLevel(snapshot.pierceLevel());
		spawnArrow.setFireTicks(snapshot.fireTicks());
		spawnArrow.setGravity(snapshot.gravity());
		spawnArrow.setItemStack(arrowItemToShoot.clone());
		spawnArrow.setPickupStatus(event.getBow().getEnchantmentLevel(Enchantment.INFINITY) == 1 ? PickupStatus.DISALLOWED : PickupStatus.ALLOWED);
		spawnArrow.setWeapon(snapshot.weapon());

		event.setProjectile(spawnArrow);
		player.getInventory().setItem(slot, event.getConsumable());
	}

	@EventHandler(ignoreCancelled = true)
	public void onLoadCrossbow(EntityLoadCrossbowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		ItemStack quiver = findQuiver(player);
		if (Nullables.isNullOrAir(quiver)) return;

		ItemStack arrow = retrieveArrow(quiver);
		if (arrow == null) return;

		event.setCancelled(true);

		CrossbowMeta meta = (CrossbowMeta) event.getCrossbow().getItemMeta();
		meta.addChargedProjectile(arrow);

		int multishotLevel = event.getCrossbow().getEnchantmentLevel(Enchantment.MULTISHOT);
		for (int i = 0; i < multishotLevel; i++) {
			meta.addChargedProjectile(arrow);
			meta.addChargedProjectile(arrow);
		}

		event.getCrossbow().setItemMeta(meta);

		if (event.getCrossbow().getEnchantmentLevel(Enchant.DOUBLE_TAP) <= 0) return;

		DoubleTapEnchant.loadAdditionalProjectiles(player, event.getHand());
	}

	private ItemStack findQuiver(Player player) {
		for (ItemStack item : player.getInventory().getContents())
			if (ItemUtils.isModelMatch(item, getItem(), false))
				return item;
		return null;
	}

	public static @Nullable ItemStack retrieveArrow(ItemStack quiver) {
		List<ItemStack> quiverContents = ItemUtils.getNBTContentsOfNonInventoryItem(quiver, 9);
		if (quiverContents.stream().noneMatch(Nullables::isNotNullOrAir)) return null;

		ItemStack item = ItemBuilder.oneOf(quiverContents.getFirst()).build();
		quiverContents.getFirst().subtract();

		ItemUtils.setNBTContentsOfNonInventoryItem(quiver, quiverContents);

		checkEmpty(quiver, quiverContents);

		return item;
	}

}
