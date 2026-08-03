package gg.projecteden.nexus.features.mcmmo.resetnew.skills.axes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.equipment.skins.ToolSkin;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Reloader.NexusReloadEvent;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class TomahawkHandler implements Listener {

	private static final NamespacedKey KEY = NamespacedKey.minecraft("tomahawk");

	public static final ItemStack TEMPLATE = new ItemBuilder(Material.PAPER)
		.name("&eTomahawk Template")
		.model("survival/mcmmoreset/skills/axes/tomahawk_template")
		.maxStackSize(1)
		.build();

	public static void setTomahawk(ItemStack item) {
		item.editPersistentDataContainer(pdc -> pdc.set(KEY, PersistentDataType.BOOLEAN, true));
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick()) return;
		if (event.getClickedBlock() != null) return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item)) return;
		if (!isTomahawk(item)) return;
		if (event.getPlayer().hasCooldown(item)) return;

		new ThrownTomahawk(item, event.getPlayer());
	}

	private boolean isTomahawk(ItemStack item) {
		PersistentDataContainerView pdc = item.getPersistentDataContainer();
		if (!pdc.has(KEY)) return false;
		return Boolean.TRUE.equals(pdc.get(KEY, PersistentDataType.BOOLEAN));
	}

	private static class ThrownTomahawk implements Listener {

		private static final double RANGE = 15;
		private static final int OUT_TICKS = 24;
		private static final int RETURN_TICKS = 20;
		private static final double SPIN_DEGREES_PER_TICK = 20;

		private final ItemStack item;
		@Getter
		private final Player player;
		private final double baseDamage;

		private ArmorStand armorStand;
		private Location origin, peak;
		private Vector direction;
		private int taskId = -1;
		private int tick;

		public ThrownTomahawk(ItemStack item, Player player) {
			this.item = item.clone();
			this.player = player;

			AttributeInstance attackDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
			this.baseDamage = attackDamage == null ? 1 : attackDamage.getValue();

			Nexus.registerListener(this);
			player.setCooldown(item, ThrownTomahawk.OUT_TICKS + ThrownTomahawk.RETURN_TICKS);

			spawn();
			throwTomahawk();
		}

		private void spawn() {
			this.direction = player.getEyeLocation().getDirection().normalize();
			this.origin = getHandLocation(player);
			this.peak = this.origin.clone().add(direction.clone().multiply(RANGE));

			this.origin.getWorld().playSound(this.origin, Sound.ITEM_TRIDENT_THROW, 1f, 0.65f);
			this.origin.getWorld().playSound(this.origin, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.6f, 0.75f);

			this.armorStand = this.origin.getWorld().spawn(this.origin, ArmorStand.class, stand -> {
				stand.setArms(true);
				stand.setVisible(false);
				stand.setInvulnerable(true);
				stand.setBasePlate(false);
				stand.setGravity(false);
				stand.setMarker(true);
				stand.setSilent(true);
				stand.setPersistent(false);
				stand.setLeftArmPose(new EulerAngle(Math.toRadians(180), 0, Math.toRadians(270)));

				stand.getEquipment().setItemInOffHand(getTomahawkItem(item));
			});
		}

		private ItemStack getTomahawkItem(ItemStack item) {
			ToolSkin skin = ToolSkin.of(item);
			String model;
			if (skin != null)
				model = skin.getBaseModel() + "/tomahawk";
			else {
				String type = item.getType().name().toLowerCase().replace("_axe", "");
				model = "survival/mcmmoreset/skills/axes/tomahawks/" + type;
			}
			return new ItemBuilder(item).model(model).build();
		}

		private Location getHandLocation(Player player) {
			Location eyeLocation = player.getEyeLocation();

			return eyeLocation.clone()
				.add(eyeLocation.getDirection().multiply(0.3))
				.subtract(0, 1.7, 0);
		}

		private void throwTomahawk() {
			this.taskId = Tasks.repeat(1, 1, () -> {
				if (!player.isOnline() || !armorStand.isValid() || player.getWorld() != armorStand.getWorld()) {
					remove();
					return;
				}

				++tick;

				Location target;
				if (tick < OUT_TICKS)
					target = getOutLocation();
				else {
					target = getReturnLocation();
					if (target != null)
						target.setDirection(target.getLocation().clone().subtract(player.getLocation()).toVector());
				}

				if (target == null) {
					remove();
					return;
				}

				armorStand.teleport(target);
				spin();

				checkHit();
			});
		}

		private void remove() {
			Tasks.cancel(taskId);

			if (armorStand != null)
				armorStand.remove();

			if (player.isOnline())
				player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 0.8f, 0.9f);

			Nexus.unregisterListener(this);
		}

		private void spin() {
			double rotation = Math.toRadians(180 + tick * SPIN_DEGREES_PER_TICK);
			armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(180), rotation, Math.toRadians(270)));
		}

		private Location getOutLocation() {
			double progress = tick / (double) OUT_TICKS;
			double eased = easeOut(progress);

			return origin.clone().add(direction.clone().multiply(RANGE * eased));
		}

		private Location getReturnLocation() {
			double progress = (tick - OUT_TICKS) / (double) RETURN_TICKS;
			if (progress >= 1)
				return null;

			double eased = easeIn(progress);
			return lerp(peak, getHandLocation(player), eased);
		}

		private Location lerp(Location from, Location to, double progress) {
			Vector difference = to.toVector().subtract(from.toVector());
			return from.clone().add(difference.multiply(progress));
		}

		private double easeOut(double progress) {
			return Math.sin(progress * Math.PI / 2);
		}

		private double easeIn(double progress) {
			return 1 - Math.cos(progress * Math.PI / 2);
		}

		@EventHandler
		public void onReload(NexusReloadEvent event) {
			remove();
		}

		private void checkHit() {
			armorStand.getEyeLocation().getNearbyLivingEntities(1f).forEach(livingEntity -> {
				if (livingEntity instanceof ArmorStand) return;
				if (livingEntity.getNoDamageTicks() <= 0)
					hit(livingEntity);
			});
		}

		private boolean hit(LivingEntity target) {
			ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
			net.minecraft.world.entity.LivingEntity nmsTarget = ((CraftLivingEntity) target).getHandle();
			net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
			ServerLevel level = nmsPlayer.level();

			DamageSource source = createDamageSource(nmsPlayer, nmsItem);

			float damage = EnchantmentHelper.modifyDamage(level, nmsItem, nmsTarget, source, (float) baseDamage);
			float knockback = EnchantmentHelper.modifyKnockback(level, nmsItem, nmsTarget, source, 0);

			if (!nmsTarget.hurtServer(level, source, damage))
				return false;

			if (knockback > 0)
				nmsTarget.knockback(knockback, nmsPlayer.getX() - nmsTarget.getX(), nmsPlayer.getZ() - nmsTarget.getZ(), source, damage);

			EnchantmentHelper.doPostAttackEffectsWithItemSource(level, nmsTarget, source, nmsItem);
			return true;
		}

		private static DamageSource createDamageSource(ServerPlayer player, net.minecraft.world.item.ItemStack item) {
			DamageSource playerAttack = player.damageSources().playerAttack(player);

			return new DamageSource(playerAttack.typeHolder(), player, player) {
				@Override
				public net.minecraft.world.item.ItemStack getWeaponItem() {
					return item;
				}
			};
		}

	}

	static {
		Nexus.registerListener(new TomahawkHandler());
	}

}
