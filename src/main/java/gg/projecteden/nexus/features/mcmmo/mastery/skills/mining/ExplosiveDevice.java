package gg.projecteden.nexus.features.mcmmo.mastery.skills.mining;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.blockmechanics.BlockMechanicUtils;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.Offset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ExplosiveDevice implements Listener {

	private static final NamespacedKey TNT_KEY = new NamespacedKey(Nexus.getInstance(), "ExplosiveDeviceTNT");

	public static final ItemStack ITEM = setMode(new ItemBuilder(ItemModelType.EXPLOSIVE_DEVICE)
		.maxStackSize(1)
		.name("&eExplosive Device").build(), Mode.SINGLE);

	public static ItemStack setMode(ItemStack itemStack, Mode mode) {
		return new ItemBuilder(itemStack, true)
			.nbt(nbt -> nbt.setString("mode", mode.name().toLowerCase()))
			.resetLore()
			.lore("&3Mode: &e" + StringUtils.camelCase(mode))
			.build();
	}

	public static Mode getMode(ItemStack itemStack) {
		AtomicReference<String> mode = new AtomicReference<>();
		new ItemBuilder(itemStack)
			.nbt(nbt -> mode.set(nbt.getString("mode")));
		return Mode.valueOf(mode.get().toUpperCase());
	}

	public static ItemStack setFuel(ItemStack itemStack, int amount) {
		return new ItemBuilder(itemStack, true)
			.nbt(nbt -> nbt.setInteger("fuel", amount))
			.build();
	}

	public static int getFuel(ItemStack itemStack) {
		AtomicInteger fuel = new AtomicInteger();
		new ItemBuilder(itemStack)
			.nbt(nbt -> {
				if (!nbt.hasTag("fuel"))
					fuel.set(0);
				else
					fuel.set(nbt.getInteger("fuel"));
			});
		return fuel.get();
	}

	static {
		new ExplosiveDevice();
	}

	private boolean stopping = false;

	public ExplosiveDevice() {
		Nexus.registerListener(this);

		Tasks.repeat(1, 1, () -> {
			if (stopping) return;

			for (Player player : OnlinePlayers.where(player -> WorldGroup.of(player) == WorldGroup.SURVIVAL).get()) {
				if (CooldownService.isOnCooldown(player.getUniqueId(), "explosive-device", TickTime.SECOND.x(1), false))
					continue;

				if (!ItemUtils.isModelMatch(player.getInventory().getItemInMainHand(), ITEM))
					unrender(player);
				else {
					ExplosiveDeviceRender render = getRender(player);
					if (render == null)
						render(player);
					else {
						Block block = player.getTargetBlockExact(4, FluidCollisionMode.NEVER);
						if (block == null) {
							unrender(player);
							continue;
						}
						BlockFace blockFace = player.getTargetBlockFace(4, FluidCollisionMode.NEVER);
						if (blockFace == null) {
							unrender(player);
							continue;
						}

						Location loc = block.getRelative(blockFace).getLocation();

						if (render.base != null && loc.toCenterLocation().equals(render.base.toCenterLocation()))
							continue;

						render(player);
					}
				}
			}
		});
	}

	private final List<ExplosiveDeviceRender> renders = new ArrayList<>();

	private ExplosiveDeviceRender getRender(Player player) {
		for (ExplosiveDeviceRender render : renders)
			if (render.player.getUniqueId().equals(player.getUniqueId()))
				return render;
		return null;
	}

	public void render(Player player) {
		unrender(player);
		ExplosiveDeviceRender render = ExplosiveDeviceRender.of(player);
		if (render != null)
			renders.add(render);
	}

	private void unrender(Player player) {
		ExplosiveDeviceRender render = getRender(player);
		if (render == null) return;
		render.unrender();
		renders.remove(render);
	}

	private void place(Player player) {
		ExplosiveDeviceRender render = getRender(player);
		if (render == null) return;
		render.place(getMode(player.getInventory().getItemInMainHand()));
		CooldownService.isOnCooldown(player.getUniqueId(), "explosive-device", TickTime.SECOND.x(1), true);
		render.unrender();
		renders.remove(render);

		ItemStack item = player.getInventory().getItemInMainHand();
		setFuel(item, getFuel(item) - render.holograms.size());
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick()) return;
		if (event.getPlayer().isSneaking()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!ItemUtils.isModelMatch(event.getPlayer().getInventory().getItemInMainHand(), ITEM)) return;
		Player player = event.getPlayer();
		int fuel = getFuel(player.getInventory().getItemInMainHand());

		new ExplosiveDeviceFuelMenu(fuel, player).open();
	}

	@EventHandler
	public void onShiftRightClick(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick()) return;
		if (!event.getPlayer().isSneaking()) return;
		if (!ItemUtils.isModelMatch(event.getPlayer().getInventory().getItemInMainHand(), ITEM)) return;

		place(event.getPlayer());
	}

	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (!event.getAction().isLeftClick()) return;
		if (event.getPlayer().isSneaking()) return;
		if (!ItemUtils.isModelMatch(event.getPlayer().getInventory().getItemInMainHand(), ITEM)) return;

		Mode mode = getMode(event.getPlayer().getInventory().getItemInMainHand());
		mode = EnumUtils.nextWithLoop(Mode.class, mode.ordinal());

		setMode(event.getPlayer().getInventory().getItemInMainHand(), mode);
	}

	@EventHandler
	public void onShutDown(PluginDisableEvent event) {
		if (!event.getPlugin().equals(Nexus.getInstance())) return;
		this.stopping = true;

		for (ExplosiveDeviceRender render : new ArrayList<>(renders))
			unrender(render.player);
	}

	@EventHandler(ignoreCancelled = true)
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		if (!(event.getEntity() instanceof TNTPrimed tnt)) return;
		if (!Boolean.TRUE.equals(tnt.getPersistentDataContainer().get(TNT_KEY, PersistentDataType.BOOLEAN))) return;

		if (!isEnclosed(tnt.getLocation().getBlock())) return;

		event.setRadius(event.getRadius() * 4.5f);
	}

	private static boolean isEnclosed(Block center) {
		BlockFace[] sides = {
			BlockFace.NORTH, BlockFace.SOUTH,
			BlockFace.EAST, BlockFace.WEST,
			BlockFace.UP, BlockFace.DOWN
		};

		int coveredFaces = 0;
		for (BlockFace side : sides) {
			Block neighbor = center.getRelative(side);
			if (neighbor.getBlockData().isFaceSturdy(
				side.getOppositeFace(), BlockSupport.FULL)) {
				coveredFaces++;
			}
		}

		return coveredFaces >= 5;
	}

	@Data
	@AllArgsConstructor
	private static class ExplosiveDeviceFuelMenu implements TemporaryListener {

		private final int fuel;
		private final Player player;

		public void open() {
			Nexus.registerTemporaryListener(this);
			Inventory inventory = Bukkit.createInventory(null, 27, "Explosive Device");

			int fullSlots = fuel / 64;
			int partial = fuel % 64;

			for (int i = 0; i < fullSlots; i++)
				inventory.setItem(i, new ItemBuilder(Material.GUNPOWDER).amount(64).build());

			inventory.setItem(fullSlots, new ItemBuilder(Material.GUNPOWDER).amount(partial).build());

			player.openInventory(inventory);
		}

		@EventHandler
		public void onClick(InventoryClickEvent event) {
			if (event.getClickedInventory() == null) return;
			if (event.getSlot() < 0) return;
			if (event.getClickedInventory().getItem(event.getSlot()) == null) return;
			if (event.getClickedInventory().getItem(event.getSlot()).getType() != Material.GUNPOWDER)
				event.setCancelled(true);
		}

		@EventHandler
		public void onClose(InventoryCloseEvent event) {
			if (!event.getPlayer().equals(player)) return;
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!ItemUtils.isModelMatch(item, ITEM)) return;

			int amount = 0;
			for (int i = 0; i < 27; i++) {
				ItemStack _item = event.getInventory().getItem(i);
				if (_item == null) continue;
				if (_item.getType() == Material.GUNPOWDER)
					amount += _item.getAmount();
			}
			setFuel(item, amount);

			Nexus.unregisterTemporaryListener(this);
		}
	}

	@Data
	private static class ExplosiveDeviceRender {
		Player player;
		Location base;
		List<Hologram> holograms = new ArrayList<>();
		boolean success = false;

		public ExplosiveDeviceRender(Player player, Location base) {
			this.player = player;
			this.base = base;
		}

		private static ExplosiveDeviceRender of(Player player) {
			Block block = player.getTargetBlockExact(4, FluidCollisionMode.NEVER);
			if (block == null)
				return null;
			BlockFace blockFace = player.getTargetBlockFace(4, FluidCollisionMode.NEVER);
			if (blockFace == null)
				return null;

			Location loc = block.getRelative(blockFace).getLocation();

			ExplosiveDeviceRender render = new ExplosiveDeviceRender(player, loc);

			ItemStack item = player.getInventory().getItemInMainHand();
			Mode mode = getMode(item);

			List<Location> locations = new ArrayList<>() {{ add(loc); }} ;
			locations.addAll(mode.getAdditionalLocations(loc));

			int fuel = getFuel(item);
			if (fuel < locations.size()) {
				Hologram holo = HologramsAPI.builder()
					.location(loc.toCenterLocation())
					.lines("&cNot enough Gunpowder", "&cRequires " + locations.size(), Offset.of(.25f), new ItemBuilder(ItemModelType.EXPLOSIVE_DEVICE_DENIED).build())
					.itemTransform(ItemDisplayTransform.FIXED)
					.build();
				holo.getLines().get(3).setBillboard(Billboard.FIXED);
				holo.spawn();

				render.holograms = List.of(holo);
				render.success = false;
				return render;
			}

			boolean canBuild = true;
			for (Location l : locations) {
				if (!BlockMechanicUtils.canBuild(player, l.getBlock(), true)) {
					canBuild = false;
					break;
				}
			}

			if (!canBuild) {
				Hologram holo = HologramsAPI.builder()
					.location(loc.toCenterLocation())
					.lines("&cArea is Protected", Offset.of(.25f), new ItemBuilder(ItemModelType.EXPLOSIVE_DEVICE_DENIED).build())
					.itemTransform(ItemDisplayTransform.FIXED)
					.build();
				holo.getLines().get(2).setBillboard(Billboard.FIXED);
				holo.spawn();

				render.holograms = List.of(holo);
				render.success = false;
				return render;
			}

			for (Location l : locations) {
				Hologram holo = HologramsAPI.builder()
					.location(l.toCenterLocation())
					.lines(new ItemBuilder(ItemModelType.EXPLOSIVE_DEVICE_SUCCESS).build())
					.billboard(Billboard.FIXED)
					.itemTransform(ItemDisplayTransform.FIXED)
					.build();
				holo.spawn();
				render.holograms.add(holo);
			}
			render.success = true;

			return render;
		}

		private void unrender() {
			if (this.holograms != null)
				for (Hologram holo : this.holograms)
					holo.remove();
		}

		private void place(Mode mode) {
			if (this.holograms == null)
				return;
			if (!this.success)
				return;

			for (int i = 0; i < this.holograms.size(); i++) {
				Hologram holo = this.holograms.get(i);
				Location loc = holo.getLocation();
				holo.remove();
				int finalI = i;
				TNTPrimed tntPrimed = loc.getWorld().spawn(loc.toCenterLocation(), TNTPrimed.class, tnt -> {
					tnt.setSource(player);
					tnt.setFuseTicks((int) (tnt.getFuseTicks() * 1.5f) + (finalI * mode.getOffset()));
					tnt.getPersistentDataContainer().set(TNT_KEY, PersistentDataType.BOOLEAN, true);
				});
				Tasks.wait(tntPrimed.getFuseTicks() - 1, () -> tntPrimed.getLocation().getBlock().setType(Material.AIR));
			}
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ExplosiveDeviceRender that)) return false;
			return Objects.equals(player.getUniqueId(), that.player.getUniqueId());
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(player.getUniqueId());
		}
	}

	public enum Mode {
		SINGLE {
			@Override
			public List<Location> getAdditionalLocations(Location base) {
				return List.of();
			}
		},
		// 5 down
		PILLAR {
			@Override
			public List<Location> getAdditionalLocations(Location base) {
				List<Location> locations = new ArrayList<>();
				for (int i = 0; i < 4; i++)
					locations.add(base.clone().add(0, (i + 1) * -3, 0));
				return locations;
			}

			@Override
			public int getOffset() {
				return 3;
			}
		},
		// x shape (5)
		X {
			@Override
			public List<Location> getAdditionalLocations(Location base) {
				return List.of();
			}
		},
		// 5 forward
		TUNNEL {
			@Override
			public List<Location> getAdditionalLocations(Location base) {
				return List.of();
			}
		};

		public abstract List<Location> getAdditionalLocations(Location base);

		public int getOffset() { return 0; }
	}

}
