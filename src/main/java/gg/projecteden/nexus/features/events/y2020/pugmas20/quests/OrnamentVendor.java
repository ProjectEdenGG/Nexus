package gg.projecteden.nexus.features.events.y2020.pugmas20.quests;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.events.models.QuestStage;
import gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20;
import gg.projecteden.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import gg.projecteden.nexus.features.events.y2020.pugmas20.models.Merchants.MerchantNPC;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.pugmas20.Pugmas20User;
import gg.projecteden.nexus.models.pugmas20.Pugmas20UserService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.Pugmas20TreeRegenJob;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldEditUtils.Paster;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NoArgsConstructor
public class OrnamentVendor implements Listener {
	public enum Ornament {
		RED(PugmasTreeType.BLOODWOOD, -3),
		ORANGE(PugmasTreeType.MAHOGANY, -4),
		YELLOW(PugmasTreeType.EUCALYPTUS, -5),
		GREEN(PugmasTreeType.WILLOW, -6),
		CYAN(PugmasTreeType.CRYSTAL, -7),
		BLUE(PugmasTreeType.MAGIC, -8),
		PURPLE(PugmasTreeType.OAK, -9),
		MAGENTA(PugmasTreeType.TEAK, -10),
		GRAY(PugmasTreeType.MAPLE, -11),
		WHITE(PugmasTreeType.BLISTERWOOD, -12);

		@Getter
		private final PugmasTreeType treeType;
		@Getter
		private final int relative;
		@Getter
		private ItemStack skull;

		public static int logsPerOrnament = 48;

		Ornament(PugmasTreeType treeType, int relative) {
			this.treeType = treeType;
			this.relative = relative;
			loadHead();
			Tasks.wait(TickTime.SECOND, this::loadHead);
		}

		private void loadHead() {
			ItemStack itemStack = AdventMenu.origin.getRelative(relative, 0, 0).getDrops().stream().findFirst().orElse(null);
			if (Nullables.isNullOrAir(itemStack))
				this.skull = null;
			else
				this.skull = Pugmas20.item(itemStack).name(StringUtils.camelCase(name() + " Ornament")).build();
		}

		public static void loadHeads() {
			for (Ornament ornament : Ornament.values())
				ornament.loadHead();
		}

		public static Ornament of(PugmasTreeType treeType) {
			for (Ornament ornament : Ornament.values())
				if (ornament.getTreeType() == treeType)
					return ornament;

			return null;
		}
	}

	public static List<ItemStack> getOrnaments(Player player) {
		List<ItemStack> ornaments = new ArrayList<>();
		for (Ornament ornament : Ornament.values()) {
			if (player.getInventory().containsAtLeast(ornament.getSkull(), 1))
				ornaments.add(ornament.getSkull());
		}

		return ornaments;
	}

	@EventHandler
	public void onItemFrameInteract(PlayerInteractAtEntityEvent event) {
		if (EquipmentSlot.HAND != event.getHand())
			return;

		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player))
			return;

		Entity entity = event.getRightClicked();
		if (entity.getType() != EntityType.ITEM_FRAME)
			return;

		if (!Pugmas20.isAtPugmas(entity.getLocation(), "lumberjacksaxe"))
			return;

		event.setCancelled(true);

		Pugmas20UserService service = new Pugmas20UserService();
		Pugmas20User user = service.get(player);

		if (user.getOrnamentVendorStage() == QuestStage.NOT_STARTED)
			return;

		if (!player.getInventory().contains(getLumberjacksAxe())) {
			if (Quests.hasRoomFor(player, getLumberjacksAxe())) {
				PlayerUtils.giveItem(player, getLumberjacksAxe());
				Quests.sound_obtainItem(player);
				user.sendMessage(Pugmas20.PREFIX + " You have obtained a &3&l" + StringUtils.stripColor(lumberjacksAxe.getItemMeta().getDisplayName()));
			} else {
				Quests.sound_villagerNo(player);
				user.sendMessage(Quests.fullInvError_obtain);
			}
		}
	}

	@Getter
	private static final ItemStack lumberjacksAxe = Pugmas20.questItem(Material.IRON_AXE).name("Lumberjack's Axe").build();

	@EventHandler
	public void onTreeBreak(BlockBreakEvent event) {
		if (!Pugmas20.isAtPugmas(event.getBlock().getLocation(), "trees"))
			return;

		if (!WorldGuardEditCommand.canWorldGuardEdit(event.getPlayer()))
			return;

		event.setCancelled(true);

		if (!ItemUtils.isFuzzyMatch(lumberjacksAxe, event.getPlayer().getInventory().getItemInMainHand()))
			return;

		PugmasTreeType treeType = PugmasTreeType.of(event.getBlock().getType());
		if (treeType == null)
			return;

		Set<ProtectedRegion> regions = Pugmas20.worldguard().getRegionsLike("pugmas20_trees_" + treeType.name() + "_[\\d]+");

		MinMaxResult<ProtectedRegion> result = Utils.getMin(regions, region -> Distance.distance(event.getBlock(), Pugmas20.worldguard().toLocation(region.getMinimumPoint())).get());

		ProtectedRegion region = result.getObject();
		double distance = Math.sqrt(result.getValue().doubleValue());

		if (region == null)
			return;

		int tree = Integer.parseInt(region.getId().split("_")[3]);

		if (tree < 1 || distance > 20)
			return;

		treeType.feller(event.getPlayer(), tree);
	}

	public enum PugmasTreeType {
		BLISTERWOOD(Material.BONE_BLOCK, Material.QUARTZ_SLAB, Material.QUARTZ_STAIRS, Material.HONEY_BLOCK),
		BLOODWOOD(Material.CRIMSON_HYPHAE, Material.BLACK_STAINED_GLASS),
		CRYSTAL(Material.ICE, Material.BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS,
				Material.PURPLE_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS, Material.CYAN_STAINED_GLASS_PANE, Material.SOUL_LANTERN),
		EUCALYPTUS(Material.STRIPPED_BIRCH_WOOD, Material.BIRCH_SLAB, Material.BIRCH_FENCE, Material.BIRCH_LEAVES,
				Material.BIRCH_STAIRS, Material.BIRCH_PLANKS),
		MAGIC(Material.STRIPPED_WARPED_HYPHAE, Material.SPRUCE_LEAVES),
		MAHOGANY(Material.DARK_OAK_WOOD, Material.DARK_OAK_LEAVES, Material.DARK_OAK_SLAB),
		MAPLE(Material.ACACIA_WOOD, Material.FIRE_CORAL_BLOCK, Material.NETHER_WART_BLOCK),
		OAK(Material.OAK_WOOD, Material.OAK_LEAVES),
		TEAK(Material.STRIPPED_OAK_WOOD, Material.JUNGLE_LEAVES, Material.OAK_LEAVES),
		WILLOW(Material.STRIPPED_SPRUCE_WOOD, Material.OAK_LEAVES);

		@Getter
		private final Material logs;
		@Getter
		private final List<Material> others;

		@Getter
		private final Map<Integer, Paster> pasters = new HashMap<>();
		@Getter
		private final Map<Integer, CompletableFuture<Queue<Location>>> queues = new HashMap<>();
		@Getter
		private final Map<Integer, ProtectedRegion> regions = new HashMap<>();

		private static final long animationTime = TickTime.SECOND.x(3);

		PugmasTreeType(Material logs, Material... others) {
			this.logs = logs;
			this.others = Arrays.asList(others);

			Tasks.async(() -> {
				for (int id = 1; id <= 10; id++) {
					if (getRegion(id) == null)
						continue;

					getPaster(id);
					getQueue(id);
				}
			});
		}

		public ItemStack getLog() {
			return getLog(1);
		}

		public ItemStack getLog(int amount) {
			return Pugmas20.questItem(logs).name(StringUtils.camelCase(name() + " Logs")).amount(amount).build();
		}

		public List<Material> getAllMaterials() {
			ArrayList<Material> materials = new ArrayList<>(others);
			materials.add(logs);
			return materials;
		}

		public String getAllMaterialsString() {
			return getAllMaterials().stream().map(material -> material.name().toLowerCase()).collect(Collectors.joining(","));
		}

		public static PugmasTreeType of(Material logs) {
			for (PugmasTreeType treeType : PugmasTreeType.values())
				if (treeType.getLogs() == logs)
					return treeType;

			return null;
		}

		public CompletableFuture<Void> build(int id) {
			Pugmas20.setTreeAnimating(true);
			return getPaster(id).buildQueue().thenRun(() -> Pugmas20.setTreeAnimating(false));
		}

		private CompletableFuture<Queue<Location>> getQueue(int id) {
			return queues.computeIfAbsent(id, $ -> {
				final CompletableFuture<Queue<Location>> future = new CompletableFuture<>();

				ProtectedRegion region = getRegion(id);
				if (region == null)
					return null;

				Location base = Pugmas20.worldedit().toLocation(region.getMinimumPoint());
				Queue<Location> queue = BlockUtils.createDistanceSortedQueue(base);
				getBlocks(id).thenAccept(blocks -> {
					queue.addAll(blocks.keySet());
					future.complete(queue);
				});

				return future;
			});
		}

		private CompletableFuture<Map<Location, BlockData>> getBlocks(int id) {
			return getPaster(id).getComputedBlocks();
		}

		private Paster getPaster(int id) {
			return pasters.computeIfAbsent(id, $ -> {
				ProtectedRegion region = getRegion(id);
				if (region == null)
					return null;

				String schematicName = region.getId().replaceAll("_", "/");
				return Pugmas20.worldedit().paster()
						.air(false)
						.at(region.getMinimumPoint())
						.duration(animationTime)
						.file(schematicName)
						.inspect();
			});
		}

		public ProtectedRegion getRegion(int id) {
			regions.computeIfAbsent(id, $ -> {
				try {
					String regionName = "pugmas20_trees_" + name().toLowerCase() + "_" + id;
					return Pugmas20.worldguard().getProtectedRegion(regionName);
				} catch (InvalidInputException ex) {
					return null;
				}
			});

			return regions.get(id);
		}

		public void feller(Player player, int id) {
			if (!new CooldownService().check(UUIDUtils.UUID0, getRegion(id).getId(), TickTime.SECOND.x(3)))
				return;

			Pugmas20.setTreeAnimating(true);
			Tasks.async(() -> getQueue(id).thenAccept(queueCopy -> {
				Queue<Location> queue = new PriorityQueue<>(queueCopy);

				int wait = 0;
				long blocksPerTick = Math.max(queue.size() / animationTime, 1);

				queueLoop:
				while (true) {
					++wait;
					for (int i = 0; i < blocksPerTick; i++) {
						Location poll = queue.poll();
						if (poll == null)
							break queueLoop;

						Tasks.wait(wait, () -> poll.getBlock().setType(Material.AIR, this != CRYSTAL));
					}
				}

				Tasks.wait(++wait, () -> Pugmas20.setTreeAnimating(false));

				Tasks.Countdown.builder()
					.duration(RandomUtils.randomInt(8, 12) * 4)
					.onTick(i -> {
						if (i % 4 == 0)
							PlayerUtils.giveItem(player, getLog());
					})
					.start();

				Jingle.TREE_FELLER.play(player);

				new Pugmas20TreeRegenJob(this, id).schedule(RandomUtils.randomInt(3 * 60, 5 * 60));
			}));
		}

	}

	// TODO Create merchant trade event
	@EventHandler
	public void onMerchantTrade(InventoryClickEvent event) {
		if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
		if (!Utils.equalsInvViewTitle(event.getView(), StringUtils.camelCase(MerchantNPC.ORNAMENT_VENDOR.name())))
			return;
		if (event.getSlot() != 2) return;

		Player player = (Player) event.getWhoClicked();
		if (!Pugmas20.isAtPugmas(player)) return;

		if (Arrays.asList(ClickType.DROP, ClickType.CONTROL_DROP).contains(event.getClick())) {
			event.setCancelled(true);
			return;
		}

		if (event.getHotbarButton() > 0)
			if (!Nullables.isNullOrAir(player.getInventory().getItem(event.getHotbarButton())))
				return;

		ItemStack result = event.getCurrentItem();
		if (Nullables.isNullOrAir(result))
			return;
		if (result.getType() != Material.PLAYER_HEAD)
			return;

		ItemStack source = event.getInventory().getItem(0);
		if (Nullables.isNullOrAir(source))
			source = event.getInventory().getItem(1);
		if (Nullables.isNullOrAir(source))
			return;

		PugmasTreeType treeType = PugmasTreeType.of(source.getType());
		if (treeType == null)
			return;

		Ornament ornament = Ornament.of(treeType);
		if (ornament == null)
			return;

		int resultAmount = 1;
		if (event.isShiftClick())
			resultAmount = source.getAmount() / Ornament.logsPerOrnament;

		Pugmas20UserService service = new Pugmas20UserService();
		Pugmas20User user = service.get(player);

		user.getOrnamentTradeCount().put(ornament, user.getOrnamentTradeCount().getOrDefault(ornament, 0) + resultAmount);

		boolean done = true;
		for (Ornament _ornament : Ornament.values())
			if (user.canTradeOrnament(_ornament))
				done = false;
		if (done)
			user.getNextStepNPCs().remove(MerchantNPC.ORNAMENT_VENDOR.getNpcId());

		service.save(user);
	}

}
