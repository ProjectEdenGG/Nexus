package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.reflection;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
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

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.getWEUtils;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.getWGUtils;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.send;

public class ReflectionGameUtils {

	public static void clearLamps() {
		for (ReflectionGameLamp lamp : ReflectionGameLamp.values()) {
			Block block = lamp.getLocation().getBlock();
			BlockData blockData = block.getBlockData();
			Lightable lightable = (Lightable) blockData;
			lightable.setLit(false);
			block.setBlockData(lightable);
		}
	}

	public static void newObjective() {
		clearLamps();
		ReflectionGameLamp newLamp = RandomUtils.randomElement(Arrays.asList(ReflectionGameLamp.values()));
		for (int i = 0; i < 10; i++) {
			if (ReflectionGame.getLamp().equals(newLamp))
				newLamp = RandomUtils.randomElement(Arrays.asList(ReflectionGameLamp.values()));
			else
				break;
		}
		ReflectionGame.setLamp(newLamp);

		String type = ReflectionGame.getLamp().getChatColor() + StringUtils.camelCase(ReflectionGame.getLamp().getType());
		ReflectionGame.setReflections(0);
		if (RandomUtils.chanceOf(50))
			ReflectionGame.setReflections(ReflectionGame.getLamp().getMin());
		else if (RandomUtils.chanceOf(50))
			ReflectionGame.setReflections(ReflectionGame.getLamp().getMax());

		String count = "";
		if (ReflectionGame.getReflections() > 0)
			count = " in " + ReflectionGame.getReflections() + "+ reflections";

		ReflectionGame.setMessage("Hit " + type + "&f" + count);
	}

	public static boolean checkObjective(int reflectCount, Material material) {
		boolean reflectBool = true;
		if (ReflectionGame.getReflections() != 0)
			reflectBool = reflectCount >= ReflectionGame.getReflections();

		ReflectionGameLamp hitLamp = ReflectionGameLamp.from(material);

		return reflectBool && ReflectionGame.getLamp().equals(hitLamp);
	}

	public static void broadcastObjective() {
		Collection<Player> players = getWGUtils().getPlayersInRegion(ReflectionGame.getGameRg());
		for (Player player : players) {
			send(ReflectionGame.getPrefix() + ReflectionGame.getMessage(), player);
		}
	}

	public static void win(int count) {
		BearFair21.getWorld().playSound(ReflectionGame.getCenter(), Sound.BLOCK_NOTE_BLOCK_BELL, 2F, 1F);

		String type = ReflectionGame.getLamp().getChatColor() + StringUtils.camelCase(ReflectionGame.getLamp().getType());
		Collection<Player> players = getWGUtils().getPlayersInRegion(ReflectionGame.getGameRg());
		for (Player player : players)
			send(ReflectionGame.getPrefix() + type + " &fwas hit in " + count + " reflections!", player);

		// TODO BF21: give points
//		if (giveDailyPoints) {
//			BearFair20User user = new BearFair20UserService().get(buttonPresser);
//			user.giveDailyPoints(Reflection.SOURCE);
//			new BearFair20UserService().save(user);
//		}

		Tasks.wait(Time.SECOND.x(3), () -> {
			randomizeBanners();
			newObjective();
			broadcastObjective();
		});
	}

	private static void randomizeBanners() {
		ProtectedRegion region = getWGUtils().getProtectedRegion(ReflectionGame.getPowderRg());
		List<Block> blocks = getWEUtils().getBlocks(region);
		for (Block block : blocks) {
			if (!block.getType().equals(Material.IRON_BLOCK))
				continue;

			Block banner = block.getRelative(0, 2, 0);
			Block banner1 = banner.getRelative(0, -5, 0);

			BlockData blockData = banner.getBlockData();
			BlockData blockData1 = banner1.getBlockData();

			Rotatable rotatable = (Rotatable) blockData;
			Rotatable rotatable1 = (Rotatable) blockData1;

			BlockFace newFace = RandomUtils.randomElement(ReflectionGame.getAngles());

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
		int ndx = ReflectionGame.getAngles().indexOf(blockFace) + 1;
		if (ndx == ReflectionGame.getAngles().size())
			ndx = 0;
		return ReflectionGame.getAngles().get(ndx);
	}
}
