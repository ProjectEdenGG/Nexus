package gg.projecteden.nexus.features.mcmmo.mastery.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Reloader.NexusReloadEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Orb implements Listener {

	private final Player owner;
	private final OrbManager manager;
	public Location location;
	public double radius;

	private ArmorStand armorStand;
	private ItemDisplay radiusDisplay;
	private final List<Entity> entities = new ArrayList<>();
	private final List<Integer> tasks = new ArrayList<>();

	public Orb(Location location, Player owner, OrbManager manager) {
		this.location = location.clone();
		this.location.setYaw(0);
		this.location.setPitch(0);

		this.manager = manager;
		this.owner = owner;
		this.radius = manager.getRadius() * 2;

		Nexus.registerListener(this);
	}

	public void spawn() {
		armorStand = location.getWorld().spawn(location.clone().subtract(0, 1.6, 0), ArmorStand.class, stand -> {
			stand.setGravity(false);
			stand.setVisible(false);
			stand.setInvulnerable(true);
			stand.getEquipment().setHelmet(manager.getItem());
		});
		entities.add(armorStand);
		animateOrb();

		radiusDisplay = location.getWorld().spawn(location, ItemDisplay.class, display -> {
			display.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			display.setItemStack(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.model("survival/mcmmoreset/orb/orb_aura_1")
				.dyeColor(manager.getColor())
				.build());

			display.setInterpolationDelay(0);
			display.setInterpolationDuration((int) radius);
		});
		entities.add(radiusDisplay);
		tasks.add(Tasks.wait(2, () -> {
			Transformation transform = radiusDisplay.getTransformation();
			transform.getScale().set(radius, 1, radius);
			radiusDisplay.setTransformation(transform);
		}));

		tasks.add(Tasks.repeat((long) (radius + 2), 16, this::spawnAuraRing));
		tasks.add(Tasks.repeat(0, TickTime.SECOND, this::applyEffects));

		tasks.add(Tasks.wait(manager.getDurationTicks(), this::cancel));
	}

	private void cancel() {
		cancel(true);
	}

	private void cancel(boolean cooldown) {
		tasks.forEach(Tasks::cancel);
		entities.forEach(Entity::remove);

		if (owner.getGameMode() != GameMode.CREATIVE && cooldown)
			OrbManager.COOLDOWN_SERVICE.edit(owner, cd -> cd.create(manager.getCooldownType(), manager.getCooldownTicks()));

		if (owner.getGameMode() == GameMode.SURVIVAL)
			PlayerUtils.giveItemAndMailExcess(owner, manager.getItem(), WorldGroup.of(location));

		Nexus.unregisterListener(this);
	}

	private void applyEffects() {
		location.getNearbyEntities(manager.getRadius(), manager.getRadius(), manager.getRadius()).stream()
			.map(entity -> manager.getEntity(entity))
			.filter(Objects::nonNull)
			.filter(entity -> {
				Location location = entity.getLocation().clone();
				location.setY(this.location.getY());

				return this.location.distanceSquared(location) <= (manager.getRadius() * manager.getRadius());
			})
			.forEach(manager::handle);
	}

	private void animateOrb() {
		Location start = armorStand.getLocation().clone();

		double riseHeight = 3.7;
		int riseDuration = 30;

		double bobHeight = 0.6;
		double bobSpeed = 0.06;
		float rotationSpeed = 3f;

		AtomicInteger tick = new AtomicInteger();

		tasks.add(Tasks.repeat(1, 1, () -> {
			if (!armorStand.isValid())
				return;

			int ticks = tick.getAndIncrement();
			double y;

			if (ticks < riseDuration) {
				double progress = ticks / (double) riseDuration;
				double eased = 1 - Math.pow(1 - progress, 3);
				y = start.getY() + (riseHeight + bobHeight) * eased;
			} else {
				int bobTick = ticks - riseDuration;
				y = start.getY() + riseHeight + Math.cos(bobTick * bobSpeed) * bobHeight;
			}

			Location location = armorStand.getLocation();
			location.setY(y);

			location.setYaw(location.getYaw() + rotationSpeed);

			armorStand.teleport(location);
		}));
	}

	public void spawnAuraRing() {
		ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class, display -> {
			display.setItemDisplayTransform(ItemDisplayTransform.HEAD);
			display.setItemStack(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.model("survival/mcmmoreset/orb/orb_aura_1")
				.dyeColor(fadeToWhite(manager.getColor().getBukkitColor(), 0, 14 * 3))
				.build());
			Transformation transform = display.getTransformation();
			transform.getScale().set(radius + 0.05, 1, radius + 0.05);
			display.setTransformation(transform);

			display.setInterpolationDelay(0);
			display.setInterpolationDuration(1);
		});

		for (int i = 0; i < 14 * 3; i++) {
			int finalI = i;
			tasks.add(Tasks.wait(i, () -> {
				if (finalI % 3 == 0 && finalI != 0)
					itemDisplay.setItemStack(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
						.model("survival/mcmmoreset/orb/orb_aura_" + (finalI / 3))
						.dyeColor(fadeToWhite(manager.getColor().getBukkitColor(), finalI, 14 * 3))
						.build());

				Transformation transformation = itemDisplay.getTransformation();
				transformation.getTranslation().set(0, (1.5d / (14 * 3)) * finalI, 0);
				transformation.getScale().set(radius + 0.05, fadeToZero(finalI, (14 * 3) - 1), radius + 0.05);
				itemDisplay.setTransformation(transformation);
			}));
		}

		tasks.add(Tasks.wait(30, itemDisplay::remove));
		entities.add(itemDisplay);
	}

	private Color fadeToWhite(Color color, int index, int max) {
		double progress = Math.clamp(index / (double) max, 0, 1);
		double amount = 0.25 + (0.75 * progress);

		int red = (int) Math.round(color.getRed() + (255 - color.getRed()) * amount);
		int green = (int) Math.round(color.getGreen() + (255 - color.getGreen()) * amount);
		int blue = (int) Math.round(color.getBlue() + (255 - color.getBlue()) * amount);

		return Color.fromRGB(red, green, blue);
	}

	private double fadeToZero(int index, int max) {
		return 1 - Math.clamp(index / (double) max, 0, 1);
	}

	@EventHandler
	public void onShutdown(NexusReloadEvent event) {
		cancel(false);
	}

}
