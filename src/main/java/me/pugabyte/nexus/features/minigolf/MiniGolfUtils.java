package me.pugabyte.nexus.features.minigolf;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.features.minigolf.models.MiniGolfUser;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.SoundBuilder;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Slab;
import org.bukkit.util.Vector;

import java.util.UUID;

public class MiniGolfUtils {

	public static void respawnBall(GolfBall golfBall) {
		golfBall.respawn();
		sendActionBar(golfBall.getUser(), "&cOut of bounds!");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BASS).receiver(golfBall.getUser().getPlayer()).pitchStep(0).play();
	}

	public static boolean isBottomSlab(Block block) {
		return isSlab(block) && ((Slab) block.getBlockData()).getType() == Slab.Type.BOTTOM;
	}

	public static boolean isTopSlab(Block block) {
		return isSlab(block) && ((Slab) block.getBlockData()).getType() == Slab.Type.TOP;
	}

	private static boolean isSlab(Block block) {
		return Tag.SLABS.isTagged(block.getType());
	}

	public static Vector getDirection(BlockFace face, double power) {
		return switch (face) {
			case NORTH -> new Vector(0, 0, power);
			case SOUTH -> new Vector(0, 0, -power);
			case EAST -> new Vector(-power, 0, 0);
			case WEST -> new Vector(power, 0, 0);
			default -> null;
		};
	}

	public static void sendActionBar(MiniGolfUser user, String message) {
		if (!user.isOnline())
			return;

		ActionBarUtils.sendActionBar(user.getPlayer(), message, Time.SECOND.x(3));
	}

	public static String getScore(int strokes, int par) {
		if (strokes == 1)
			return "Hole In One";

		int diff = strokes - par;
		switch (diff) {
			case -4:
				return "Condor";
			case -3:
				return "Albatross";
			case -2:
				return "Eagle";
			case -1:
				return "Birdie";
			case 0:
				return "Par";
			case 1:
				return "Bogey";
			case 2:
				return "Double Bogey";
			case 3:
				return "Triple Bogey";
			default:
				if (diff < 4)
					return "" + diff;
				else
					return "+" + diff;
		}
	}

	public static MiniGolfUser getUser(UUID uuid) {
		return MiniGolf.getUsers()
			.stream()
			.filter(miniGolfUser -> miniGolfUser.getUuid().equals(uuid))
			.findFirst()
			.orElse(null);
	}

	public static String getStrokeString(MiniGolfUser user) {
		return "Stroke " + user.getGolfBall().getStrokes();
	}
}
