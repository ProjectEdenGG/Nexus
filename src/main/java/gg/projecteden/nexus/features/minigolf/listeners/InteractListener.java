package gg.projecteden.nexus.features.minigolf.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.menus.MiniGolfScorecardMenu;
import gg.projecteden.nexus.features.minigolf.models.blocks.BounceBlock;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallSpawnEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfUserPlaceBallEvent;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public class InteractListener implements Listener {

	public InteractListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPutt(PlayerInteractEvent event) {
		if (event.getHand() == null) return;
		if (!event.getHand().equals(EquipmentSlot.HAND)) return;

		UUID uuid = event.getPlayer().getUniqueId();
		MiniGolfUser user = new MiniGolfUserService().get(uuid);
		if (user == null)
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		Block block = event.getClickedBlock();

		// Whistle
		if (ItemUtils.isFuzzyMatch(item, MiniGolfUtils.WHISTLE)) {
			recallBall(event, user);
			event.setCancelled(true);
			return;
		}

		// Whistle
		if (ItemUtils.isFuzzyMatch(item, MiniGolfUtils.SCORECARD)) {
			scorecard(event, user);
			event.setCancelled(true);
			return;
		}

		// Golfball
		if (item.getType() == Material.SNOWBALL && ItemBuilder.Model.hasModel(item)) {
			placeBall(event, user, item, block);
			event.setCancelled(true);
			return;
		}

		// Club
		if (MiniGolfUtils.isClub(item)) {
			boolean isWedge = ItemUtils.isFuzzyMatch(item, MiniGolfUtils.WEDGE);
			puttBall(event, user, isWedge);
			event.setCancelled(true);
			return;
		}
	}

	private void scorecard(PlayerInteractEvent event, MiniGolfUser user) {
		MiniGolfCourse course = user.getCurrentCourse();
		if (course == null) {
			user.sendMessage(MiniGolf.PREFIX + "You are not in a course");
			return;
		}

		new MiniGolfScorecardMenu(user, course).open();
	}

	private void recallBall(PlayerInteractEvent event, MiniGolfUser user) {
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event))
			return;

		GolfBall golfBall = user.getGolfBall();
		if (golfBall == null || !golfBall.isAlive()) {
			user.sendMessage("You don't have an active golfball");
			return;
		}

		golfBall.reset();
	}

	private static void placeBall(PlayerInteractEvent event, MiniGolfUser user, ItemStack item, Block block) {
		user.debug("placing golf ball...");

		if (user.getGolfBall() != null && user.getGolfBall().getSnowball() != null) {
			user.debug("you already have a ball placed");
			event.setCancelled(true);
			return;
		}

		if (Nullables.isNullOrAir(block)) {
			user.debug("placed on block is air or null");
			event.setCancelled(true);
			return;
		}

		GolfBall golfBall = new GolfBall(user.getUuid());

		// Verify region
		WorldGuardUtils worldguard = new WorldGuardUtils(block);
		Set<ProtectedRegion> regions = worldguard.getRegionsLikeAt(MiniGolf.HOLE_REGION_REGEX, block.getLocation());
		ProtectedRegion region = regions.stream().findFirst().orElse(null);
		if (region == null) {
			user.debug("hole region not found");
			event.setCancelled(true);
			return;
		}

		String regionId = region.getId();
		user.debug("hole region original = " + regionId);
		String extra = regionId.replaceAll(".*_minigolf_hole_[0-9]+", "");
		user.debug("extra = " + extra);
		regionId = StringUtils.replaceLast(regionId, extra, "");
		user.debug("regionId = " + regionId);
		String[] split = regionId.split("_minigolf_hole_");
		golfBall.setCourseId(split[0]);
		golfBall.setHoleId(Integer.parseInt(split[1]));
		user.debug("course = " + golfBall.getCourseId());
		user.debug("hole = " + golfBall.getHoleId());

		// Place Event
		MiniGolfUserPlaceBallEvent placeBallEvent = new MiniGolfUserPlaceBallEvent(user, golfBall, Set.of(Material.GREEN_WOOL));
		if (!placeBallEvent.callEvent()) {
			user.debug("place ball event cancelled");
			event.setCancelled(true);
			return;
		}

		if (!placeBallEvent.canPlaceBall(block.getType())) {
			user.debug("incorrect starting position");
			event.setCancelled(true);
			return;
		}

		// Spawn Event
		MiniGolfBallSpawnEvent ballSpawnEvent = new MiniGolfBallSpawnEvent(golfBall, block.getLocation());
		if (!ballSpawnEvent.callEvent()) {
			user.debug("place spawn event cancelled");
			event.setCancelled(true);
			return;
		}

		item.setAmount(item.getAmount() - 1);
		ballSpawnEvent.spawnBall();
		user.setGolfBall(golfBall);
	}

	private static void puttBall(PlayerInteractEvent event, MiniGolfUser user, boolean isWedge) {
		Player player = user.getOnlinePlayer();

		GolfBall golfBall = user.getGolfBall();
		if (golfBall == null || !golfBall.isAlive()) {
			user.debug("golfball is null or dead, returning");
			return;
		}

		Vector dir = player.getEyeLocation().getDirection();
		Location entityLoc = golfBall.getBallLocation();

		if (!isLookingAtGolfBall(user))
			return;

		if (!Utils.ActionGroup.LEFT_CLICK.applies(event)) {
			golfBall.pickup();
			return;
		}

		if (!golfBall.isMinVelocity()) {
			user.sendMessage("You cannot hit a golfball while it's in motion");
			return;
		}

		// Hit golf ball
		golfBall.setActive(true);
		dir.setY(0).normalize();

		double power = MiniGolf.POWER_MAP.getOrDefault(player.getUniqueId(), .0f);
		if (power >= 0.90)
			power = 1.0;
		else if (power < 0.16)
			power = 0.16;

		dir.multiply(power);
		if (isWedge)
			dir.setY(BounceBlock.BOUNCE_XZ); // 0.25

		// Update stroke
		golfBall.incStrokes();
		golfBall.setName(MiniGolfUtils.getStrokeString(user));

		// Update last pos
		if (golfBall.isInBounds())
			golfBall.setLastLocation(entityLoc);

		golfBall.setTicksLived(1);
		golfBall.setVelocity(dir);
		new SoundBuilder(Sound.BLOCK_METAL_HIT).location(entityLoc).volume(0.75).pitch(1.25).play();

		ActionBarUtils.sendActionBar(player, "&6Power: " + MiniGolfUtils.getPowerDisplay(power), TimeUtils.TickTime.SECOND.x(3));
		user.debug("&6Power: " + MiniGolfUtils.getPowerDisplay(power));
	}

	private static boolean isLookingAtGolfBall(MiniGolfUser user) {
		GolfBall golfBall = user.getGolfBall();
		if (golfBall == null || !golfBall.isAlive())
			return false;

		if (Distance.distance(golfBall.getBallLocation(), user.getOnlinePlayer()).gt(3.5))
			return false;

		Location eye = user.getOnlinePlayer().getEyeLocation();
		Vector dir = eye.getDirection();

		Location entityLoc = golfBall.getBallLocation();
		Vector vec = entityLoc.toVector().subtract(eye.toVector());

		return dir.angle(vec) < 0.15f;
	}
}
