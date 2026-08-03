package gg.projecteden.nexus.features.mcmmo.resetnew.skills.crossbows.turret;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.mcmmo.resetnew.skills.crossbows.turret.TurretTrajectory.AimResult;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.pvp.PVPService;
import gg.projecteden.nexus.models.survival.TurretConfigService;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turret {

	public static final NamespacedKey ARROW_KEY = NamespacedKey.minecraft("turret");
	private static final float YAW_SPEED = 4;
	private static final float PITCH_SPEED = 2;
	public static final int RANGE = 50;

	private Location location;
	private UUID armorStand;
	private UUID itemFrame;
	private UUID owner;

	public Turret(Location location, UUID owner) {
		this.location = location.toBlockLocation();
		this.location.setYaw(0);
		this.location.setPitch(0);

		this.owner = owner;

		TurretConfigService.get().getTurrets().add(this);

		create();
	}

	private transient ArmorStand stand;
	private transient ItemFrame frame;
	private transient LivingEntity target;

	public void create() {
		this.stand = findStand();
		this.frame = findFrame();

		if (stand == null) {
			stand = location.getWorld().spawn(LocationUtils.getCenteredLocation(location).add(0, .35, 0), ArmorStand.class, as -> {
				as.setVisible(false);
				as.setSmall(true);
				as.setSmall(true);
				as.setInvulnerable(true);
				as.setGravity(false);
				as.setHeadPose(EulerAngle.ZERO);

				as.getEquipment().setHelmet(new ItemBuilder(Material.PAPER).model(TurretStage.STANDBY.getModel()).build());
			});
		}
		if (frame == null) {
			frame = location.getWorld().spawn(location, ItemFrame.class, itemFrame -> {
				itemFrame.setVisible(false);
				itemFrame.setInvulnerable(true);
				itemFrame.setFacingDirection(BlockFace.UP, true);

				itemFrame.setItem(new ItemBuilder(Material.PAPER).model(TurretStage.BASE.getModel()).build());
			});
		}

		this.armorStand = stand.getUniqueId();
		this.itemFrame = frame.getUniqueId();

		TurretConfigService.get().save();
	}

	private ArmorStand findStand() {
		if (this.armorStand == null) return null;
		return location.getNearbyEntities(2, 2, 2)
			.stream()
			.filter(entity -> entity instanceof ArmorStand)
			.map(entity -> (ArmorStand) entity)
			.filter(stand -> stand.getUniqueId().equals(this.armorStand))
			.findFirst()
			.orElse(null);
	}

	private ItemFrame findFrame() {
		if (this.itemFrame == null) return null;
		return location.getNearbyEntities(2, 2, 2)
			.stream()
			.filter(entity -> entity instanceof ItemFrame)
			.map(entity -> (ItemFrame) entity)
			.filter(frame -> frame.getUniqueId().equals(this.itemFrame))
			.findFirst()
			.orElse(null);
	}

	public List<ItemStack> remove() {
		if (stand == null)
			stand = findStand();
		if (frame == null)
			frame = findFrame();

		List<ItemStack> items = new ArrayList<>();
		if (stand != null) {
			items = ItemUtils.getNBTContentsOfNonInventoryItem(stand.getEquipment().getHelmet(), 27);
			stand.remove();
		}
		if (frame != null)
			frame.remove();

		TurretConfigService.get().getTurrets().remove(this);
		TurretConfigService.get().save();

		return items;
	}

	public void setStage(TurretStage stage) {
		if (stage == null || stage == TurretStage.BASE) return;
		if (this.stand == null) return;

		if (!this.stand.isValid())
			create();

		this.stand.getEquipment().setHelmet(new ItemBuilder(this.stand.getEquipment().getHelmet(), true).model(stage.getModel()).build());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return ((Turret) o).getLocation().equals(location);
	}

	private transient int ANIMATION_TICKS = 0;
	private transient int LOAD_COOLDOWN = 0;
	private transient boolean inventoryOpen = false;

	public void tick() {
		if (inventoryOpen) return;

		if (stand == null || frame == null) {
			this.stand = findStand();
			this.frame = findFrame();
		}
		if (stand == null || frame == null) {
			remove(); // Failsafe... if the entities were removed - remove it from being processed
			return;
		}

		if (this.target != null && !isValidTarget(this.target))
			this.target = null;

		List<ItemStack> inventory = ItemUtils.getNBTContentsOfNonInventoryItem(stand.getEquipment().getHelmet(), 27);
		ItemStack arrow = getArrow(inventory);

		if (Nullables.isNullOrAir(arrow)) {
			if (TurretStage.of(this) != TurretStage.STANDBY)
				setStage(TurretStage.STANDBY);
		}
		if (ANIMATION_TICKS++ > 20) {
			ANIMATION_TICKS = 0;

			if (arrow == null) {
				stand.getWorld().spawnParticle(Particle.SMOKE, stand.getEyeLocation(), 3, 0, 0, 0, 0);
			}
			else {
				TurretStage current = TurretStage.of(this);
				if (current != TurretStage.ARROW) {
					setStage(EnumUtils.next(TurretStage.class, current.ordinal()));
					if (TurretStage.of(this) == TurretStage.ARROW) {
						LOAD_COOLDOWN = 10;
						location.getWorld().playSound(this.stand.getEyeLocation(), Sound.ITEM_CROSSBOW_LOADING_MIDDLE, 1f, 1f);
					} else
						location.getWorld().playSound(this.stand.getEyeLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1f, 1f);
				}
			}
		}
		if (LOAD_COOLDOWN > 0)
			LOAD_COOLDOWN--;

		AimResult finalAim;
		Location muzzle = null;

		if (this.target == null || arrow == null)
			finalAim = new AimResult(0, 30, new Vector(0, 0, 0));
		else {
			Location pivot = location.clone().add(0.5, 1, 0.5);

			AimResult initialAim = TurretTrajectory.aim(pivot, this.target);
			if (initialAim == null)
				return;
			muzzle = pivot.clone().add(initialAim.velocity().clone().normalize().multiply(0.6));
			finalAim = TurretTrajectory.aim(muzzle, this.target);

			if (finalAim == null)
				return;
		}

		float yaw = moveTowardsAngle(stand.getYaw(), finalAim.yaw());

		double currentPitch = Math.toDegrees(stand.getHeadPose().getX());
		double pitch = moveTowards(currentPitch, finalAim.pitch());

		stand.setRotation(yaw, 0);
		stand.setHeadPose(new EulerAngle(Math.toRadians(pitch), 0, 0));

		boolean aimed = angleDifference(yaw, finalAim.yaw()) < 2
			&& Math.abs(pitch - finalAim.pitch()) < 2;
		TurretStage stage = TurretStage.of(this);

		if (this.target != null && aimed && stage == TurretStage.ARROW && LOAD_COOLDOWN == 0) {
			fire(muzzle, finalAim, arrow);
			setStage(TurretStage.STANDBY);
			ItemStack helmet = stand.getEquipment().getHelmet();
			ItemUtils.setNBTContentsOfNonInventoryItem(helmet, inventory);
			stand.getEquipment().setHelmet(helmet);
		}
	}

	private void fire(Location muzzle, AimResult aim, ItemStack arrowItem) {
		Vector direction = aim.velocity().clone().normalize();

		Class<? extends AbstractArrow> clazz = arrowItem.getType() == Material.SPECTRAL_ARROW ? SpectralArrow.class : Arrow.class;
		AbstractArrow arrow = muzzle.getWorld().spawnArrow(
			muzzle,
			direction,
			(float) aim.velocity().length(),
			0,
			clazz
		);
		arrow.setItemStack(arrowItem);

		if (owner != null)
			arrow.setShooter(Bukkit.getPlayer(owner));

		arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		arrow.getPersistentDataContainer().set(ARROW_KEY, PersistentDataType.BOOLEAN, true);

		this.stand.getWorld().playSound(this.stand.getEyeLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1f, 1f);
	}

	private ItemStack getArrow(List<ItemStack> inventory) {
		if (this.owner == null)
			return new ItemStack(Material.ARROW);

		if (inventory.isEmpty())
			return null;

		for (ItemStack item : inventory) {
			if (Nullables.isNullOrAir(item)) continue;
			if (item.getEnchantments().containsKey(Enchantment.INFINITY))
				return ItemBuilder.oneOf(item).build();
		}
		for (ItemStack item : inventory)
			if (Nullables.isNotNullOrAir(item)) {
				ItemStack one = ItemBuilder.oneOf(item).build();
				item.subtract();
				return one;
			}

		return null;
	}

	private static float angleDifference(float first, float second) {
		return Math.abs(normalizeDegrees(second - first));
	}

	private static float moveTowardsAngle(float current, float target) {
		float difference = normalizeDegrees(target - current);

		if (Math.abs(difference) <= Turret.YAW_SPEED)
			return target;

		return normalizeDegrees(current + Math.copySign(Turret.YAW_SPEED, difference));
	}

	private static double moveTowards(double current, double target) {
		double difference = target - current;

		if (Math.abs(difference) <= (double) Turret.PITCH_SPEED)
			return target;

		return current + Math.copySign(Turret.PITCH_SPEED, difference);
	}

	private static float normalizeDegrees(float degrees) {
		degrees %= 360;

		if (degrees >= 180)
			degrees -= 360;
		else if (degrees < -180)
			degrees += 360;

		return degrees;
	}

	private transient PVPService PVP_SERVICE = new PVPService();

	public boolean isValidTarget(LivingEntity entity) {
		if (!entity.isValid()) return false;
		if (entity instanceof ArmorStand) return false;
		if (this.stand.getEyeLocation().distanceSquared(entity.getEyeLocation()) > Math.pow(RANGE, 2)) return false;
		if (!this.stand.hasLineOfSight(entity)) return false;
		if (entity instanceof Enemy) return !EntityUtils.hasCustomName(entity);
		if (!(entity instanceof Player player)) return false;
		if (this.owner == null) return player.getGameMode() == GameMode.SURVIVAL;
		if (!Nerd.of(this.owner).isOnline()) return false;
		if (this.owner.equals(player.getUniqueId())) return false;
		if (!PVP_SERVICE.get(this.owner).isEnabled()) return false;
		if (!Bukkit.getPlayer(this.owner).canSee(player)) return false;
		if (player.getGameMode() != GameMode.SURVIVAL) return false;
		if (Bukkit.getPlayer(this.owner).getGameMode() != GameMode.SURVIVAL) return false;
		return PVP_SERVICE.get(player).isEnabled();
	}

	public enum TurretStage {
		BASE,
		STANDBY,
		PULLING_0,
		PULLING_1,
		PULLING_2,
		ARROW;

		public String getModel() {
			return "survival/mcmmoreset/skills/crossbows/turret_in_world/" + name().toLowerCase();
		}

		public static TurretStage of(Turret turret) {
			if (turret == null) return null;
			if (turret.stand == null) return null;

			for (TurretStage stage : TurretStage.values())
				if (stage.getModel().equalsIgnoreCase(Model.of(turret.stand.getEquipment().getHelmet())))
					return stage;

			return null;
		}

	}

}
