package gg.projecteden.nexus.features.mcmmo.resetnew.skills.crossbows.turret;

import dev.geco.gsit.api.event.PreEntitySitEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.KillerMoney.KillerMoneyEarnedEvent;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackMenu.BackpackHolder;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.trust.TrustFeature;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.models.survival.TurretConfigService;
import gg.projecteden.nexus.models.trust.TrustsUser.TrustType;
import gg.projecteden.nexus.models.trust.TrustsUserService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TurretManager implements Listener {

	public static ItemStack ITEM = new ItemBuilder(Material.PAPER)
		.name("&eTurret")
		.model("survival/mcmmoreset/skills/crossbows/turret")
		.build();

	static  {
		Nexus.registerListener(new TurretManager());

		Tasks.repeat(1, 1, () -> {
			TurretConfigService.get().getTurrets().forEach(turret -> {
				if (!turret.getLocation().isChunkLoaded()) return;
				turret.tick();

				if (turret.getTarget() != null) return;

				LivingEntity target = turret.getLocation().toCenterLocation().getNearbyLivingEntities(Turret.RANGE).stream()
					.filter(turret::isValidTarget)
					.min(Comparator.comparing(e -> turret.getLocation().toCenterLocation().distanceSquared(e.getEyeLocation())))
					.orElse(null);
				if (target == null) return;

				turret.setTarget(target);
			});
		});
	}

	@EventHandler
	public void onArrowHitBlock(ProjectileHitEvent event) {
		if (event.getHitBlock() == null) return;
		if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
		PersistentDataContainer pdc = arrow.getPersistentDataContainer();
		if (!pdc.has(Turret.ARROW_KEY)) return;
		event.setCancelled(true);
		arrow.remove();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent event) {
		TurretConfigService.get().getTurrets().stream()
			.filter(turret -> turret.getLocation().equals(event.getBlock().getLocation()))
			.findFirst().ifPresent(turret -> {
				boolean hasOverride = event.getPlayer().hasPermission(Group.SENIOR_STAFF);

				if (!hasOverride && (turret.getOwner() == null || !turret.getOwner().equals(event.getPlayer().getUniqueId()))) {
					event.setCancelled(true);
					return;
				}

				List<ItemStack> arrows = turret.remove();

				event.setDropItems(false);
				event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation().toCenterLocation(), ITEM.clone());
				for (ItemStack arrow : arrows)
					if (Nullables.isNotNullOrAir(arrow))
						event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation().toCenterLocation(), arrow);
			});
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClick(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (!event.getAction().isRightClick()) return;
		if (!ItemUtils.isModelMatch(event.getItem(), ITEM)) return;
		if (event.getBlockFace() != BlockFace.UP) return;

		event.setCancelled(true);
		Location placeLoc = event.getClickedBlock().getLocation().clone().add(0, 1, 0);

		if (!BlockUtils.tryPlaceEvent(event.getPlayer(), placeLoc.getBlock(), event.getClickedBlock(), Material.SMOOTH_STONE_SLAB, event.getItem()))
			return;

		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.getItem().subtract();

		new Turret(placeLoc, event.getPlayer().getUniqueId());
		placeLoc.getBlock().setType(Material.SMOOTH_STONE_SLAB);
		event.getPlayer().swingHand(event.getHand());
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		handle(event);
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		handle(event);
	}

	private void handle(PlayerInteractEntityEvent event) {
		if (TurretConfigService.get().getTurrets().stream().anyMatch(turret -> turret.getItemFrame().equals(event.getRightClicked().getUniqueId()))) {
			event.setCancelled(true);
			return;
		}
		if (TurretConfigService.get().getTurrets().stream().anyMatch(turret -> turret.getArmorStand().equals(event.getRightClicked().getUniqueId())))
			event.setCancelled(true);
	}

	@EventHandler
	public void onKillerMoneyGain(KillerMoneyEarnedEvent event) {
		if (!(event.getDamageSource().getDirectEntity() instanceof Arrow arrow)) return;
		if (!arrow.getPersistentDataContainer().has(Turret.ARROW_KEY)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onSit(PreEntitySitEvent event) {
		if (TurretConfigService.get().getTurrets().stream().anyMatch(turret -> turret.getLocation().equals(event.getBlock().getLocation())))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!(event.getDamageSource().getDirectEntity() instanceof Arrow arrow)) return;
		if (!arrow.getPersistentDataContainer().has(Turret.ARROW_KEY)) return;
		event.setDroppedExp(0);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClickBlock(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (!event.getAction().isRightClick()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		handle(event.getClickedBlock().getLocation(), event.getPlayer(), event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClickArmorStand(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof ArmorStand)) return;
		handle(event.getRightClicked().getLocation().toBlockLocation(), event.getPlayer(), event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClickArmorStand(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof ArmorStand)) return;
		handle(event.getRightClicked().getLocation().toBlockLocation(), event.getPlayer(), event);
	}

	TrustsUserService service = new TrustsUserService();

	private void handle(Location location, Player player, Cancellable event) {
		TurretConfigService.get().getTurrets().stream()
			.filter(turret -> turret.getLocation().equals(location))
			.filter(turret -> {
				if (turret.getOwner() == null) return false;
				if (player.hasPermission("lwc.admin")) return true;
				return service.get(turret.getOwner()).trusts(TrustType.LOCKS, player);
			})
			.findFirst().ifPresent(turret -> {
				event.setCancelled(true);
				new TurretInventory(turret, player);
			});
	}

	private static class TurretInventory implements TemporaryMenuListener {

		private static final Map<Location, TurretInventoryHolder> HOLDERS = new HashMap<>();

		private final Turret turret;
		private final Player player;
		@Getter
		private final TurretInventoryHolder inventoryHolder;

		private TurretInventory(Turret turret, Player player) {
			this.turret = turret;
			this.turret.setInventoryOpen(true);
			this.player = player;

			this.inventoryHolder = HOLDERS.computeIfAbsent(turret.getLocation(), TurretInventoryHolder::new);

			if (inventoryHolder.getInventory() != null)
				open(inventoryHolder.getInventory());
			else
				open(3, ItemUtils.getNBTContentsOfNonInventoryItem(turret.getStand().getEquipment().getHelmet(), 27));
		}

		@Override
		public String getTitle() {
			return "Turret";
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			ItemStack helmet = turret.getStand().getEquipment().getHelmet();
			ItemUtils.setNBTContentsOfNonInventoryItem(helmet, contents);
			turret.getStand().getEquipment().setHelmet(helmet);

			turret.setInventoryOpen(false);
		}

		@Override
		public Player getPlayer() {
			return this.player;
		}

		@Data
		@AllArgsConstructor
		private static class TurretInventoryHolder extends CustomInventoryHolder {
			private final Location location;
		}
	}

}
