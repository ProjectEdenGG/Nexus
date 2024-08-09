package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.DecoratedPot.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pugmas24SlotMachine implements Listener {
	private static final String potsRegion = Pugmas24.get().getRegionName() + "_slotmachine_pots";
	private static final String leverRegion = Pugmas24.get().getRegionName() + "_slotmachine_lever";

	public static final List<Material> ALL_SHERDS = List.of(
		Material.PRIZE_POTTERY_SHERD, Material.HEART_POTTERY_SHERD, Material.ARMS_UP_POTTERY_SHERD,
		Material.ANGLER_POTTERY_SHERD, Material.MINER_POTTERY_SHERD, Material.BLADE_POTTERY_SHERD,
		Material.MOURNER_POTTERY_SHERD, Material.HEARTBREAK_POTTERY_SHERD, Material.DANGER_POTTERY_SHERD
	);

	private static final Map<Integer, List<DecoratedPot>> columns = new HashMap<>();
	private static int WIDTH;
	private static int HEIGHT;
	private static SlotMachineAxis AXIS;
	private static boolean rolling = false;
	private static Player rollingPlayer;

	public Pugmas24SlotMachine() {
		Nexus.registerListener(this);

		setup(null);
	}

	private enum SlotMachineAxis {
		X, Z
	}

	public static void setup(Player player) {
		ProtectedRegion region = Pugmas24.get().worldguard().getProtectedRegion(potsRegion);
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
			List<DecoratedPot> pots = new ArrayList<>();

			int x = min.getBlockX();
			int z = min.getBlockZ();

			if (AXIS == SlotMachineAxis.X)
				x += horizontal;
			else
				z += horizontal;

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

			columns.put(horizontal, pots);
		}
	}

	private static void reset() {
		rollingPlayer = null;
		//
		rolling = false;
	}

	public static void roll(Player player) {
		if (rolling) {
			PlayerUtils.send(player, Pugmas24.PREFIX + "&cThe slot machine is already being rolled");
			return;
		}

		rolling = true;
		rollingPlayer = player;

		for (Integer column : columns.keySet()) {
			Material randomSherd = RandomUtils.randomElement(ALL_SHERDS);
			for (DecoratedPot decoratedPot : columns.get(column)) {
				setSides(decoratedPot, randomSherd);
			}
		}

		reset();
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
