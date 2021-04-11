package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
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
//	@Getter private static final List<Snowball> golfBalls = new ArrayList<>();
	@Getter private static final Set<MiniGolfUser> users = new HashSet<>();
	// Constants
	@Getter private static final double floorOffset = 0.05;
	@Getter private static final double maxVelLen = 2;
	@Getter private static final List<Material> inBounds = Arrays.asList(Material.GREEN_WOOL, Material.GREEN_CONCRETE, Material.PETRIFIED_OAK_SLAB);
	// Attributes
	@Getter private static final AttributeModifier noDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", -10, Operation.ADD_NUMBER, EquipmentSlot.HAND);
	@Getter private static final AttributeModifier fastSwing = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 10, Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND);
	// Keys
	private static final Nexus instance = Nexus.getInstance();
	@Getter private static final NamespacedKey ballKey = new NamespacedKey(instance, "golf_ball");
	@Getter private static final NamespacedKey putterKey = new NamespacedKey(instance, "putter");
	@Getter private static final NamespacedKey wedgeKey = new NamespacedKey(instance, "wedge");
	@Getter private static final NamespacedKey whistleKey = new NamespacedKey(instance, "return_whistle");
	@Getter private static final NamespacedKey parKey = new NamespacedKey(instance, "par");
	@Getter private static final NamespacedKey xKey = new NamespacedKey(instance, "x");
	@Getter private static final NamespacedKey yKey = new NamespacedKey(instance, "y");
	@Getter private static final NamespacedKey zKey = new NamespacedKey(instance, "z");
	// @formatter:on

	// TODO:
	//  add: scorecard

	public MiniGolf() {
		new ProjectileListener();
		new PuttListener();

		ballTask();
		playerPowerTask();
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
		for (MiniGolfUser user : MiniGolf.getUsers()) {
			Snowball snowball = user.getSnowball();
			if (snowball != null) {
				snowball.remove();
				user.setSnowball(null);
				giveBall(user.getUuid());
			}
		}
	}

	private void playerPowerTask() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK, () -> {
			for (MiniGolfUser user : new HashSet<>(users)) {
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
			for (MiniGolfUser user : new HashSet<>(users)) {
				if (user == null) {
					users.remove(null);
					continue;
				}

				Snowball ball = user.getSnowball();

				if (ball == null)
					continue;

				if (!ball.isValid()) {
					ball.remove();
					user.setSnowball(null);
					continue;
				}

				// TODO: Do differently
				// Drop if older than 1 minute
				if (ball.getTicksLived() > Time.MINUTE.get()) {
					dropBall(ball);
					ball.remove();
					user.setSnowball(null);
					continue;
				}

				// Check block underneath
				Location loc = ball.getLocation();
				Block block = loc.subtract(0, 0.1, 0).getBlock();

				// Act upon block type
				Vector vel = ball.getVelocity();
				Material type = block.getType();
				switch (type) {
					case CAULDRON:
						// Check speed
						if (vel.getY() >= 0 && vel.length() > 0.34)
							continue;

						Player player = getPlayer(ball);

						// Halt velocity
						ball.setVelocity(new Vector(0, ball.getVelocity().getY(), 0));

						// Remove ball
						user.setSnowball(null);
						ball.remove();

						// Spawn firework
						Tasks.wait(Time.TICK, () -> FireworkLauncher.random(loc)
								.power(0)
								.detonateAfter(Time.TICK.x(2))
								.type(Type.BURST)
								.launch());

						// Send message
						sendActionBar(player, "&6Stroke: " + getStroke(ball));
						giveBall(player);
						break;
					case AIR:
					case WATER:
					case LAVA:
					case CRIMSON_HYPHAE:
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
					case SOUL_SOIL:
						// Stop bouncing, with no friction
						vel.setY(0);
						ball.setVelocity(vel);
						break;
					case REDSTONE_BLOCK:
						// Boost
						if (vel.length() < maxVelLen)
							ball.setVelocity(vel.multiply(1.3));
						break;
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
									ball.teleport(newLoc);
									ball.setGravity(true);
								} catch (Exception ignored) {
								}
							}
						}

						break;
					default:
						// Check if floating above slabs
						if (isBottomSlab(block) && loc.getY() > block.getY() + 0.5) {
							ball.setGravity(true);
						}

						// Stop & respawn ball if slow enough
						if (vel.getY() >= 0 && vel.length() <= 0.01) {
							ball.setVelocity(new Vector(0, 0, 0));
							ball.teleport(ball.getLocation());
							ball.setGravity(false);

							if (!inBounds.contains(type)) {
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

	public static boolean isBottomSlab(Block block) {
		return Tag.SLABS.isTagged(block.getType()) && ((Slab) block.getBlockData()).getType() == Slab.Type.BOTTOM;
	}

	public static void dropBall(Snowball ball) {
		ball.getWorld().dropItem(ball.getLocation(), golfBall);
	}

	public static void giveBall(UUID uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
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

	public static int getStroke(Snowball ball) {
		return ball.getPersistentDataContainer().get(parKey, PersistentDataType.INTEGER);
	}

	public static void respawnBall(Snowball ball) {
		Player player = getPlayer(ball);
		PersistentDataContainer c = ball.getPersistentDataContainer();

		double x = c.get(MiniGolf.getXKey(), PersistentDataType.DOUBLE);
		double y = c.get(MiniGolf.getYKey(), PersistentDataType.DOUBLE);
		double z = c.get(MiniGolf.getZKey(), PersistentDataType.DOUBLE);

		ball.setVelocity(new Vector(0, 0, 0));
		ball.teleport(new Location(ball.getWorld(), x, y, z));
		ball.setGravity(false);
		ball.setFireTicks(0);
		ball.setTicksLived(1);

		sendActionBar(player, "&cOut of bounds!");
	}

	public static Player getPlayer(Snowball ball) {
		for (MiniGolfUser user : new HashSet<>(users)) {
			if (user == null) {
				users.remove(null);
				continue;
			}

			if (user.getSnowball() == null)
				continue;

			if (user.getSnowball().equals(ball)) {
				OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(user.getUuid());
				if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
					return offlinePlayer.getPlayer();
				}
			}
		}
		return null;
	}

	public static MiniGolfUser getUser(UUID uuid) {
		for (MiniGolfUser user : getUsers()) {
			if (user.getUuid().equals(uuid))
				return user;
		}
		return null;
	}

	public static void sendActionBar(Player player, String message) {
		if (player == null)
			return;
		ActionBarUtils.sendActionBar(player, message, Time.SECOND.x(3));
	}
}
