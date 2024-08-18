package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.HealCommand;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24.Pugmas24DeathCause;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Pugmas24SlotMachine.Pugmas24SlotMachineReward.Pugmas24SlotMachineRewardEnchant;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Pugmas24SlotMachine.Pugmas24SlotMachineReward.Pugmas24SlotMachineRewardType;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Pugmas24SlotMachine.SlotMachineColumn.SlotMachineColumnStatus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItem;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.DecoratedPot.Side;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Powerable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/*
	TODO:
		- BALANCE CHECK + WITHDRAW ON ROLL
		- REWARDS
 */
public class Pugmas24SlotMachine implements Listener {
	private static final Pugmas24 PUGMAS = Pugmas24.get();
	private static final WorldGuardUtils worldguard = PUGMAS.worldguard();
	private static final WorldEditUtils worldedit = PUGMAS.worldedit();

	private static final String REGION = PUGMAS.getRegionName() + "_slotmachine_";
	private static final String POTS_REGION = REGION + "pots";
	private static final String LEVER_REGION = REGION + "lever";
	private static final String SOUNDS_REGION = REGION + "sound";
	private static final String ROW_REGION = REGION + "row";
	private static final String PLAY_REGION = REGION + "play";

	private static int WIDTH;
	private static int HEIGHT;
	private static SlotMachineAxis AXIS;
	private static boolean REVERSED = true;
	private static final List<Location> winningRow = new ArrayList<>();
	private static final Map<Integer, SlotMachineColumn> columns = new HashMap<>();

	@Getter
	private static boolean playing = false;
	private static Player gamer;
	private static int gameTaskId = -1;
	@Getter
	private static Location soundLocation;
	private static Block lever;

	public Pugmas24SlotMachine() {
		Nexus.registerListener(this);
		init();
	}

	public static void init() {
		columns.clear();

		ProtectedRegion rowRg = worldguard.getProtectedRegion(ROW_REGION);
		worldedit.getBlocks(rowRg).forEach(block -> {
			if (block.getState() instanceof DecoratedPot)
				winningRow.add(block.getLocation());
		});

		soundLocation = worldedit.getBlocks(worldguard.getRegion(SOUNDS_REGION)).getFirst().getLocation();

		ProtectedRegion potsRg = worldguard.getProtectedRegion(POTS_REGION);
		BlockVector3 potsMin = potsRg.getMinimumPoint();
		BlockVector3 potsMax = potsRg.getMaximumPoint();

		HEIGHT = (potsMax.getBlockY() - potsMin.getBlockY()) + 1;
		WIDTH = (potsMax.getBlockX() - potsMin.getBlockX()) + 1;
		AXIS = SlotMachineAxis.X;

		if (potsMin.getBlockX() == potsMax.getBlockX()) {
			WIDTH = (potsMax.getBlockZ() - potsMin.getBlockZ()) + 1;
			AXIS = SlotMachineAxis.Z;
		}

		for (int horizontal = 0; horizontal < WIDTH; horizontal++) {
			int x = potsMin.getBlockX();
			int z = potsMin.getBlockZ();

			if (AXIS == SlotMachineAxis.X)
				x += horizontal;
			else
				z += horizontal;

			List<DecoratedPot> pots = new ArrayList<>();
			for (int vertical = 0; vertical < HEIGHT; vertical++) {
				int y = potsMin.getBlockY();
				y += vertical;

				Location loc = PUGMAS.location(x, y, z);

				Block block = loc.getBlock();
				if (!(block.getState() instanceof DecoratedPot decoratedPot))
					continue;

				pots.add(decoratedPot);
			}

			int columnIndex = horizontal;
			if (REVERSED) {
				columnIndex = ((WIDTH - 1) - columnIndex);
			}

			SlotMachineColumn column = new SlotMachineColumn(columnIndex, pots);
			columns.put(columnIndex, column);
		}
	}

	public static void reset() {
		Tasks.cancel(gameTaskId);
		slowingColumnSkips = 0;
		for (SlotMachineColumn slotColumn : columns.values()) {
			slotColumn.setStatus(SlotMachineColumnStatus.INACTIVE);
		}

		if (lever != null && lever.getBlockData() instanceof Powerable powerable) {
			powerable.setPowered(false);
			lever.setBlockData(powerable, true);
			new SoundBuilder(Sound.BLOCK_LEVER_CLICK).location(lever).volume(0.3).pitch(0.5).play();
		}
		//
		gameTicks = 0;
		gamer = null;
		playing = false;
	}

	private static List<Material> rollSherds;
	private static int rollSherdsSize;
	private static long gameTicks;
	private static final long UPDATE_INTERVAL = TimeUtils.TickTime.TICK.x(4);

	public static void start(Player player) {
		Pugmas24 pugmas = PUGMAS;
		if (Nexus.isMaintenanceQueued()) {
			pugmas.send(player, "&cServer maintenance is queued, try again later");
			return;
		}

		if (playing) {
			pugmas.send(player, "&cThe slot machine is already being rolled");
			return;
		}

		// TODO: BALANCE CHECK & WITHDRAW

		playing = true;
		gamer = player;
		gameTicks = 0;
		rollSherds = new ArrayList<>(Pugmas24SlotMachineReward.getAllSherds());
		rollSherdsSize = rollSherds.size();
		Collections.shuffle(rollSherds);
		slowingColumnSkips = 0;

		// Roll
		for (SlotMachineColumn slotColumn : columns.values()) {
			slotColumn.setStatus(SlotMachineColumnStatus.RUNNING);
		}

		gameTaskId = Tasks.repeat(0, UPDATE_INTERVAL, () -> {
			update();
			gameTicks += UPDATE_INTERVAL;
		});

	}

	private static int slowingColumnSkips = 0;
	private static void update() {
		for (int column = 0; column < WIDTH; column++) {
			SlotMachineColumn slotColumn = columns.get(column);
			SlotMachineColumnStatus columnStatus = slotColumn.getStatus();

			if (columnStatus == SlotMachineColumnStatus.INACTIVE || columnStatus == SlotMachineColumnStatus.STOPPED)
				continue;

			if (columnStatus == SlotMachineColumnStatus.SLOWING) {
				if (gameTicks % (UPDATE_INTERVAL * 2) != 0) {
					slowingColumnSkips++;
					continue;
				}
			}

			int columnOffset = ((int) (((gameTicks / UPDATE_INTERVAL + column * HEIGHT) - slowingColumnSkips) % rollSherdsSize));
			for (int row = 0; row < HEIGHT; row++) {
				int sherdIndex = ((columnOffset + row) % rollSherdsSize);
				Material sherd = rollSherds.get(sherdIndex);
				setSides(slotColumn.getPots().get(row), sherd);
			}

			if (slotColumn.isAbleToStop()) {
				if (slotColumn.canSlow()) {
					slotColumn.setStatus(SlotMachineColumnStatus.SLOWING);
					slotColumn.playSound(soundLocation);
					continue;
				}

				if (slotColumn.canStop()) {
					slotColumn.setStatus(SlotMachineColumnStatus.STOPPED);
					slotColumn.playSound(soundLocation);
					slotColumn.setStoppedTick(gameTicks);

					slowingColumnSkips = 0;

					if (slotColumn.getColumnIndex() == (columns.size() - 1)) {
						end();
						return;
					}
				}
			}

			slotColumn.playSound(soundLocation);
		}
	}

	private static void end() {
		Tasks.cancel(gameTaskId);

		Map<Material, Integer> winningSherdMap = new HashMap<>();
		for (Location potLoc : winningRow) {
			if (!(potLoc.getBlock().getState() instanceof DecoratedPot decoratedPot))
				continue;

			Material sherd = decoratedPot.getSherds().values().stream().toList().getFirst();
			int count = winningSherdMap.getOrDefault(sherd, 0);
			count++;
			winningSherdMap.put(sherd, count);
		}

		int fullCount = winningRow.size();
		int halfCount = (int) Math.ceil(fullCount / 2.0);
		Pugmas24SlotMachineReward reward = null;
		Pugmas24SlotMachineRewardType rewardType = null;
		for (Material material : winningSherdMap.keySet()) {
			int count = winningSherdMap.get(material);
			if (count < halfCount)
				continue;

			if (count == halfCount) {
				reward = Pugmas24SlotMachineReward.of(material);
				rewardType = Pugmas24SlotMachineReward.Pugmas24SlotMachineRewardType.HALF;
				break;
			} else if (count == fullCount) {
				reward = Pugmas24SlotMachineReward.of(material);
				rewardType = Pugmas24SlotMachineReward.Pugmas24SlotMachineRewardType.FULL;
				break;
			}
		}

		if (reward != null) {
			reward.give(gamer, rewardType);
		} else {
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).location(soundLocation).volume(0.5).play();
		}

		reset();
	}

	private static List<Location> LIGHTS = new ArrayList<>(List.of(
		PUGMAS.location(-733, 85, -2904),
		PUGMAS.location(-735, 86, -2904),
		PUGMAS.location(-737, 85, -2904)));

	private static final List<Location> activatedLights = new ArrayList<>();
	private static int loopCounter = 0;
	private static boolean transitioning = false;
	public static void nextLight() {
		List<Location> nextLights = new ArrayList<>(LIGHTS);

		if (transitioning) {
			transitioning = false;
			LIGHTS.forEach(location -> setPowerable(location, false));
			return;
		}

		if (!activatedLights.isEmpty()) {
			Location previousLightLoc = activatedLights.getLast();
			setPowerable(previousLightLoc, false);
		}

		if (loopCounter >= 3) {
			loopCounter = 0;
			transitioning = true;
			LIGHTS.forEach(location -> setPowerable(location, true));
			LIGHTS = LIGHTS.reversed();
			activatedLights.clear();
			return;
		}

		nextLights.removeAll(activatedLights);

		if (nextLights.isEmpty()) {
			activatedLights.clear();
			loopCounter++;
			if (loopCounter >= 3)
				return;

			nextLights = new ArrayList<>(LIGHTS);
		}

		Location nextLightLoc = nextLights.getFirst();
		setPowerable(nextLightLoc, true);
		activatedLights.add(nextLightLoc);
	}

	private static void setPowerable(Location location, boolean lit) {
		Block light = location.getBlock();
		Lightable lightable = (Lightable) light.getBlockData();
		lightable.setLit(lit);
		light.setBlockData(lightable, false);
	}

	//

	private enum SlotMachineAxis {
		X, Z
	}


	@AllArgsConstructor
	public enum Pugmas24SlotMachineReward {
		JACKPOT(SlotPos.of(1, 2), new ItemBuilder(Material.PRIZE_POTTERY_SHERD).name("&bTODO")
			.lore("&3Half: &eTODO", "&3Full: &eTODO"),
			(player) -> {
				PUGMAS.send(player, "TODO HALF REWARD - JACKPOT");
			},
			(player) -> {
				PUGMAS.send(player, "TODO FULL REWARD - JACKPOT");
			}
		),

		HEARTS(SlotPos.of(2, 2), new ItemBuilder(Material.HEART_POTTERY_SHERD).name("&bHeart Crystals")
			.lore("&3Half: &a2 &eHeart Crystals", "&3Full: &a5 &eHeart Crystals"),
			(player) -> PUGMAS.give(player, Pugmas24QuestItem.HEART_CRYSTAL.getItemBuilder().amount(2)),
			(player) -> PUGMAS.give(player, Pugmas24QuestItem.HEART_CRYSTAL.getItemBuilder().amount(5))
		),

		COINS(SlotPos.of(3, 2), new ItemBuilder(Material.ARMS_UP_POTTERY_SHERD).name("&bCoins")
			.lore("&3Half: &eTODO", "&3Full: &eTODO"),
			(player) -> {
				PUGMAS.send(player, "TODO HALF REWARD - CURRENCY");
			},
			(player) -> {
				PUGMAS.send(player, "TODO FULL REWARD - CURRENCY");
			}
		),

		PICKAXE(SlotPos.of(1, 4), new ItemBuilder(Material.MINER_POTTERY_SHERD).name("&dRandom Pickaxe Enchant")
			.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
			(player) -> {
				PUGMAS.send(player, "TODO HALF REWARD - PICKAXE");
			},
			(player) -> {
				PUGMAS.send(player, "TODO FULL REWARD - PICKAXE");
			}
		),

		FISHING_ROD(SlotPos.of(2, 4), new ItemBuilder(Material.ANGLER_POTTERY_SHERD).name("&dRandom Fishing Rod Enchant")
			.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
			(player) -> {
				PUGMAS.send(player, "TODO HALF REWARD - FISHING_ROD");
			},
			(player) -> {
				PUGMAS.send(player, "TODO FULL REWARD - FISHING_ROD");
			}
		),

		SWORD(SlotPos.of(3, 4), new ItemBuilder(Material.BLADE_POTTERY_SHERD).name("&dRandom Sword Enchant")
			.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
			(player) -> {
				PUGMAS.send(player, "TODO HALF REWARD - SWORD");
			},
			(player) -> {
				PUGMAS.send(player, "TODO FULL REWARD - SWORD");
			}
		),

		INSTANT_DEATH(SlotPos.of(1, 6), new ItemBuilder(Material.DANGER_POTTERY_SHERD).name("&cInstant Death")
			.lore("&3Half: &aHealth &3set to &c50%", "&3Full: &aHealth &3set to &c0%"),
			(player) -> player.setHealth(player.getHealth() / 2),
			(player) -> PUGMAS.onDeath(player, Pugmas24DeathCause.INSTANT_DEATH)
		),

		HALF_MAX_HEALTH(SlotPos.of(2, 6), new ItemBuilder(Material.HEARTBREAK_POTTERY_SHERD).name("&cHalve Max Health")
			.lore("&3Half: &aMax Health &3set to &c75%", "&3Full: &aMax Health &3set to &c50%"),
			(player) -> PUGMAS.setMaxHealth(player, HealCommand.getMaxHealth(player) * 0.75),
			(player) -> PUGMAS.setMaxHealth(player, HealCommand.getMaxHealth(player) * 0.50)
		),

		HALF_CURRENCY(SlotPos.of(3, 6), new ItemBuilder(Material.MOURNER_POTTERY_SHERD).name("&cHalve Currency")
			.lore("&3Half: &aCoin Pouch &3set to &c75% Coins", "&3Full: &aCoin Pouch &3set to &c50% Coins"),
			(player) -> {
				PUGMAS.send(player, "TODO HALF REWARD - RANDOM_DEATH");
			},
			(player) -> {
				PUGMAS.send(player, "TODO FULL REWARD - RANDOM_DEATH");
			}
		),
		;

		@AllArgsConstructor
		enum Pugmas24SlotMachineRewardEnchant {
			PICKAXE(List.of(Enchant.FORTUNE, Enchant.EFFICIENCY)),
			SWORD(List.of(Enchant.LOOTING, Enchant.SHARPNESS)),
			FISHING_ROD(List.of(Enchant.LUCK_OF_THE_SEA, Enchant.LURE));

			final List<Enchantment> enchants;
			private static final List<Enchantment> sharedEnchants = List.of(Enchant.MENDING, Enchant.UNBREAKING);

			public static Pugmas24SlotMachineRewardEnchant of(Pugmas24SlotMachineReward reward) {
				return switch (reward) {
					case PICKAXE -> PICKAXE;
					case SWORD -> SWORD;
					case FISHING_ROD -> FISHING_ROD;
					default -> null;
				};
			}

			public List<Enchantment> getEnchants() {
				List<Enchantment> result = new ArrayList<>(sharedEnchants);
				result.addAll(this.enchants);
				return result;
			}

			public List<String> getEnchantStrings() {
				List<String> result = new ArrayList<>();
				for (Enchantment enchant : getEnchants()) {
					result.add(enchant.getKey().getKey());
				}
				return result;
			}
		}

		@Getter
		final SlotPos displaySlot;
		final ItemBuilder displayItem;
		final Consumer<Player> halfReward;
		final Consumer<Player> fullReward;

		public ItemBuilder getDisplayItem() {
			return displayItem.clone();
		}

		public static List<Material> getAllSherds() {
			return Arrays.stream(values()).map(reward -> reward.getDisplayItem().material()).toList();
		}

		public static @Nullable Pugmas24SlotMachine.Pugmas24SlotMachineReward of(Material sherd) {
			for (Pugmas24SlotMachineReward reward : values()) {
				if (reward.getDisplayItem().material() == sherd)
					return reward;
			}
			return null;
		}

		private static final List<Location> fireworkLocations = List.of(
			PUGMAS.location(-732.5, 82.2, -2907.5),
			PUGMAS.location(-736.5, 82.2, -2907.5));

		public void give(Player player, Pugmas24SlotMachineRewardType type) {
			switch (type) {
				case HALF -> {
					new SoundBuilder(Sound.ENTITY_VILLAGER_CELEBRATE).location(soundLocation).volume(0.5).play();
					halfReward.accept(player);
				}
				case FULL -> {
					new SoundBuilder(CustomSound.PARTY_HOORAY).location(soundLocation).volume(0.5).play();
					fireworkLocations.forEach(location -> new FireworkLauncher(location).power(1).detonateAfter(TickTime.TICK.x(13)).rainbow().flickering(true).type(Type.BALL).launch());
					fullReward.accept(player);
				}
			}
		}

		public enum Pugmas24SlotMachineRewardType {
			FULL, HALF;
		}
	}

	@Data
	static class SlotMachineColumn {
		int columnIndex;
		SlotMachineColumnStatus status = SlotMachineColumnStatus.INACTIVE;
		List<DecoratedPot> pots;
		long stoppedTick;

		public SlotMachineColumn(int index, List<DecoratedPot> pots) {
			this.columnIndex = index;
			this.pots = pots;
		}

		public boolean isAbleToStop() {
			if (status != SlotMachineColumnStatus.RUNNING || columnIndex == 0)
				return true;

			int previousIndex = columnIndex - 1;
			SlotMachineColumn previousColumn = columns.get(previousIndex);
			if (previousColumn.getStatus() != SlotMachineColumnStatus.STOPPED)
				return false;

			return previousColumn.getStoppedTick() < (gameTicks + getRandomTicks(2, 5));
		}

		public boolean canSlow() {
			if (status != SlotMachineColumnStatus.RUNNING)
				return false;


			return gameTicks > getRandomTicks(3, 5);
		}

		public boolean canStop() {
			if (status != SlotMachineColumnStatus.SLOWING)
				return false;

			return gameTicks > getRandomTicks(7, 10);
		}

		private long getRandomTicks(int secondsMin, int secondsMax) {
			return TickTime.SECOND.x(RandomUtils.randomInt((columnIndex + 1) * secondsMin, (columnIndex + 1) * secondsMax));
		}

		public void playSound(Location soundLocation) {
			switch (status) {
				case RUNNING ->
					new SoundBuilder(Sound.UI_BUTTON_CLICK).location(soundLocation).volume(0.3).pitch(2).play();
				case SLOWING ->
					new SoundBuilder(Sound.UI_BUTTON_CLICK).location(soundLocation).volume(0.3).pitch(1.5).play();
				case STOPPED ->
					new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).location(soundLocation).volume(0.3).play();
			}
		}

		protected enum SlotMachineColumnStatus {
			INACTIVE,
			RUNNING,
			SLOWING,
			STOPPED,
		}
	}

	private static void setSides(DecoratedPot pot, Material sherd) {
		for (Side side : Side.values()) {
			pot.setSherd(side, sherd);
		}

		pot.update(true, false);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		if (!PUGMAS.shouldHandle(player))
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || block.getType() != Material.LEVER)
			return;

		if (!PUGMAS.worldguard().isInRegion(player, PLAY_REGION))
			return;

		if (!PUGMAS.worldguard().isInRegion(block, LEVER_REGION))
			return;

		event.setCancelled(true);
		if (playing) {
			PUGMAS.send(player, "&cThe slot machine is already being rolled");
			return;
		}

		if (block.getBlockData() instanceof Powerable powerable) {
			lever = block;
			powerable.setPowered(true);
			lever.setBlockData(powerable, true);
			new SoundBuilder(Sound.BLOCK_LEVER_CLICK).location(lever).volume(0.3).pitch(0.6).play();
		}

		start(player);
	}

	@EventHandler
	public void on(PlayerLeavingRegionEvent event) {
		if (!shouldHandle(event.getPlayer()))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(PLAY_REGION))
			return;

		event.setCancelled(true);
		PUGMAS.sendCooldown(gamer, "&cYou can't leave while the slot machine is rolling", "pugmas24_slotmachine_playing");
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!shouldHandle(player)) return;

		reset();
	}

	private static boolean shouldHandle(Player player) {
		if (!playing)
			return false;

		if (!PUGMAS.shouldHandle(player))
			return false;

		return gamer.getUniqueId().equals(player.getUniqueId());
	}

	@Rows(5)
	@Title("Slot Machine Rewards")
	public static class Pugmas24SlotMachineRewardMenu extends InventoryProvider {
		ItemBuilder infoItem = new ItemBuilder(Material.BOOK).name("&eInfo").lore("&e2 &3of a kind -> &eHalf reward", "&e3 &3of a kind -> &eFull reward").itemFlags(ItemFlags.HIDE_ALL);
		ItemBuilder categoryPrize = new ItemBuilder(Material.DIAMOND).name("&bPrizes").itemFlags(ItemFlags.HIDE_ALL);
		ItemBuilder categoryTool = new ItemBuilder(Material.IRON_PICKAXE).name("&dTool Rewards").itemFlags(ItemFlags.HIDE_ALL);
		ItemBuilder categoryPenalty = new ItemBuilder(Material.TNT).name("&cPenalties").itemFlags(ItemFlags.HIDE_ALL);

		@Override
		public void init() {
			addCloseItem();
			contents.set(SlotPos.of(0, 8), ClickableItem.empty(infoItem));
			contents.set(SlotPos.of(0, 2), ClickableItem.empty(categoryPrize));
			contents.set(SlotPos.of(0, 4), ClickableItem.empty(categoryTool));
			contents.set(SlotPos.of(0, 6), ClickableItem.empty(categoryPenalty));

			for (Pugmas24SlotMachineReward reward : Pugmas24SlotMachineReward.values()) {
				ItemBuilder displayItem = reward.getDisplayItem().loreize(false).itemFlags(ItemFlags.HIDE_ALL);

				Pugmas24SlotMachineRewardEnchant rewardEnchant = Pugmas24SlotMachineRewardEnchant.of(reward);
				if (rewardEnchant != null) {
					List<String> lore = new ArrayList<>();
					for (String enchant : rewardEnchant.getEnchantStrings()) {
						lore.add("&3- &e" + StringUtils.camelCase(enchant));
					}
					displayItem.lore(lore);
				}

				contents.set(reward.getDisplaySlot(), ClickableItem.empty(displayItem));
			}
		}
	}


}
