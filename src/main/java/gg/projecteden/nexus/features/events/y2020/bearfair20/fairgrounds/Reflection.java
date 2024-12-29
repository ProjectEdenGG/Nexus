package gg.projecteden.nexus.features.events.y2020.bearfair20.fairgrounds;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.events.y2020.bearfair20.models.Laser;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.bearfair20.BearFair20User;
import gg.projecteden.nexus.models.bearfair20.BearFair20User.BF20PointSource;
import gg.projecteden.nexus.models.bearfair20.BearFair20UserService;
import gg.projecteden.nexus.utils.*;
import org.bukkit.*;
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

public class Reflection implements Listener {

	private WorldEditUtils worldedit = new WorldEditUtils(BearFair20.getWorld());
	private String gameRg = BearFair20.getRegion() + "_reflection";
	private String powderRg = gameRg + "_powder";
	private BF20PointSource SOURCE = BF20PointSource.REFLECTION;
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
	private Location center = new Location(BearFair20.getWorld(), -950, 137, -1689);
	private List<BlockFace> directions = Arrays.asList(BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST);
	private String prefix = "&8&l[&eReflection&8&l] &f";
	//
	private String objMsg = "null";
	private ColorType objColor;
	private int objReflections;
	private String objMob;

	public Reflection() {
		Nexus.registerListener(this);
		setLamps();
		newObjective();
	}

	private void setLamps() {
		ProtectedRegion region = BearFair20.worldguard().getProtectedRegion(powderRg);
		List<Block> blocks = worldedit.getBlocks(region);
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
		ProtectedRegion region = BearFair20.worldguard().getProtectedRegion(powderRg);
		List<Block> blocks = worldedit.getBlocks(region);
		for (Block block : blocks) {
			if (!MaterialTag.CONCRETE_POWDERS.isTagged(block.getType())) continue;

			Block banner = block.getRelative(0, 2, 0);
			BlockData blockData = banner.getBlockData();
			if (!(blockData instanceof Rotatable rotatable)) continue;

			if (block.getType().equals(Material.CYAN_CONCRETE_POWDER)) {
				BlockFace newFace = RandomUtils.randomElement(directions);
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
		if (!BearFair20.isAtBearFair(button)) return;

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
				Location skullLoc = LocationUtils.getCenteredLocation(powder.getRelative(0, 3, 0).getLocation());
				skullLoc.setY(skullLoc.getY() + 0.25);
				laserStart = skullLoc;

				BlockData blockDataDir = skullLoc.getBlock().getBlockData();
				if (!(blockDataDir instanceof Rotatable skullDir)) return;
				BlockFace skullFace = skullDir.getRotation().getOppositeFace();

				if (powderType.equals(Material.BLACK_CONCRETE_POWDER)) {
					if (prototypeActive) return;
					prototypeActive = true;
					new Laser(event.getPlayer(), skullLoc, skullFace, Color.BLUE, Material.FLETCHING_TABLE);
					Tasks.wait(TickTime.SECOND.x(2), () -> prototypeActive = false);

				} else {
					startLaser(event.getPlayer(), skullFace);
					buttonPresser = event.getPlayer();
				}
			}
		} else if (powderType.equals(Material.RED_CONCRETE_POWDER)) {
			Location skullLoc = LocationUtils.getCenteredLocation(powder.getRelative(0, 3, 0).getLocation());
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
		BearFair20.getWorld().playSound(laserStart, Sound.BLOCK_BEACON_ACTIVATE, 2F, 1F);
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
				if (middle == 0.5 && MaterialTag.STANDING_BANNERS.isTagged(bannerType) && cooldown.get() == 0) {
					loc[0] = LocationUtils.getCenteredLocation(loc[0]);
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
		soundTaskId = Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
			Collection<Player> players = BearFair20.worldguard().getPlayersInRegion(gameRg);
			for (Player player : players)
				player.playSound(laserSoundLoc, Sound.BLOCK_BEACON_AMBIENT, 1F, 1F);
		});
	}

	private void endLaser() {
		Tasks.cancel(laserTaskId);
		Tasks.cancel(soundTaskId);
		Collection<Player> players = BearFair20.worldguard().getPlayersInRegion(gameRg);
		for (Player player : players)
			player.stopSound(Sound.BLOCK_BEACON_AMBIENT);
		BearFair20.getWorld().playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
		Tasks.wait(TickTime.SECOND.x(2), () -> active = false);
	}

	private void win(int reflections) {
		BearFair20.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1F);

		String color = objColor.getChatColor() + StringUtils.camelCase(objColor.getName());
		Collection<Player> players = BearFair20.worldguard().getPlayersInRegion(gameRg);
		for (Player player : players)
			BearFair20.send(prefix + color + " " + objMob + " &fwas hit in " + reflections + " reflections!", player);

		if (BearFair20.giveDailyPoints) {
			BearFair20User user = new BearFair20UserService().get(buttonPresser);
			user.giveDailyPoints(SOURCE);
			new BearFair20UserService().save(user);
		}

		Tasks.wait(TickTime.SECOND.x(3), () -> {
			randomizeBanners();
			newObjective();
			broadcastObjective();
		});
	}

	private void newObjective() {
		List<ColorType> colors = Arrays.asList(ColorType.RED, ColorType.ORANGE, ColorType.YELLOW, ColorType.LIGHT_GREEN, ColorType.LIGHT_BLUE, ColorType.CYAN, ColorType.BLUE, ColorType.PURPLE, ColorType.PINK);
		List<String> mobs = Arrays.asList("Mooshroom", "Fox", "Bee", "Turtle", "Dolphin", "Guardian", "Squid", "Sheep", "Pig");

		if (objColor == null) {
			objColor = RandomUtils.randomElement(colors);
		} else {
			ColorType newColor = RandomUtils.randomElement(colors);
			for (int i = 0; i < 10; i++) {
				if (objColor.equals(newColor))
					newColor = RandomUtils.randomElement(colors);
				else
					break;
			}
			objColor = newColor;
		}

		objMob = mobs.get(colors.indexOf(objColor));

		String color = objColor.getChatColor() + StringUtils.camelCase(objColor.getName());

		objReflections = 0;
		if (RandomUtils.chanceOf(50))
			objReflections = RandomUtils.randomInt(4, 10);

		String reflections = "";
		if (objReflections > 0)
			reflections = " in " + objReflections + "+ reflections";

		objMsg = "Hit " + color + " " + objMob + "&f" + reflections;
	}

	private boolean checkObjective(int reflectCount, Material material) {
		boolean reflectBool = true;
		if (objReflections != 0)
			reflectBool = reflectCount >= objReflections;

		return reflectBool && objColor.equals(ColorType.of(material));
	}

	private void broadcastObjective() {
		Collection<Player> players = BearFair20.worldguard().getPlayersInRegion(gameRg);
		for (Player player : players) {
			BearFair20.send(prefix + objMsg, player);
		}
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			BearFair20.send(prefix + objMsg, event.getPlayer());
		}
	}
}
