package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigolf.models.GolfBallColor;
import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Slab;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class MiniGolfUtils {

	public static void debugDot(Location location, ColorType color) {
		for (MiniGolfUser user : MiniGolf.getUsers()) {
			user.debugDot(location, color);
		}
	}

	@Getter
	private static final ItemStack putter = new ItemBuilder(CustomMaterial.MINIGOLF_PUTTER)
		.name("Putter")
		.lore("&7A specialized club", "&7for finishing holes.", "")
		.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
		.undroppable()
		.build();

	@Getter
	private static final ItemStack wedge = new ItemBuilder(CustomMaterial.MINIGOLF_WEDGE)
		.name("Wedge")
		.lore("&7A specialized club", "&7for tall obstacles", "")
		.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
		.undroppable()
		.build();

	@Getter
	private static final ItemStack whistle = new ItemBuilder(CustomMaterial.MINIGOLF_WHISTLE)
		.name("Golf Whistle")
		.lore("&7Returns your last", "&7hit golf ball to its", "&7previous location", "")
		.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
		.undroppable()
		.build();

	private static final ItemBuilder golfBall = new ItemBuilder(CustomMaterial.MINIGOLF_BALL)
		.name("Golf Ball")
		.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
		.undroppable();

	public static ItemBuilder getGolfBall() {
		return golfBall.clone();
	}

	public static List<ItemStack> getKit(GolfBallColor color) {
		return List.of(getPutter(), getWedge(), getWhistle(), getGolfBall(color));
	}

	public static boolean isClub(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return false;

		return ItemUtils.isFuzzyMatch(item, getPutter()) || ItemUtils.isFuzzyMatch(item, getWedge());
	}

	public static ItemStack getGolfBall(GolfBallColor color) {
		return getGolfBall().model(color.getModel()).build();
	}

	public static boolean isBottomSlab(Block block) {
		return Tag.SLABS.isTagged(block.getType()) && ((Slab) block.getBlockData()).getType() == Slab.Type.BOTTOM;
	}

	public static boolean isFloating(Location location, Block below, double belowHeight) {
		double ballHeight = location.getY() - 0.1;
		double floatingHeight = below.getY() + belowHeight + MiniGolf.getFloorOffset();

		return ballHeight > floatingHeight;
	}

	public static boolean isFloatingOnUniqueCollision(Location location, Block below) {
		if (below == null)
			return false;

		Material material = below.getType();

		// Bottom Slab
		if (isBottomSlab(below) && isFloating(location, below, 0.5))
			return true;

		// Trapdoor
		if (MaterialTag.TRAPDOORS.isTagged(material) && isFloating(location, below, 0.1875))
			return true;

		return false;
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

		ActionBarUtils.sendActionBar(user.getPlayer(), message, TickTime.SECOND.x(3));
	}

	public static String getScore(int strokes, int par) {
		if (strokes == 1)
			return "Hole In One";

		int diff = strokes - par;
		return switch (diff) {
			case -4 -> "Condor";
			case -3 -> "Albatross";
			case -2 -> "Eagle";
			case -1 -> "Birdie";
			case 0 -> "Par";
			case 1 -> "Bogey";
			case 2 -> "Double Bogey";
			case 3 -> "Triple Bogey";
			default -> diff < 4 ? "" + diff : "+" + diff;
		};
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

	public static void giveBall(MiniGolfUser user) {
		if (!user.getOnlinePlayer().isOnline())
			return;

		PlayerUtils.giveItem(user.getOnlinePlayer(), getGolfBall(user.getGolfBallColor()));

	}

	public static String getPowerDisplay(double power) {
		int result = (int) (power * 100);

		String color = "&a";
		if (result >= 70)
			color = "&c";
		else if (result >= 50)
			color = "&e";

		return color + result;
	}
}
