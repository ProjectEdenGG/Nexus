package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.EdenEventGameConfig;
import gg.projecteden.nexus.features.events.EdenEventSinglePlayerGame;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachineReward.Pugmas25SlotMachineRewardType;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.SlotMachineColumn.SlotMachineColumnStatus;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
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
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
	TODO:
		- BALANCE CHECK + WITHDRAW ON ROLL
		- REWARDS
 */
@SuppressWarnings("FieldCanBeLocal")
@EdenEventGameConfig(
	prefix = "Slot Machine",
	world = "pugmas25",
	playRegion = "pugmas25_slotmachine_play"
)
@Environments(Env.PROD)
public class Pugmas25SlotMachine extends EdenEventSinglePlayerGame {
	private static Pugmas25SlotMachine instance;

	private static final String BASE_REGION = "pugmas25_slotmachine_";
	private static final String POTS_REGION = BASE_REGION + "pots";
	private static final String LEVER_REGION = BASE_REGION + "lever";
	private static final String SOUNDS_REGION = BASE_REGION + "sound";
	private static final String ROW_REGION = BASE_REGION + "row";

	private int WIDTH;
	private int HEIGHT;
	private SlotMachineAxis AXIS;
	private final boolean REVERSED = true;

	private final Set<Location> winningRow = new HashSet<>();
	public final Map<Integer, SlotMachineColumn> columns = new HashMap<>();
	private List<Material> rollSherds;
	private int rollSherdsSize;
	private int slowingColumnSkips = 0;
	Location soundLocation;
	private Block lever;

	public Pugmas25SlotMachine() {
		instance = this;
		init();
	}

	public static Pugmas25SlotMachine get() {
		return instance;
	}

	@Override
	public long getMaxGameTicks() {
		return TickTime.MINUTE.x(1);
	}

	@Override
	public Location getBaseLocation() {
		return soundLocation;
	}

	@Override
	public void init() {
		super.init();

		columns.clear();

		ProtectedRegion rowRg = worldguard().getProtectedRegion(ROW_REGION);
		worldedit().getBlocks(rowRg).forEach(block -> {
			if (block.getState() instanceof DecoratedPot)
				winningRow.add(block.getLocation());
		});

		soundLocation = worldedit().getBlocks(worldguard().getRegion(SOUNDS_REGION)).getFirst().getLocation();

		ProtectedRegion potsRg = worldguard().getProtectedRegion(POTS_REGION);
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

				Location loc = location(x, y, z);

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

	@Override
	public void reset() {
		cancelUpdateTask();
		slowingColumnSkips = 0;
		for (SlotMachineColumn slotColumn : columns.values()) {
			slotColumn.setStatus(SlotMachineColumnStatus.INACTIVE);
		}

		if (lever != null && lever.getBlockData() instanceof Powerable powerable) {
			powerable.setPowered(false);
			lever.setBlockData(powerable, true);
			new SoundBuilder(Sound.BLOCK_LEVER_CLICK).location(lever).volume(0.3).pitch(0.5).play();
		}

		super.reset();
	}

	@Override
	protected boolean startChecks(Player player) {
		// TODO: BALANCE CHECK & WITHDRAW

		return super.startChecks(player);
	}

	@Override
	protected void preStart() {
		rollSherds = new ArrayList<>(Pugmas25SlotMachineReward.getAllSherds());
		rollSherdsSize = rollSherds.size();
		Collections.shuffle(rollSherds);
		slowingColumnSkips = 0;

		// Roll
		for (SlotMachineColumn slotColumn : columns.values()) {
			slotColumn.setStatus(SlotMachineColumnStatus.RUNNING);
		}
	}

	@Override
	protected void update() {
		super.update();

		for (int column = 0; column < WIDTH; column++) {
			SlotMachineColumn slotColumn = columns.get(column);
			SlotMachineColumnStatus columnStatus = slotColumn.getStatus();

			if (columnStatus == SlotMachineColumnStatus.INACTIVE || columnStatus == SlotMachineColumnStatus.STOPPED)
				continue;

			if (columnStatus == SlotMachineColumnStatus.SLOWING) {
				if (getGameTicks() % (updateIntervalTicks * 2L) != 0) {
					slowingColumnSkips++;
					continue;
				}
			}

			int columnOffset = ((int) (((getGameTicks() / updateIntervalTicks + column * HEIGHT) - slowingColumnSkips) % rollSherdsSize));
			for (int row = 0; row < HEIGHT; row++) {
				int sherdIndex = ((columnOffset + row) % rollSherdsSize);
				Material sherd = rollSherds.get(sherdIndex);
				setSides(slotColumn.getPots().get(row), sherd);
			}

			if (slotColumn.isAbleToStop(this)) {
				if (slotColumn.canSlow(this)) {
					slotColumn.setStatus(SlotMachineColumnStatus.SLOWING);
					slotColumn.playSound(soundLocation);
					continue;
				}

				if (slotColumn.canStop(this)) {
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

	@Override
	public void end() {
		cancelUpdateTask();

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
		Pugmas25SlotMachineReward reward = null;
		Pugmas25SlotMachineRewardType rewardType = null;
		for (Material material : winningSherdMap.keySet()) {
			int count = winningSherdMap.get(material);
			if (count < halfCount)
				continue;

			if (count == halfCount) {
				reward = Pugmas25SlotMachineReward.of(material);
				rewardType = Pugmas25SlotMachineRewardType.HALF;
				break;
			} else if (count == fullCount) {
				reward = Pugmas25SlotMachineReward.of(material);
				rewardType = Pugmas25SlotMachineRewardType.FULL;
				break;
			}
		}

		if (reward != null) {
			reward.give(getGamer(), rewardType);
		} else {
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).location(soundLocation).volume(0.5).play();
		}

		super.end();
	}

	//

	private List<Location> LIGHTS = new ArrayList<>(List.of(
		location(-733, 85, -2904),
		location(-735, 86, -2904),
		location(-737, 85, -2904)));

	private final List<Location> activatedLights = new ArrayList<>();
	private int loopCounter = 0;
	private boolean transitioning = false;

	public void nextLight() {
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

	private void setPowerable(Location location, boolean lit) {
		Block light = location.getBlock();
		Lightable lightable = (Lightable) light.getBlockData();
		lightable.setLit(lit);
		light.setBlockData(lightable, false);
	}

	private enum SlotMachineAxis {
		X, Z
	}

	private void setSides(DecoratedPot pot, Material sherd) {
		for (Side side : Side.values()) {
			pot.setSherd(side, sherd);
		}

		pot.update(true, false);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || block.getType() != Material.LEVER)
			return;

		if (!worldguard().isInRegion(player, getPlayRegion()))
			return;

		if (!worldguard().isInRegion(block, LEVER_REGION))
			return;

		event.setCancelled(true);

		if (isPlaying())
			return;

		if (block.getBlockData() instanceof Powerable powerable) {
			lever = block;
			powerable.setPowered(true);
			lever.setBlockData(powerable, true);
			new SoundBuilder(Sound.BLOCK_LEVER_CLICK).location(lever).volume(0.3).pitch(0.6).play();
		}

		start(player);
	}


}
