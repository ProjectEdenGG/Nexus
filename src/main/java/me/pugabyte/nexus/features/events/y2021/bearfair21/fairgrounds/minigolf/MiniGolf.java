package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21User;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21UserService;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MiniGolf {
	// @formatter:off
	@Getter private static ItemStack putter;
	@Getter private static ItemStack wedge;
	@Getter private static ItemStack whistle;
	@Getter private static ItemStack golfBall;
	@Getter private static List<ItemStack> kit = new ArrayList<>();
	@Getter private static List<ItemStack> clubs = new ArrayList<>();
	// Data
	@Getter private static final MiniGolf21UserService service = new MiniGolf21UserService();
	// Constants
	@Getter private static final String PREFIX = StringUtils.getPrefix("MiniGolf");
	@Getter private static final double floorOffset = 0.05;
	@Getter private static final double maxVelLen = 2;
	@Getter private static final List<Material> inBounds = Arrays.asList(Material.GREEN_WOOL, Material.GREEN_CONCRETE, Material.PETRIFIED_OAK_SLAB);
	@Getter private static final String regionHole = "bearfair21_minigolf_hole_";
	// Attributes
	@Getter private static final AttributeModifier noDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", -10, Operation.ADD_NUMBER, EquipmentSlot.HAND);
	@Getter private static final AttributeModifier fastSwing = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 10, Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND);
	// Keys
	private static final Nexus instance = Nexus.getInstance();
	@Getter private static final NamespacedKey ballKey = new NamespacedKey(instance, "golf_ball");
	@Getter private static final NamespacedKey putterKey = new NamespacedKey(instance, "putter");
	@Getter private static final NamespacedKey wedgeKey = new NamespacedKey(instance, "wedge");
	@Getter private static final NamespacedKey whistleKey = new NamespacedKey(instance, "return_whistle");
	@Getter private static final NamespacedKey xKey = new NamespacedKey(instance, "x");
	@Getter private static final NamespacedKey yKey = new NamespacedKey(instance, "y");
	@Getter private static final NamespacedKey zKey = new NamespacedKey(instance, "z");
	// @formatter:on

	// TODO:
	//  add: scorecard book item

	public MiniGolf() {
		new ProjectileListener();
		new PuttListener();

		ballTask();
		powerTask();
		redstoneTask();
	}

	static {
		loadItems();
	}

	private static void loadItems() {
		ItemMeta meta;
		putter = new ItemBuilder(Material.IRON_HOE)
				.name("Putter")
				.customModelData(901)
				.lore("&7A specialized club", "&7for finishing holes.", "")
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.build();
		meta = putter.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getPutterKey());
		putter.setItemMeta(meta);

		wedge = new ItemBuilder(Material.IRON_HOE)
				.name("Wedge")
				.customModelData(903)
				.lore("&7A specialized club", "&7for tall obstacles", "")
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.build();
		meta = wedge.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getWedgeKey());
		wedge.setItemMeta(meta);

		whistle = new ItemBuilder(Material.IRON_NUGGET)
				.name("Golf Whistle")
				.lore("&7Returns your last", "&7hit golf ball to its", "&7previous location", "")
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.customModelData(901)
				.build();
		meta = whistle.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getWhistleKey());
		whistle.setItemMeta(meta);

		golfBall = new ItemBuilder(Material.SNOWBALL)
				.name("Golf Ball")
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.customModelData(901)
				.build();
		meta = golfBall.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getBallKey());
		golfBall.setItemMeta(meta);

		kit = Arrays.asList(getPutter(), getWedge(), getWhistle(), getGolfBall());
		clubs = Arrays.asList(getPutter(), getWedge());
	}

	public static void shutdown() {
		for (MiniGolf21User user : service.getUsers()) {
			Snowball snowball = user.getSnowball();
			if (snowball != null) {
				user.removeBall();
				giveBall(user);
			}
		}
	}

	public static void giveKit(Player player) {
		PlayerUtils.giveItems(player, kit);
	}

	public static void takeKit(Player player) {
		for (ItemStack item : kit)
			player.getInventory().remove(item);
	}

	private void redstoneTask() {
		String hole13 = regionHole + "13_activate";
		Location hole13Loc = new Location(BearFair21.getWorld(), 101, 119, -27);
		//
		// ...


		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(2), () -> {
			if (BearFair21.getWGUtils().getPlayersInRegion(hole13).size() > 0)
				hole13Loc.getBlock().setType(Material.REDSTONE_BLOCK);
		});
	}

	private void powerTask() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK, () -> {
			for (MiniGolf21User user : new HashSet<>(service.getUsers())) {
				OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(user.getUuid());
				if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null)
					continue;

				if (user.getSnowball() == null)
					continue;

				Player player = offlinePlayer.getPlayer();
				ItemStack tool = ItemUtils.getTool(player);
				if (ItemUtils.isNullOrAir(tool))
					continue;

				// quick fix
				ItemStack clone = tool.clone();
				clone.setAmount(1);
				boolean stop = true;
				for (ItemStack _item : MiniGolf.getClubs()) {
					if (ItemUtils.isFuzzyMatch(clone, _item))
						stop = false;
				}
				if (stop)
					continue;
				//

				if (player.getLevel() != 0)
					player.setLevel(0);

				double amount = player.spigot().getPing() < 200 ? 0.04 : 0.02;
				double exp = player.getExp() + amount;
				if (exp > 1.00) {
					exp = 0.00;
				}

				player.setExp((float) exp);
			}
		});
	}

	private void ballTask() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK, () -> {
			for (MiniGolf21User user : new HashSet<>(service.getUsers())) {
				Snowball ball = user.getSnowball();
				if (ball == null)
					continue;

				if (!ball.isValid()) {
					user.removeBall();
					continue;
				}

				// Check block underneath
				Location loc = ball.getLocation();
				Block block = loc.subtract(0, 0.1, 0).getBlock();

				Vector vel = ball.getVelocity();

				// Particles
				Particle particle = user.getParticle();
				if (particle != null && vel.length() > 0.01) {
					try {
						ParticleBuilder particleBuilder = new ParticleBuilder(particle).location(ball.getLocation()).count(1).extra(0);
						if (particle.equals(Particle.REDSTONE)) {
							if (user.isRainbow()) {
								int[] rgb = ParticleUtils.incRainbow(ball.getTicksLived());
								DustOptions dustOptions = ParticleUtils.newDustOption(particle, rgb[0], rgb[1], rgb[2]);
								particleBuilder.data(dustOptions);
							} else
								particleBuilder.color(user.getColor().getColor());
						}
						particleBuilder.spawn();
					} catch (Exception ignored) {
					}
				}

				// Act upon block type
				Material type = block.getType();
				Integer ballHole = getHole(ball.getLocation());
				switch (type) {
					case CAULDRON:
						// Check speed
						if (vel.getY() >= 0 && vel.length() > 0.34)
							continue;

						if (ballHole == null || !ballHole.equals(user.getCurrentHole())) {
							respawnBall(ball);
							continue;
						}

						// Halt velocity
						ball.setVelocity(new Vector(0, ball.getVelocity().getY(), 0));

						// Remove ball
						user.removeBall();

						// Spawn firework
						Tasks.wait(Time.TICK, () -> new FireworkLauncher(loc)
								.power(0)
								.detonateAfter(Time.TICK.x(2))
								.type(Type.BURST)
								.colors(Collections.singletonList(user.getColor().getColor()))
								.fadeColors(Collections.singletonList(Color.WHITE))
								.launch());

						// Send message
						sendActionBar(user, "&6Stroke: " + user.getCurrentStrokes());
						giveBall(user);

						user.incTotalStrokes();
						user.setCurrentHole(null);
						user.setCurrentStrokes(0);
						break;
					case AIR:
					case WATER:
					case LAVA:
					case CRIMSON_HYPHAE:
					case PURPLE_STAINED_GLASS:
						// Fall
						ball.setGravity(true);
						break;
					case ICE:
					case PACKED_ICE:
					case BLUE_ICE:
						// No friction, constant speed
						ball.setVelocity(vel);
						break;
					case SLIME_BLOCK:
						// Bounce, with no friction
						vel.setY(0.30);
						ball.setVelocity(vel);
						break;
					case REDSTONE_BLOCK:
						// Boost
						if (vel.length() < maxVelLen)
							ball.setVelocity(vel.multiply(1.3));
						break;
					case SOUL_SOIL:
						// Stop bouncing
						vel.setY(0);
						ball.setVelocity(vel);
					case SAND:
					case RED_SAND:
						// Friction
						vel.multiply(0.9);
						ball.setVelocity(vel);
						break;
					case MAGENTA_GLAZED_TERRACOTTA:
						// Get Direction
						Directional directional = (Directional) block.getBlockData();

						Vector newVel;
						switch (directional.getFacing()) {
							case NORTH:
								newVel = new Vector(0, 0, 0.1);
								break;
							case SOUTH:
								newVel = new Vector(0, 0, -0.1);
								break;
							case EAST:
								newVel = new Vector(-0.1, 0, 0);
								break;
							case WEST:
								newVel = new Vector(0.1, 0, 0);
								break;
							default:
								continue;
						}

						// Push ball
						ball.setVelocity(vel.multiply(9.0).add(newVel).multiply(0.1)); // 9.0

						break;
					case OBSERVER:
						// Get Direction
						directional = (Directional) block.getBlockData();

						switch (directional.getFacing()) {
							case NORTH:
								newVel = new Vector(0, 0, 0.5);
								break;
							case SOUTH:
								newVel = new Vector(0, 0, -0.5);
								break;
							case EAST:
								newVel = new Vector(-0.5, 0, 0);
								break;
							case WEST:
								newVel = new Vector(0.5, 0, 0);
								break;
							default:
								continue;
						}

						if (vel.length() < maxVelLen) {
							// Push ball
							ball.setVelocity(vel.multiply(9.3).add(newVel).multiply(0.1));
						}

						break;
					case DISPENSER:
						Block below = block.getRelative(BlockFace.DOWN);
						if (MaterialTag.SIGNS.isTagged(below.getType())) {
							Sign sign = (Sign) below.getState();
							String line4 = sign.getLine(3);
							String[] split = line4.split(",");
							if (split.length == 3) {
								try {
									Location newLoc = new Location(ball.getWorld(),
											Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
									ball.setVelocity(new Vector(0, 0, 0));
									ball.teleport(LocationUtils.getCenteredLocation(newLoc));
									ball.setGravity(true);
								} catch (Exception ignored) {
								}
							}
						}

						break;
					default:
						// Check if floating above slabs
						if (isBottomSlab(block) && loc.getY() > block.getY() + 0.5)
							ball.setGravity(true);

						// Stop & respawn ball if slow enough
						if (vel.getY() >= 0 && vel.length() <= 0.01) {
							ball.setVelocity(new Vector(0, 0, 0));
							ball.teleport(ball.getLocation());
							ball.setGravity(false);

							if (!inBounds.contains(type))
								MiniGolf.respawnBall(ball);
							else {
								if (!user.getCurrentHole().equals(ballHole))
									MiniGolf.respawnBall(ball);
							}

							break;
						}

						// Slight friction
						vel.multiply(0.975);
						ball.setVelocity(vel);
						break;
				}
			}
		});
	}

	public static Integer getHole(Location location) {
		Set<ProtectedRegion> regions = BearFair21.getWGUtils().getRegionsLikeAt(regionHole + ".*", location);
		ProtectedRegion region = regions.stream().findFirst().orElse(null);
		if (region == null)
			return null;

		String[] split = region.getId().split("_");
		return Integer.parseInt(split[3]);
	}

	public static boolean isBottomSlab(Block block) {
		return Tag.SLABS.isTagged(block.getType()) && ((Slab) block.getBlockData()).getType() == Slab.Type.BOTTOM;
	}

	public static void giveBall(MiniGolf21User user) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(user.getUuid());
		if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
			giveBall(offlinePlayer.getPlayer());
		}
	}

	public static void giveBall(Player player) {
		if (player == null)
			return;
		PlayerUtils.giveItem(player, golfBall.clone());
	}

	public static void addKey(ItemMeta meta, NamespacedKey key) {
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 0);
	}

	public static boolean hasKey(ItemMeta meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
	}

	public static void respawnBall(Snowball ball) {
		MiniGolf21User user = getUser(ball);
		if (user == null)
			return;

		PersistentDataContainer c = ball.getPersistentDataContainer();

		double x = c.get(MiniGolf.getXKey(), PersistentDataType.DOUBLE);
		double y = c.get(MiniGolf.getYKey(), PersistentDataType.DOUBLE);
		double z = c.get(MiniGolf.getZKey(), PersistentDataType.DOUBLE);

		ball.setVelocity(new Vector(0, 0, 0));
		ball.teleport(new Location(ball.getWorld(), x, y, z));
		ball.setGravity(false);
		ball.setFireTicks(0);
		ball.setTicksLived(1);

		sendActionBar(user, "&cOut of bounds!");
	}

	public static MiniGolf21User getUser(Snowball ball) {
		for (MiniGolf21User user : new HashSet<>(service.getUsers())) {
			if (user.getSnowball() == null)
				continue;

			if (!user.isOnline()) {
				user.removeBall();
				continue;
			}

			if (user.getSnowball().equals(ball))
				return user;
		}
		return null;
	}

	public static MiniGolf21User getUser(UUID uuid) {
		return service.get(uuid);
	}


	public static void sendActionBar(MiniGolf21User user, String message) {
		if (!user.isOnline())
			return;

		ActionBarUtils.sendActionBar(user.getPlayer(), message, Time.SECOND.x(3));
	}

	public static void error(Player player, String message) {
		player.sendMessage(PREFIX + StringUtils.colorize("&c" + message));
	}

	private static String getScore(int par, int strokes) {
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
					return "-" + diff;
				else
					return "+" + diff;

		}
	}
}
