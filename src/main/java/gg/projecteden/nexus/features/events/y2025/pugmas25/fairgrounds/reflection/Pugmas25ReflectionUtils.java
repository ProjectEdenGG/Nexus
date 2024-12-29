package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.reflection;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
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

public class Pugmas25ReflectionUtils {

	public static void clearLamps() {
		for (Pugmas25ReflectionLamp lamp : Pugmas25ReflectionLamp.values()) {
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
		Pugmas25ReflectionLamp newLamp = RandomUtils.randomElement(Arrays.asList(Pugmas25ReflectionLamp.values()));
		for (int i = 0; i < 10; i++) {
			if (Pugmas25Reflection.getLamp().equals(newLamp))
				newLamp = RandomUtils.randomElement(Arrays.asList(Pugmas25ReflectionLamp.values()));
			else
				break;
		}
		Pugmas25Reflection.setLamp(newLamp);

		String type = Pugmas25Reflection.getLamp().getChatColor() + StringUtils.camelCase(Pugmas25Reflection.getLamp().getType());
		Pugmas25Reflection.setReflections(0);
		if (RandomUtils.chanceOf(50))
			Pugmas25Reflection.setReflections(Pugmas25Reflection.getLamp().getMin());
		else if (RandomUtils.chanceOf(50))
			Pugmas25Reflection.setReflections(Pugmas25Reflection.getLamp().getMax());

		String count = "";
		if (Pugmas25Reflection.getReflections() > 0)
			count = " in " + Pugmas25Reflection.getReflections() + "+ reflections";

		Pugmas25Reflection.setMessage("Hit " + type + "&f" + count);
	}


	public static boolean checkObjective(int reflectCount, Material material) {
		boolean reflectBool = true;
		if (Pugmas25Reflection.getReflections() != 0)
			reflectBool = reflectCount >= Pugmas25Reflection.getReflections();

		Pugmas25ReflectionLamp hitLamp = Pugmas25ReflectionLamp.from(material);

		return reflectBool && Pugmas25Reflection.getLamp().equals(hitLamp);
	}

	public static void broadcastObjective() {
		Collection<Player> players = Pugmas25.get().getPlayersIn(Pugmas25Reflection.getGameRg());
		for (Player player : players) {
			Pugmas25.get().sendNoPrefix(player, Pugmas25Reflection.getPrefix() + Pugmas25Reflection.getMessage());
		}
	}

	public static void win(int count) {
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).location(Pugmas25Reflection.getCenter()).volume(2.0).play();

		String type = Pugmas25Reflection.getLamp().getChatColor() + StringUtils.camelCase(Pugmas25Reflection.getLamp().getType());
		Collection<Player> players = Pugmas25.get().getPlayersIn(Pugmas25Reflection.getGameRg());
		for (Player player : players)
			Pugmas25.get().sendNoPrefix(player, Pugmas25Reflection.getPrefix() + type + " &fwas hit in " + count + " reflections!");

		//BearFair21.giveDailyTokens(Reflection.getButtonPresser(), BF21PointSource.REFLECTION, 5);

		Tasks.wait(TickTime.SECOND.x(3), () -> {
			randomizeBanners();
			newObjective();
			broadcastObjective();
		});
	}

	private static void randomizeBanners() {
		ProtectedRegion region = Pugmas25.get().worldguard().getProtectedRegion(Pugmas25Reflection.getBannerRg());
		List<Block> blocks = Pugmas25.get().worldedit().getBlocks(region);
		for (Block block : blocks) {
			if (!block.getType().equals(Material.IRON_BLOCK))
				continue;

			Block banner = block.getRelative(0, 2, 0);
			Block banner1 = banner.getRelative(0, -5, 0);

			BlockData blockData = banner.getBlockData();
			BlockData blockData1 = banner1.getBlockData();

			Rotatable rotatable = (Rotatable) blockData;
			Rotatable rotatable1 = (Rotatable) blockData1;

			BlockFace newFace = RandomUtils.randomElement(Pugmas25Reflection.getAngles());

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
		int ndx = Pugmas25Reflection.getAngles().indexOf(blockFace) + 1;
		if (ndx == Pugmas25Reflection.getAngles().size())
			ndx = 0;
		return Pugmas25Reflection.getAngles().get(ndx);
	}

	static BlockFace getReflection(BlockFace from, BlockFace bannerFace) {
		if (bannerFace.name().toLowerCase().contains(from.name().toLowerCase()))
			return null;

		if (from.getOppositeFace().equals(bannerFace))
			return from.getOppositeFace();

		if (from.equals(BlockFace.NORTH)) {
			if (bannerFace.equals(BlockFace.WEST) || bannerFace.equals(BlockFace.EAST))
				return from;

			if (bannerFace.equals(BlockFace.SOUTH_WEST))
				return BlockFace.WEST;
			else
				return BlockFace.EAST;

		} else if (from.equals(BlockFace.SOUTH)) {
			if (bannerFace.equals(BlockFace.WEST) || bannerFace.equals(BlockFace.EAST))
				return from;

			if (bannerFace.equals(BlockFace.NORTH_WEST))
				return BlockFace.WEST;
			else
				return BlockFace.EAST;

		} else if (from.equals(BlockFace.EAST)) {
			if (bannerFace.equals(BlockFace.SOUTH) || bannerFace.equals(BlockFace.NORTH))
				return from;

			if (bannerFace.equals(BlockFace.SOUTH_WEST))
				return BlockFace.SOUTH;
			else
				return BlockFace.NORTH;

		} else if (from.equals(BlockFace.WEST)) {
			if (bannerFace.equals(BlockFace.SOUTH) || bannerFace.equals(BlockFace.NORTH))
				return from;

			if (bannerFace.equals(BlockFace.NORTH_EAST))
				return BlockFace.NORTH;
			else
				return BlockFace.SOUTH;
		}

		return from;
	}
}
