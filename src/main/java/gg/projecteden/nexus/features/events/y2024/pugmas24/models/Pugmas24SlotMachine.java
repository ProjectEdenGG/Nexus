package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine.SlotMachineColumn.SlotMachineColumnStatus;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.DecoratedPot.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: MAKE LIGHTS FLASH WHEN RUNNING
public class Pugmas24SlotMachine implements Listener {
	private static final String potsRegion = Pugmas24.get().getRegionName() + "_slotmachine_pots";
	private static final String leverRegion = Pugmas24.get().getRegionName() + "_slotmachine_lever";
	private static final String soundRegion = Pugmas24.get().getRegionName() + "_slotmachine_sound";
	private static final String rowRegion = Pugmas24.get().getRegionName() + "_slotmachine_row";

	public static final List<Material> ALL_SHERDS = List.of(
		Material.PRIZE_POTTERY_SHERD, Material.HEART_POTTERY_SHERD, Material.ARMS_UP_POTTERY_SHERD,
		Material.ANGLER_POTTERY_SHERD, Material.MINER_POTTERY_SHERD, Material.BLADE_POTTERY_SHERD,
		Material.MOURNER_POTTERY_SHERD, Material.HEARTBREAK_POTTERY_SHERD, Material.DANGER_POTTERY_SHERD
	);

	private static final Map<Integer, SlotMachineColumn> columns = new HashMap<>();
	private static int WIDTH;
	private static int HEIGHT;
	private static SlotMachineAxis AXIS;
	@Getter
	private static boolean rolling = false;
	private static Player rollingPlayer;
	private static Location soundLocation;

	public Pugmas24SlotMachine() {
		Nexus.registerListener(this);

		init(null);
	}

	public static void init(Player player) {
		WorldGuardUtils worldguard = Pugmas24.get().worldguard();
		WorldEditUtils worldedit = Pugmas24.get().worldedit();

		soundLocation = worldedit.getBlocks(worldguard.getRegion(soundRegion)).getFirst().getLocation();

		ProtectedRegion region = worldguard.getProtectedRegion(potsRegion);
		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();

		HEIGHT = (max.getBlockY() - min.getBlockY()) + 1;
		WIDTH = (max.getBlockX() - min.getBlockX()) + 1;
		AXIS = SlotMachineAxis.X;

		if (min.getBlockX() == max.getBlockX()) {
			WIDTH = (max.getBlockZ() - min.getBlockZ()) + 1;
			AXIS = SlotMachineAxis.Z;
		}

		for (int horizontal = 0; horizontal < WIDTH; horizontal++) {
			int x = min.getBlockX();
			int z = min.getBlockZ();

			if (AXIS == SlotMachineAxis.X)
				x += horizontal;
			else
				z += horizontal;

			List<DecoratedPot> pots = new ArrayList<>();
			for (int vertical = 0; vertical < HEIGHT; vertical++) {
				int y = min.getBlockY();
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
		rollingPlayer = null;
		//
		rolling = false;
	}

	private static void reward() {
		PlayerUtils.send(rollingPlayer, "Nerd. <reward stuff here>");
		reset();
	}

	private static int rollingTaskId;
	private static List<Material> rollSherds;
	private static int rollSherdsSize;
	private static long tick;
	private static final long interval = TickTime.TICK.x(4);

	public static void roll(Player player) {
		if (rolling) {
			PlayerUtils.send(player, Pugmas24.PREFIX + "&cThe slot machine is already being rolled");
			return;
		}

		rolling = true;
		rollingPlayer = player;
		rollSherds = new ArrayList<>(ALL_SHERDS);
		rollSherdsSize = rollSherds.size();
		Collections.shuffle(rollSherds);
		tick = 0;

		// Roll
		for (SlotMachineColumn slotColumn : columns.values()) {
			slotColumn.setStatus(SlotMachineColumnStatus.RUNNING);
		}

		rollingTaskId = Tasks.repeat(0, interval, () -> {
			update();
			tick += interval;
		});

	}

	private static void update() {
		int slowingColumnSkips = 0;
		for (int column = 0; column < WIDTH; column++) {
			SlotMachineColumn slotColumn = columns.get(column);
			SlotMachineColumnStatus columnStatus = slotColumn.getStatus();

			if (columnStatus == SlotMachineColumnStatus.INACTIVE)
				continue;

			if (columnStatus == SlotMachineColumnStatus.SLOWING) {
				if (tick % (interval * 2) != 0) {
					slowingColumnSkips++;
					continue;
				}
			}

			if (slotColumn.isAbleToStop()) {
				if (slotColumn.canSlow()) {
					slotColumn.setStatus(SlotMachineColumnStatus.SLOWING);
				} else if (slotColumn.canStop()) {
					slowingColumnSkips = 0;
					slotColumn.setStatus(SlotMachineColumnStatus.INACTIVE);
					slotColumn.setStoppedTick(tick);
					new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).location(soundLocation).volume(0.3).play();
					if (slotColumn.getColumnIndex() == (columns.size() - 1)) {
						reward();
						return;
					}
					continue;
				}
			}

			new SoundBuilder(Sound.UI_BUTTON_CLICK).location(soundLocation).volume(0.3).play();

			int columnOffset = (int) ((tick / interval + column * HEIGHT) % rollSherdsSize);
			for (int row = 0; row < HEIGHT; row++) {
				int sherdIndex = ((columnOffset - slowingColumnSkips + row) % rollSherdsSize);
				Material sherd = rollSherds.get(sherdIndex);
				setSides(slotColumn.getPots().get(row), sherd);
			}
		}
	}

	public static void shutdown() {
		Tasks.cancel(rollingTaskId);
	}

	//

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
			if (previousColumn.getStatus() != SlotMachineColumnStatus.INACTIVE)
				return false;

			return previousColumn.getStoppedTick() < (tick + TickTime.SECOND.x(3));
		}

		public boolean canSlow() {
			if (status != SlotMachineColumnStatus.RUNNING)
				return false;

			return tick > TickTime.SECOND.x(RandomUtils.randomInt((columnIndex + 1) * 3, (columnIndex + 1) * 5));
		}

		public boolean canStop() {
			if (status != SlotMachineColumnStatus.SLOWING)
				return false;

			return tick > TickTime.SECOND.x(RandomUtils.randomInt((columnIndex + 1) * 7, (columnIndex + 1) * 10));
		}

		protected enum SlotMachineColumnStatus {
			INACTIVE,
			RUNNING,
			SLOWING,
		}
	}

	private enum SlotMachineAxis {
		X, Z
	}

	private static void setSides(DecoratedPot pot, Material sherd) {
		for (Side side : Side.values()) {
			pot.setSherd(side, sherd);
		}

		pot.update(true);
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
		roll(player);
	}


}
