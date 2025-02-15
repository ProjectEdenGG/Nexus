package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21Quests;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfColor;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfHole;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21User;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BearFair21MiniGolfUtils {
	public static boolean isInMiniGolf(Location location) {
		return BearFair21.worldguard().getRegionsLikeAt(BearFair21MiniGolf.getGameRegion() + ".*", location).size() > 0;
	}

	public static String getStrokeString(MiniGolf21User user) {
		String strokes = "Stroke " + user.getCurrentStrokes();
		if (user.getMiniGolfColor().equals(BearFair21MiniGolfColor.RAINBOW))
			return StringUtils.Rainbow.apply(strokes);
		else
			return user.getChatColor() + strokes;
	}

	public static boolean isInBounds(MiniGolf21User user, Location location) {
		Material material = location.subtract(0, 0.1, 0).getBlock().getType();
		if (!BearFair21MiniGolf.getInBounds().contains(material))
			return false;

		return user.getCurrentHole().equals(getHole(location));
	}

	public static void checkHoleInOnes(MiniGolf21User user) {
		Set<BearFair21MiniGolfHole> userHoles = user.getHoleInOne();
		for (BearFair21MiniGolfHole hole : BearFair21MiniGolfHole.getHoles()) {
			if (!userHoles.contains(hole))
				return;
		}

		send(user, "You've unlocked the " + StringUtils.Rainbow.apply("Rainbow Ball") + "&3!");
		user.setRainbow(true);
		BearFair21MiniGolf.getService().save(user);
	}

	public static void checkCompleted(MiniGolf21User user) {
		Set<BearFair21MiniGolfHole> userCompleted = user.getCompleted();
		if (userCompleted.size() == BearFair21MiniGolfHole.getHoles().size())
			BearFair21Quests.giveTrophy(user, TrophyType.BEAR_FAIR_2021_MINIGOLF);
	}

	public static BearFair21MiniGolfHole getHole(Location location) {
		Set<ProtectedRegion> regions = BearFair21.worldguard().getRegionsLikeAt(BearFair21MiniGolf.getRegionHole() + ".*", location);
		ProtectedRegion region = regions.stream().findFirst().orElse(null);
		if (region != null) {
			for (BearFair21MiniGolfHole minigolfHole : BearFair21MiniGolfHole.values()) {
				String[] regionSplit = region.getId().replace(BearFair21MiniGolf.getRegionHole(), "").split("_");
				if (regionSplit[0].equalsIgnoreCase(String.valueOf(minigolfHole.getHole())))
					return minigolfHole;
			}
		}

		return null;
	}

	public static boolean isBottomSlab(Block block) {
		return Tag.SLABS.isTagged(block.getType()) && ((Slab) block.getBlockData()).getType() == Slab.Type.BOTTOM;
	}

	public static void giveBall(MiniGolf21User user) {
		if (user.getOnlinePlayer().isOnline())
			PlayerUtils.giveItem(user.getOnlinePlayer(), BearFair21MiniGolf.getGolfBall().clone().model(user.getMiniGolfColor().getModel()).build());
	}

	public static void respawnBall(Snowball ball) {
		MiniGolf21User user = getUser(ball);
		if (user == null)
			return;

		user.debug("found user from ball, respawning ball...");

		ball.setVelocity(new Vector(0, 0, 0));
		ball.setGravity(false);
		ball.teleport(user.getBallLocation().add(0, BearFair21MiniGolf.getFloorOffset(), 0));
		ball.setFireTicks(0);
		ball.setTicksLived(1);

		sendActionBar(user, "&cOut of bounds!");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BASS).receiver(user.getOnlinePlayer()).pitchStep(0).play();
	}

	public static MiniGolf21User getUser(Snowball ball) {
		for (MiniGolf21User user : new HashSet<>(BearFair21MiniGolf.getService().getUsers())) {
			if (user.getSnowball() == null)
				continue;

			if (!user.isOnline()) {
				user.debug("user is not online, removing golfball");
				user.removeBall();
				continue;
			}

			if (user.getSnowball().equals(ball))
				return user;
		}
		return null;
	}

	public static MiniGolf21User getUser(UUID uuid) {
		return BearFair21MiniGolf.getService().get(uuid);
	}

	public static void sendActionBar(MiniGolf21User user, String message) {
		if (!user.isOnline())
			return;

		ActionBarUtils.sendActionBar(user.getOnlinePlayer(), message, TickTime.SECOND.x(3));
	}

	public static void error(MiniGolf21User user, String message) {
		send(user, "&c" + message);
	}

	public static void send(MiniGolf21User user, String message) {
		user.getOnlinePlayer().sendMessage(BearFair21MiniGolf.getPREFIX() + StringUtils.colorize(message));
	}

	static String getScore(MiniGolf21User user) {
		int strokes = user.getCurrentStrokes();
		if (strokes == 1)
			return "Hole In One";

		int diff = strokes - user.getCurrentHole().getPar();
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
}
