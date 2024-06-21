package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.reflection;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24Utils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.NORTH_EAST;
import static org.bukkit.block.BlockFace.NORTH_WEST;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.SOUTH_WEST;
import static org.bukkit.block.BlockFace.WEST;

public class ReflectionUtils {

	public static void clearLamps() {
		for (ReflectionLamp lamp : ReflectionLamp.values()) {
			Block block = lamp.getLocation().getBlock();
			Block block1 = block.getRelative(0, -6, 0);

			BlockData blockData = block.getBlockData();
			BlockData blockData1 = block1.getBlockData();

			Lightable lightable = (Lightable) blockData;
			Lightable lightable1 = (Lightable) blockData1;

			lightable.setLit(false);
			lightable1.setLit(false);

			block.setBlockData(lightable);
			block1.setBlockData(lightable1);
		}
	}

	public static void newObjective() {
		clearLamps();
		ReflectionLamp newLamp = RandomUtils.randomElement(Arrays.asList(ReflectionLamp.values()));
		for (int i = 0; i < 10; i++) {
			if (Reflection.getLamp().equals(newLamp))
				newLamp = RandomUtils.randomElement(Arrays.asList(ReflectionLamp.values()));
			else
				break;
		}
		Reflection.setLamp(newLamp);

		String type = Reflection.getLamp().getChatColor() + StringUtils.camelCase(Reflection.getLamp().getType());
		Reflection.setReflections(0);
		if (RandomUtils.chanceOf(50))
			Reflection.setReflections(Reflection.getLamp().getMin());
		else if (RandomUtils.chanceOf(50))
			Reflection.setReflections(Reflection.getLamp().getMax());

		String count = "";
		if (Reflection.getReflections() > 0)
			count = " in " + Reflection.getReflections() + "+ reflections";

		Reflection.setMessage("Hit " + type + "&f" + count);
	}


	public static boolean checkObjective(int reflectCount, Material material) {
		boolean reflectBool = true;
		if (Reflection.getReflections() != 0)
			reflectBool = reflectCount >= Reflection.getReflections();

		ReflectionLamp hitLamp = ReflectionLamp.from(material);

		return reflectBool && Reflection.getLamp().equals(hitLamp);
	}

	public static void broadcastObjective() {
		Collection<Player> players = Pugmas24Utils.getPlayersIn(Reflection.getGameRg());
		for (Player player : players) {
			Pugmas24Utils.send(Reflection.getPrefix() + Reflection.getMessage(), player);
		}
	}

	public static void win(int count) {
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).location(Reflection.getCenter()).volume(2.0).play();

		String type = Reflection.getLamp().getChatColor() + StringUtils.camelCase(Reflection.getLamp().getType());
		Collection<Player> players = Pugmas24Utils.getPlayersIn(Reflection.getGameRg());
		for (Player player : players)
			Pugmas24Utils.send(Reflection.getPrefix() + type + " &fwas hit in " + count + " reflections!", player);

		//BearFair21.giveDailyTokens(Reflection.getButtonPresser(), BF21PointSource.REFLECTION, 5);

		Tasks.wait(TickTime.SECOND.x(3), () -> {
			randomizeBanners();
			newObjective();
			broadcastObjective();
		});
	}

	private static void randomizeBanners() {
		ProtectedRegion region = Pugmas24Utils.worldguard().getProtectedRegion(Reflection.getBannerRg());
		List<Block> blocks = Pugmas24Utils.worldedit().getBlocks(region);
		for (Block block : blocks) {
			if (!block.getType().equals(Material.IRON_BLOCK))
				continue;

			Block banner = block.getRelative(0, 2, 0);
			Block banner1 = banner.getRelative(0, -5, 0);

			BlockData blockData = banner.getBlockData();
			BlockData blockData1 = banner1.getBlockData();

			Rotatable rotatable = (Rotatable) blockData;
			Rotatable rotatable1 = (Rotatable) blockData1;

			BlockFace newFace = RandomUtils.randomElement(Reflection.getAngles());

			rotatable.setRotation(newFace);
			rotatable1.setRotation(newFace);

			banner.setBlockData(rotatable);
			banner1.setBlockData(rotatable1);
		}
	}

	static void rotateBanner(Block banner) {
		Block banner1 = banner.getRelative(0, -5, 0);

		BlockData blockData = banner.getBlockData();
		BlockData blockData1 = banner1.getBlockData();

		Rotatable rotatable = (Rotatable) blockData;
		Rotatable rotatable1 = (Rotatable) blockData1;

		BlockFace newFace = rotateBlockFace(rotatable.getRotation());

		rotatable.setRotation(newFace);
		rotatable1.setRotation(newFace);

		banner.setBlockData(rotatable);
		banner1.setBlockData(rotatable1);
	}

	private static BlockFace rotateBlockFace(BlockFace blockFace) {
		int ndx = Reflection.getAngles().indexOf(blockFace) + 1;
		if (ndx == Reflection.getAngles().size())
			ndx = 0;
		return Reflection.getAngles().get(ndx);
	}

	static BlockFace getReflection(BlockFace from, BlockFace bannerFace) {
		if (bannerFace.name().toLowerCase().contains(from.name().toLowerCase()))
			return null;

		if (from.getOppositeFace().equals(bannerFace))
			return from.getOppositeFace();

		if (from.equals(NORTH)) {
			if (bannerFace.equals(WEST) || bannerFace.equals(EAST))
				return from;

			if (bannerFace.equals(SOUTH_WEST))
				return WEST;
			else
				return EAST;

		} else if (from.equals(SOUTH)) {
			if (bannerFace.equals(WEST) || bannerFace.equals(EAST))
				return from;

			if (bannerFace.equals(NORTH_WEST))
				return WEST;
			else
				return EAST;

		} else if (from.equals(EAST)) {
			if (bannerFace.equals(SOUTH) || bannerFace.equals(NORTH))
				return from;

			if (bannerFace.equals(SOUTH_WEST))
				return SOUTH;
			else
				return NORTH;

		} else if (from.equals(WEST)) {
			if (bannerFace.equals(SOUTH) || bannerFace.equals(NORTH))
				return from;

			if (bannerFace.equals(NORTH_EAST))
				return NORTH;
			else
				return SOUTH;
		}

		return from;
	}
}
