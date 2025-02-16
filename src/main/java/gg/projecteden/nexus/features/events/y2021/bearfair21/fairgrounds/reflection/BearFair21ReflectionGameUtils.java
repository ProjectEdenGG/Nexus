package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.reflection;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21.BF21PointSource;
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

public class BearFair21ReflectionGameUtils {

	public static void clearLamps() {
		for (BearFair21ReflectionGameLamp lamp : BearFair21ReflectionGameLamp.values()) {
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
		BearFair21ReflectionGameLamp newLamp = RandomUtils.randomElement(Arrays.asList(BearFair21ReflectionGameLamp.values()));
		for (int i = 0; i < 10; i++) {
			if (BearFair21ReflectionGame.getLamp().equals(newLamp))
				newLamp = RandomUtils.randomElement(Arrays.asList(BearFair21ReflectionGameLamp.values()));
			else
				break;
		}
		BearFair21ReflectionGame.setLamp(newLamp);

		String type = BearFair21ReflectionGame.getLamp().getChatColor() + StringUtils.camelCase(BearFair21ReflectionGame.getLamp().getType());
		BearFair21ReflectionGame.setReflections(0);
		if (RandomUtils.chanceOf(50))
			BearFair21ReflectionGame.setReflections(BearFair21ReflectionGame.getLamp().getMin());
		else if (RandomUtils.chanceOf(50))
			BearFair21ReflectionGame.setReflections(BearFair21ReflectionGame.getLamp().getMax());

		String count = "";
		if (BearFair21ReflectionGame.getReflections() > 0)
			count = " in " + BearFair21ReflectionGame.getReflections() + "+ reflections";

		BearFair21ReflectionGame.setMessage("Hit " + type + "&f" + count);
	}

	public static boolean checkObjective(int reflectCount, Material material) {
		boolean reflectBool = true;
		if (BearFair21ReflectionGame.getReflections() != 0)
			reflectBool = reflectCount >= BearFair21ReflectionGame.getReflections();

		BearFair21ReflectionGameLamp hitLamp = BearFair21ReflectionGameLamp.from(material);

		return reflectBool && BearFair21ReflectionGame.getLamp().equals(hitLamp);
	}

	public static void broadcastObjective() {
		Collection<Player> players = BearFair21.worldguard().getPlayersInRegion(BearFair21ReflectionGame.getGameRg());
		for (Player player : players) {
			BearFair21.send(BearFair21ReflectionGame.getPrefix() + BearFair21ReflectionGame.getMessage(), player);
		}
	}

	public static void win(int count) {
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).location(BearFair21ReflectionGame.getCenter()).volume(2.0).play();

		String type = BearFair21ReflectionGame.getLamp().getChatColor() + StringUtils.camelCase(BearFair21ReflectionGame.getLamp().getType());
		Collection<Player> players = BearFair21.worldguard().getPlayersInRegion(BearFair21ReflectionGame.getGameRg());
		for (Player player : players)
			BearFair21.send(BearFair21ReflectionGame.getPrefix() + type + " &fwas hit in " + count + " reflections!", player);

		BearFair21.giveDailyTokens(BearFair21ReflectionGame.getButtonPresser(), BF21PointSource.REFLECTION, 5);

		Tasks.wait(TickTime.SECOND.x(3), () -> {
			randomizeBanners();
			newObjective();
			broadcastObjective();
		});
	}

	private static void randomizeBanners() {
		ProtectedRegion region = BearFair21.worldguard().getProtectedRegion(BearFair21ReflectionGame.getPowderRg());
		List<Block> blocks = BearFair21.worldedit().getBlocks(region);
		for (Block block : blocks) {
			if (!block.getType().equals(Material.IRON_BLOCK))
				continue;

			Block banner = block.getRelative(0, 2, 0);
			Block banner1 = banner.getRelative(0, -5, 0);

			BlockData blockData = banner.getBlockData();
			BlockData blockData1 = banner1.getBlockData();

			Rotatable rotatable = (Rotatable) blockData;
			Rotatable rotatable1 = (Rotatable) blockData1;

			BlockFace newFace = RandomUtils.randomElement(BearFair21ReflectionGame.getAngles());

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
		int ndx = BearFair21ReflectionGame.getAngles().indexOf(blockFace) + 1;
		if (ndx == BearFair21ReflectionGame.getAngles().size())
			ndx = 0;
		return BearFair21ReflectionGame.getAngles().get(ndx);
	}
}
