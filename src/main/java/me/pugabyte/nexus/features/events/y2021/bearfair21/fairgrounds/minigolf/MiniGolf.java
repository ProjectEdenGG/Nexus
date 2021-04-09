package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.Block;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class MiniGolf {
	@Getter
	private static ItemStack putter;
	@Getter
	private static ItemStack iron;
	@Getter
	private static ItemStack wedge;
	@Getter
	private static ItemStack whistle;
	@Getter
	private static ItemStack golfBall;
	@Getter
	private static List<ItemStack> kit = new ArrayList<>();
	//
	@Getter
	private static final List<Snowball> golfBalls = new ArrayList<>();
	@Getter
	private static final Map<UUID, Snowball> lastPlayerBall = new HashMap<>();
	private static final Nexus instance = Nexus.getInstance();
	//
	@Getter
	private static final NamespacedKey ballKey = new NamespacedKey(instance, "golf_ball");
	@Getter
	private static final NamespacedKey putterKey = new NamespacedKey(instance, "putter");
	@Getter
	private static final NamespacedKey ironKey = new NamespacedKey(instance, "iron");
	@Getter
	private static final NamespacedKey wedgeKey = new NamespacedKey(instance, "wedge");
	@Getter
	private static final NamespacedKey whistleKey = new NamespacedKey(instance, "return_whistle");
	//
	@Getter
	private static final NamespacedKey parKey = new NamespacedKey(instance, "par");
	@Getter
	private static final NamespacedKey xKey = new NamespacedKey(instance, "x");
	@Getter
	private static final NamespacedKey yKey = new NamespacedKey(instance, "y");
	@Getter
	private static final NamespacedKey zKey = new NamespacedKey(instance, "z");
	//
	@Getter
	private static final double floorOffset = 0.05;
	@Getter
	private static final double maxVelLen = 2;
	@Getter
	private static final AttributeModifier noDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
			-10, Operation.ADD_NUMBER, EquipmentSlot.HAND);
	@Getter
	private static final AttributeModifier fastSwing = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed",
			10, Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND);

	public MiniGolf() {
		new ProjectileListener();
		new PuttListener();

		ballTask();
	}

	static {
		loadItems();
	}

	private static void loadItems() {
		ItemMeta meta;
		putter = new ItemBuilder(Material.IRON_HOE)
				.name("Putter")
				.customModelData(901)
				.lore("&8A specialized club", "&8for finishing holes.")
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.build();
		meta = putter.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getPutterKey());
		putter.setItemMeta(meta);

		iron = new ItemBuilder(Material.IRON_HOE)
				.name("Iron")
				.customModelData(902)
				.lore("&8A well-rounded club", "&8for longer distances.")
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.build();
		meta = iron.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getIronKey());
		iron.setItemMeta(meta);

		wedge = new ItemBuilder(Material.IRON_HOE)
				.name("Wedge")
				.customModelData(903)
				.lore("&8A specialized club", "&8for tall obstacles.")
				.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
				.build();
		meta = wedge.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getWedgeKey());
		wedge.setItemMeta(meta);

		whistle = new ItemBuilder(Material.IRON_NUGGET)
				.name("Golf Whistle")
				.lore("&8Returns your last", "&8hit golf ball to its", "&8previous location")
				.customModelData(901)
				.build();
		meta = whistle.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getWhistleKey());
		whistle.setItemMeta(meta);

		golfBall = new ItemBuilder(Material.SNOWBALL).name("Golf Ball").customModelData(1).build();
		meta = golfBall.getItemMeta();
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
		addKey(meta, getBallKey());
		golfBall.setItemMeta(meta);

		kit = Arrays.asList(getPutter(), getIron(), getWedge(), getWhistle(), getGolfBall());
	}

	private void ballTask() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK, () -> {
			for (Snowball ball : new ArrayList<>(golfBalls)) {
				// Drop if older than 1 minute
				if (ball.getTicksLived() > Time.MINUTE.get()) {
					dropBall(ball);
					golfBalls.remove(ball);
					ball.remove();
					return;
				}

				// Check block underneath
				Location loc = ball.getLocation();
				Block block = loc.subtract(0, 0.1, 0).getBlock();

				// Act upon block type
				Vector vel = ball.getVelocity();
				switch (block.getType()) {
					case CAULDRON:
						// Check speed
						if (vel.getY() >= 0 && vel.length() > 0.34)
							return;

						// Spawn firework
						Tasks.wait(Time.TICK, () -> FireworkLauncher.random(loc).power(0).detonateAfter(Time.SECOND.get()).launch());

						// Remove ball
						golfBalls.remove(ball);
						ball.remove();

						// Send message
						for (Entry<UUID, Snowball> entry : lastPlayerBall.entrySet()) {
							if (entry.getValue().equals(ball)) {
								OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(entry.getKey());
								if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
									Player player = offlinePlayer.getPlayer();
									player.sendMessage("par: " + getPar(ball));
									giveBall(player);
								}
								break;
							}
						}

						break;
					case CRIMSON_HYPHAE:
						// Halt velocity
						ball.setVelocity(new Vector(0, ball.getVelocity().getY(), 0));

						PersistentDataContainer c = ball.getPersistentDataContainer();
						// Last pos
						double x = c.get(MiniGolf.getXKey(), PersistentDataType.DOUBLE);
						double y = c.get(MiniGolf.getYKey(), PersistentDataType.DOUBLE);
						double z = c.get(MiniGolf.getZKey(), PersistentDataType.DOUBLE);
						World world = ball.getWorld();

						ball.teleport(new Location(world, x, y, z));
						ball.setGravity(false);
						return;
					case AIR:
					case WATER:
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
								return;
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
								return;
						}

						if (vel.length() < maxVelLen) {
							// Push ball
							ball.setVelocity(vel.multiply(9.3).add(newVel).multiply(0.1));
						}

						break;
					default:
						// Check if floating above slabs
						if (isBottomSlab(block) && loc.getY() > block.getY() + 0.5) {
							ball.setGravity(true);
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

	public static void giveBall(Player player) {
		PlayerUtils.giveItem(player, golfBall.clone());
	}

	public static void addKey(ItemMeta meta, NamespacedKey key) {
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 0);
	}

	public static boolean hasKey(ItemMeta meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
	}

	public static int getPar(Snowball ball) {
		return ball.getPersistentDataContainer().get(parKey, PersistentDataType.INTEGER);
	}
}
