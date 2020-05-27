package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.models.Laser;
import me.pugabyte.bncore.features.particles.effects.DotEffect;
import me.pugabyte.bncore.models.bearfair.BFPointsService;
import me.pugabyte.bncore.models.bearfair.BFPointsUser;
import me.pugabyte.bncore.models.bearfair.BFPointsUser.BFPointSource;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.BFRg;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.NORTH_EAST;
import static org.bukkit.block.BlockFace.NORTH_WEST;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.SOUTH_EAST;
import static org.bukkit.block.BlockFace.SOUTH_WEST;
import static org.bukkit.block.BlockFace.WEST;

public class Reflection implements Listener {

	private WorldEditUtils WEUtils = new WorldEditUtils(BearFair20.world);
	private String gameRg = BearFair20.BFRg + "_reflection";
	private String powderRg = gameRg + "_powder";
	private BFPointSource SOURCE = BFPointSource.REFLECTION;
	//
	private boolean active = false;
	private boolean prototypeActive = false;
	private int laserTaskId;
	private int soundTaskId;
	private Location laserStart;
	private Location laserSoundLoc;
	private Player buttonPresser;
	//
	private List<Location> lampLocList = new ArrayList<>();
	private Location center = new Location(BearFair20.world, -950, 137, -1689);
	private List<BlockFace> directions = Arrays.asList(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST);
	private String prefix = "&8&l[&eReflection&8&l] &f";
	//
	private String objMsg = "null";
	private ColorType objColor;
	private int objReflections;
	private String objMob;

	public Reflection() {
		BNCore.registerListener(this);
		setLamps();
		newObjective();
	}

	private void setLamps() {
		ProtectedRegion region = WGUtils.getProtectedRegion(powderRg);
		List<Block> blocks = WEUtils.getBlocks(region);
		for (Block block : blocks) {
			if (block.getType().equals(Material.YELLOW_CONCRETE_POWDER)) {
				Location loc = block.getRelative(0, 3, 0).getLocation();
				lampLocList.add(loc);
			}
		}
	}

	private void clearLamps() {
		for (Location lampLoc : lampLocList) {
			Block lamp = lampLoc.getBlock();
			BlockData blockData = lamp.getBlockData();
			Lightable lightable = (Lightable) blockData;
			lightable.setLit(false);
			lamp.setBlockData(lightable);
		}
	}

	private void randomizeBanners() {
		ProtectedRegion region = WGUtils.getProtectedRegion(powderRg);
		List<Block> blocks = WEUtils.getBlocks(region);
		for (Block block : blocks) {
			if (!MaterialTag.CONCRETE_POWDERS.isTagged(block.getType())) continue;

			Block banner = block.getRelative(0, 2, 0);
			BlockData blockData = banner.getBlockData();
			if (!(blockData instanceof Rotatable)) continue;
			Rotatable rotatable = (Rotatable) blockData;

			if (block.getType().equals(Material.CYAN_CONCRETE_POWDER)) {
				BlockFace newFace = Utils.getRandomElement(directions);
				if (newFace == null) continue;

				rotatable.setRotation(newFace);
				banner.setBlockData(rotatable);
			}
		}
	}

	@EventHandler
	public void onButtonPress(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (event.getHand() == null) return;
		if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		if (!event.getClickedBlock().getType().equals(Material.STONE_BUTTON)) return;

		Block button = event.getClickedBlock();
		Location loc = button.getLocation();
		if (!WGUtils.getRegionNamesAt(loc).contains(BFRg)) return;

		BlockData blockData = button.getBlockData();
		Directional directional = (Directional) blockData;
		Block powder = button.getRelative(0, -1, 0).getRelative(directional.getFacing().getOppositeFace());
		Material powderType = powder.getType();
		if (!MaterialTag.CONCRETE_POWDERS.isTagged(powderType)) return;

		if (powderType.equals(Material.CYAN_CONCRETE_POWDER)) {
			Block banner = powder.getRelative(0, 2, 0);
			rotateBanner(banner);
		} else if (powderType.equals(Material.WHITE_CONCRETE_POWDER) || powderType.equals(Material.BLACK_CONCRETE_POWDER)) {
			if (!active) {
				Location skullLoc = Utils.getCenteredLocation(powder.getRelative(0, 3, 0).getLocation());
				skullLoc.setY(skullLoc.getY() + 0.25);
				laserStart = skullLoc;

				BlockData blockDataDir = skullLoc.getBlock().getBlockData();
				if (!(blockDataDir instanceof Rotatable)) return;
				Rotatable skullDir = (Rotatable) blockDataDir;
				BlockFace skullFace = skullDir.getRotation().getOppositeFace();

				if (powderType.equals(Material.BLACK_CONCRETE_POWDER)) {
					if (prototypeActive) return;
					prototypeActive = true;
					new Laser(event.getPlayer(), skullLoc, skullFace, Color.BLUE, Material.FLETCHING_TABLE);
					Tasks.wait(Time.SECOND.x(2), () -> prototypeActive = false);

				} else {
					startLaser(event.getPlayer(), skullFace);
					buttonPresser = event.getPlayer();
				}
			}
		} else if (powderType.equals(Material.RED_CONCRETE_POWDER)) {
			Location skullLoc = Utils.getCenteredLocation(powder.getRelative(0, 3, 0).getLocation());
			skullLoc.setY(skullLoc.getY() + 0.25);
			skullLoc.getWorld().spawnParticle(Particle.LAVA, skullLoc, 5, 0, 0, 0);
			skullLoc.getWorld().playSound(skullLoc, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.5F, 1F);
			Tasks.wait(5, () -> skullLoc.getWorld().spawnParticle(Particle.LAVA, skullLoc, 5, 0, 0, 0));
			Tasks.wait(10, () -> skullLoc.getWorld().spawnParticle(Particle.LAVA, skullLoc, 5, 0, 0, 0));
		}
	}

	private void rotateBanner(Block banner) {
		BlockData blockData = banner.getBlockData();
		Rotatable rotatable = (Rotatable) blockData;
		rotatable.setRotation(rotateBlockFace(rotatable.getRotation()));
		banner.setBlockData(rotatable);
	}

	private BlockFace rotateBlockFace(BlockFace blockFace) {
		int ndx = directions.indexOf(blockFace) + 1;
		if (ndx == directions.size())
			ndx = 0;
		return directions.get(ndx);
	}

	private void startLaser(Player player, BlockFace startFace) {
		active = true;
		clearLamps();
		AtomicInteger cooldown = new AtomicInteger(5);
		AtomicInteger lifespan = new AtomicInteger(750);
		final BlockFace[] blockFace = {startFace};
		final Location[] loc = {laserStart.clone()};
		AtomicReference<Color> laserColor = new AtomicReference<>(Color.RED);
		AtomicInteger reflections = new AtomicInteger(0);
		BearFair20.world.playSound(laserStart, Sound.BLOCK_BEACON_ACTIVATE, 10F, 1F);
		laserSound();

		laserTaskId = Tasks.repeat(0, 1, () -> {
			if (active) {
				laserSoundLoc = loc[0].clone();
				DotEffect.builder().player(player).location(loc[0].clone()).speed(0.1).ticks(10).color(laserColor.get()).start();
				Block block = loc[0].getBlock();
				Material blockType = block.getType();

				double middle = loc[0].getX() - loc[0].getBlockX();
				if (middle == 0.5 && !blockType.equals(Material.AIR) && cooldown.get() == 0) {
					boolean broadcast = true;
					if (blockType.equals(Material.REDSTONE_LAMP)) {
						if (checkObjective(reflections.get(), block.getRelative(0, 1, 0).getType())) {
							BlockData blockData = block.getBlockData();
							Lightable lightable = (Lightable) blockData;
							lightable.setLit(true);
							block.setBlockData(lightable);
							win(reflections.get());
							broadcast = false;
						}
					}
					if (broadcast)
						broadcastObjective();
					endLaser();
					return;
				}

				Block below = block.getRelative(0, -1, 0);
				Material bannerType = below.getType();
				if (middle == 0.5 && MaterialTag.BANNERS.isTagged(bannerType) && cooldown.get() == 0) {
					loc[0] = Utils.getCenteredLocation(loc[0]);
					loc[0].setY(loc[0].getY() + 0.25);
					Rotatable rotatable = (Rotatable) below.getBlockData();
					BlockFace newFace = Laser.getReflection(blockFace[0], rotatable.getRotation());
					if (newFace == null) {
						endLaser();
						return;
					}
					if (!blockFace[0].equals(newFace))
						reflections.incrementAndGet();
					blockFace[0] = newFace;
					cooldown.set(5);
				}

				loc[0] = loc[0].clone().add(blockFace[0].getDirection().multiply(0.25));
				lifespan.getAndDecrement();

				if (cooldown.get() > 0) {
					cooldown.getAndDecrement();
				}

				int curLifespan = lifespan.get();
				if (curLifespan <= 0) {
					endLaser();
				}

				if (curLifespan <= 300) {
					if (curLifespan <= 100)
						laserColor.set(Color.YELLOW);
					else
						laserColor.set(Color.ORANGE);
				}
			} else {
				endLaser();
			}
		});
	}

	private void laserSound() {
		soundTaskId = Tasks.repeat(0, Time.SECOND.x(5), () -> {
			Collection<Player> players = WGUtils.getPlayersInRegion(gameRg);
			for (Player player : players)
				player.playSound(laserSoundLoc, Sound.BLOCK_BEACON_AMBIENT, 1F, 1F);
		});
	}

	private void endLaser() {
		Tasks.cancel(laserTaskId);
		Tasks.cancel(soundTaskId);
		Collection<Player> players = WGUtils.getPlayersInRegion(gameRg);
		for (Player player : players)
			player.stopSound(Sound.BLOCK_BEACON_AMBIENT);
		BearFair20.world.playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
		Tasks.wait(Time.SECOND.x(2), () -> active = false);
	}

	private void win(int reflections) {
		BearFair20.world.playSound(center, Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1F);

		String color = objColor.getChatColor() + camelCase(objColor.getName());
		Collection<Player> players = WGUtils.getPlayersInRegion(gameRg);
		for (Player player : players)
			player.sendMessage(colorize(prefix + color + " " + objMob + " &fwas hit in " + reflections + " reflections!"));

		BFPointsUser user = new BFPointsService().get(buttonPresser);
		user.giveDailyPoints(1, SOURCE);
		new BFPointsService().save(user);

		Tasks.wait(Time.SECOND.x(3), () -> {
			randomizeBanners();
			newObjective();
			broadcastObjective();
		});
	}

	private void newObjective() {
		List<ColorType> colors = Arrays.asList(ColorType.RED, ColorType.ORANGE, ColorType.YELLOW, ColorType.LIGHT_GREEN, ColorType.LIGHT_BLUE, ColorType.CYAN, ColorType.BLUE, ColorType.PURPLE, ColorType.PINK);
		List<String> mobs = Arrays.asList("Mooshroom", "Fox", "Bee", "Turtle", "Dolphin", "Guardian", "Squid", "Sheep", "Pig");

		if (objColor == null) {
			objColor = Utils.getRandomElement(colors);
		} else {
			ColorType newColor = Utils.getRandomElement(colors);
			for (int i = 0; i < 10; i++) {
				if (objColor.equals(newColor))
					newColor = Utils.getRandomElement(colors);
				else
					break;
			}
			objColor = newColor;
		}

		objMob = mobs.get(colors.indexOf(objColor));


		String color = objColor.getChatColor() + camelCase(objColor.getName());

		objReflections = 0;
		if (Utils.chanceOf(50))
			objReflections = Utils.randomInt(4, 10);

		String reflections = "";
		if (objReflections > 0)
			reflections = " in " + objReflections + "+ reflections";

		objMsg = "Hit " + color + " " + objMob + "&f" + reflections;
	}

	private boolean checkObjective(int reflectCount, Material material) {
		boolean reflectBool = true;
		if (objReflections != 0)
			reflectBool = reflectCount >= objReflections;

		return reflectBool && objColor.equals(ColorType.fromMaterial(material));
	}

	private void broadcastObjective() {
		Collection<Player> players = WGUtils.getPlayersInRegion(gameRg);
		for (Player player : players) {
			player.sendMessage(colorize(prefix + objMsg));
		}
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			event.getPlayer().sendMessage(colorize(prefix + objMsg));
		}
	}
}
