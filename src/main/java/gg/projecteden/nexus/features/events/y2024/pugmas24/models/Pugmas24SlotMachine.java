package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24.Pugmas24DeathCause;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine.Pugmas24SlotMachineReward.Pugmas24SlotMachineRewardType;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine.SlotMachineColumn.SlotMachineColumnStatus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItem;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
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
		- REWARDS
 */
public class Pugmas24SlotMachine implements Listener {
	private static final String potsRegion = Pugmas24.get().getRegionName() + "_slotmachine_pots";
	private static final String leverRegion = Pugmas24.get().getRegionName() + "_slotmachine_lever";
	private static final String soundRegion = Pugmas24.get().getRegionName() + "_slotmachine_sound";
	private static final String rowRegion = Pugmas24.get().getRegionName() + "_slotmachine_row";

	private static final List<Location> winningRow = new ArrayList<>();
	private static final Map<Integer, SlotMachineColumn> columns = new HashMap<>();
	private static int WIDTH;
	private static int HEIGHT;
	private static SlotMachineAxis AXIS;
	@Getter
	private static boolean rolling = false;
	private static Player rollingPlayer;
	@Getter
	private static Location soundLocation;

	public Pugmas24SlotMachine() {
		Nexus.registerListener(this);
		init(null);
	}

	public static void init(Player player) {
		WorldGuardUtils worldguard = Pugmas24.get().worldguard();
		WorldEditUtils worldedit = Pugmas24.get().worldedit();

		ProtectedRegion rowRg = worldguard.getProtectedRegion(rowRegion);
		worldedit.getBlocks(rowRg).forEach(block -> {
			if (block.getState() instanceof DecoratedPot)
				winningRow.add(block.getLocation());
		});

		soundLocation = worldedit.getBlocks(worldguard.getRegion(soundRegion)).getFirst().getLocation();

		ProtectedRegion potsRg = worldguard.getProtectedRegion(potsRegion);
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

				Location loc = Pugmas24.get().location(x, y, z);
				Pugmas24.get().debugDot(player, loc, ColorType.YELLOW);

				Block block = loc.getBlock();
				if (!(block.getState() instanceof DecoratedPot decoratedPot))
					continue;

				pots.add(decoratedPot);
			}

			SlotMachineColumn column = new SlotMachineColumn(horizontal, pots);
			columns.put(horizontal, column);
		}
	}

	public static void reset() {
		Tasks.cancel(rollingTaskId);
		slowingColumnSkips = 0;
		for (SlotMachineColumn slotColumn : columns.values()) {
			slotColumn.setStatus(SlotMachineColumnStatus.INACTIVE);
		}
		//
		rollingPlayer = null;
		rolling = false;
	}

	private static void reward() {
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
			reward.give(rollingPlayer, rewardType);
		} else {
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).location(soundLocation).volume(0.5).play();
		}

		reset();
	}

	private static int rollingTaskId;
	private static List<Material> rollSherds;
	private static int rollSherdsSize;
	private static long tick;
	private static final long interval = TickTime.TICK.x(4);

	public static void roll(Player player) {
		if (Nexus.isMaintenanceQueued()) {
			Pugmas24.get().send(player, "&cServer maintenance is queued, try again later");
			return;
		}

		if (rolling) {
			Pugmas24.get().send(player, "&cThe slot machine is already being rolled");
			return;
		}

		// TODO: BALANCE CHECK & WITHDRAW

		rolling = true;
		rollingPlayer = player;
		rollSherds = new ArrayList<>(Pugmas24SlotMachineReward.getAllSherds());
		rollSherdsSize = rollSherds.size();
		Collections.shuffle(rollSherds);
		tick = 0;
		slowingColumnSkips = 0;

		// Roll
		for (SlotMachineColumn slotColumn : columns.values()) {
			slotColumn.setStatus(SlotMachineColumnStatus.RUNNING);
		}

		rollingTaskId = Tasks.repeat(0, interval, () -> {
			update();
			tick += interval;
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
				if (tick % (interval * 2) != 0) {
					slowingColumnSkips++;
					continue;
				}
			}

			int columnOffset = ((int) (((tick / interval + column * HEIGHT) - slowingColumnSkips) % rollSherdsSize));
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
					slotColumn.setStoppedTick(tick);

					slowingColumnSkips = 0;

					if (slotColumn.getColumnIndex() == (columns.size() - 1)) {
						Tasks.cancel(rollingTaskId);
						reward();
						return;
					}
				}
			}

			slotColumn.playSound(soundLocation);
		}
	}

	private static List<Location> LIGHTS = new ArrayList<>(List.of(
		Pugmas24.get().location(-733, 85, -2904),
		Pugmas24.get().location(-735, 86, -2904),
		Pugmas24.get().location(-737, 85, -2904)));

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
		JACKPOT(SlotPos.of(1, 2), new ItemBuilder(Material.PRIZE_POTTERY_SHERD).name("&aTODO")
			.lore("&eHalf&3: &eTODO", "&eFull&3: &eTODO"),
			(player) -> {
				Pugmas24.get().send(player, "TODO HALF REWARD - JACKPOT");
			},
			(player) -> {
				Pugmas24.get().send(player, "TODO FULL REWARD - JACKPOT");
			}
		),

		HEARTS(SlotPos.of(2, 2), new ItemBuilder(Material.HEART_POTTERY_SHERD).name("&aHeart Crystals")
			.lore("&eHalf&3: 2 Heart Crystals", "&eFull&3: 5 Heart Crystals"),
			(player) -> Pugmas24.get().give(player, Pugmas24QuestItem.HEART_CRYSTAL.getItemBuilder().amount(2)),
			(player) -> Pugmas24.get().give(player, Pugmas24QuestItem.HEART_CRYSTAL.getItemBuilder().amount(5))
		),

		CURRENCY(SlotPos.of(3, 2), new ItemBuilder(Material.ARMS_UP_POTTERY_SHERD).name("&aCurrency")
			.lore("&eHalf&3: &eTODO", "&eFull&3: &eTODO"),
			(player) -> {
				Pugmas24.get().send(player, "TODO HALF REWARD - CURRENCY");
			},
			(player) -> {
				Pugmas24.get().send(player, "TODO FULL REWARD - CURRENCY");
			}
		),

		PICKAXE(SlotPos.of(1, 4), new ItemBuilder(Material.MINER_POTTERY_SHERD).name("&eRandom Pickaxe Enchant")
			.lore("&eHalf&3: Uncommon", "&eFull&3: Rare", "", "&3Enchants: ", "&3- TODO"),
			(player) -> {
				Pugmas24.get().send(player, "TODO HALF REWARD - PICKAXE");
			},
			(player) -> {
				Pugmas24.get().send(player, "TODO FULL REWARD - PICKAXE");
			}
		),

		FISHING_ROD(SlotPos.of(2, 4), new ItemBuilder(Material.ANGLER_POTTERY_SHERD).name("&eRandom Fishing Rod Enchant")
			.lore("&eHalf&3: &eUncommon", "&eFull&3: &eRare", "", "&3Enchants: ", "&3- TODO"),
			(player) -> {
				Pugmas24.get().send(player, "TODO HALF REWARD - FISHING_ROD");
			},
			(player) -> {
				Pugmas24.get().send(player, "TODO FULL REWARD - FISHING_ROD");
			}
		),

		SWORD(SlotPos.of(3, 4), new ItemBuilder(Material.BLADE_POTTERY_SHERD).name("&eRandom Sword Enchant")
			.lore("&eHalf&3: &eUncommon", "&eFull&3: &eRare", "", "&3Enchants: ", "&3- TODO"),
			(player) -> {
				Pugmas24.get().send(player, "TODO HALF REWARD - SWORD");
			},
			(player) -> {
				Pugmas24.get().send(player, "TODO FULL REWARD - SWORD");
			}
		),

		HALF_MAX_HEALTH(SlotPos.of(1, 6), new ItemBuilder(Material.HEARTBREAK_POTTERY_SHERD).name("&cHalf Max Health")
			.lore("&eHalf&3: &aMax Health &3set to &e75%", "&eFull&3: &aMax Health &3set to &e50%"),
			(player) -> Pugmas24.get().setMaxHealth(player, Pugmas24.get().getMaxHealth(player) * 0.75),
			(player) -> Pugmas24.get().setMaxHealth(player, Pugmas24.get().getMaxHealth(player) * 0.50)
		),

		RANDOM_DEATH(SlotPos.of(2, 6), new ItemBuilder(Material.MOURNER_POTTERY_SHERD).name("&cRandom Death")
			.lore("&eHalf&3: In the future, lose half health", "&eFull&3: In the future, instantly die"),
			(player) -> {
				Pugmas24.get().send(player, "TODO HALF REWARD - RANDOM_DEATH");
			},
			(player) -> {
				Pugmas24.get().send(player, "TODO FULL REWARD - RANDOM_DEATH");
			}
		),

		INSTANT_DEATH(SlotPos.of(3, 6), new ItemBuilder(Material.DANGER_POTTERY_SHERD).name("&cInstant Death")
			.lore("&eHalf&3: &eHealth &3set to &e50%", "&eFull&3: &eHealth &3set to &e0%"),
			(player) -> player.setHealth(player.getHealth() / 2),
			(player) -> Pugmas24.get().onDeath(player, Pugmas24DeathCause.INSTANT_DEATH)
		),
		;

		@Getter
		final SlotPos displaySlot;
		@Getter
		final ItemBuilder displayItem;
		final Consumer<Player> halfReward;
		final Consumer<Player> fullReward;

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
			Pugmas24.get().location(-732.5, 82.2, -2907.5),
			Pugmas24.get().location(-736.5, 82.2, -2907.5));

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

			return previousColumn.getStoppedTick() < (tick + getRandomTicks(2, 5));
		}

		public boolean canSlow() {
			if (status != SlotMachineColumnStatus.RUNNING)
				return false;


			return tick > getRandomTicks(3, 5);
		}

		public boolean canStop() {
			if (status != SlotMachineColumnStatus.SLOWING)
				return false;

			return tick > getRandomTicks(7, 10);
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
		if (!Pugmas24.get().shouldHandle(player))
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || block.getType() != Material.LEVER)
			return;

		if (!Pugmas24.get().worldguard().isInRegion(block, leverRegion))
			return;

		event.setCancelled(true);

		if (block.getBlockData() instanceof Powerable powerable) {
			powerable.setPowered(true);
			block.setBlockData(powerable, true);
			new SoundBuilder(Sound.BLOCK_LEVER_CLICK).location(block).volume(0.3).pitch(0.6).play();

			Tasks.wait(10, () -> {
				powerable.setPowered(false);
				block.setBlockData(powerable, true);
				new SoundBuilder(Sound.BLOCK_LEVER_CLICK).location(block).volume(0.3).pitch(0.5).play();
			});
		}

		roll(player);
	}

	@Rows(5)
	@Title("Slot Machine Rewards")
	public static class Pugmas24SlotMachineRewardMenu extends InventoryProvider {
		ItemBuilder infoItem = new ItemBuilder(Material.BOOK).name("&eInfo").lore("&e2 &3of a kind -> &eHalf reward", "&e3 &3of a kind -> &eFull reward");
		ItemBuilder categoryTool = new ItemBuilder(Material.IRON_PICKAXE).name("&eTool Rewards");
		ItemBuilder categoryPrize = new ItemBuilder(Material.DIAMOND).name("&aPrizes");
		ItemBuilder categoryPenalty = new ItemBuilder(Material.TNT).name("&cPenalties");

		@Override
		public void init() {
			addCloseItem();
			contents.set(SlotPos.of(0, 8), ClickableItem.empty(infoItem));
			contents.set(SlotPos.of(0, 2), ClickableItem.empty(categoryPrize));
			contents.set(SlotPos.of(0, 4), ClickableItem.empty(categoryTool));
			contents.set(SlotPos.of(0, 6), ClickableItem.empty(categoryPenalty));

			for (Pugmas24SlotMachineReward reward : Pugmas24SlotMachineReward.values()) {
				contents.set(reward.getDisplaySlot(), ClickableItem.empty(reward.getDisplayItem().loreize(false)));
			}
		}
	}


}
