package gg.projecteden.nexus.features.events.y2021.pugmas21.advent;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.particles.VectorUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Builder
public class AdventAnimation {
	private final Location location;
	@Builder.Default
	private final double length1 = 0.25;
	@Builder.Default
	private final double height1 = 0.5;
	@Builder.Default
	private final Particle particle1 = Particle.CRIT;
	@Builder.Default
	private final int ticks1 = 40;
	@Builder.Default
	private final double length2 = 0.25;
	@Builder.Default
	private final double height2 = 0.25;
	@Builder.Default
	private final Particle particle2 = Particle.CRIT;
	@Builder.Default
	private final int ticks2 = 40;
	@Builder.Default
	private final int randomMax = 40;
	@Builder.Default
	private final List<ItemStack> items = new ArrayList<>();
	private final List<ItemStack> givenItems = new ArrayList<>();
	private final Player player;

	public void open() {
		ItemStack chest = new ItemBuilder(Material.TRAPPED_CHEST).customModelData(1).build();
		Item item = spawnItem(location, chest, length1, height1, location.getDirection());
		int itemTaskId = particleTask(particle1, item);

		Tasks.wait(ticks1, () -> {
			Tasks.cancel(itemTaskId);
			Location location = removeItem(item);

			explodeContents(location);
		});


	}

	public void openTwice() {
		ItemStack chest = new ItemBuilder(Material.TRAPPED_CHEST).customModelData(1).build();
		Item chestItem = spawnItem(location, chest, length1, height1, location.getDirection());
		int itemTaskId = particleTask(particle1, chestItem);

		Tasks.wait(ticks1, () -> {
			Tasks.cancel(itemTaskId);
			Location location = removeItem(chestItem);
			new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(location).play();

			for (int i = 0; i < items.size(); i++) {
				Item _item = spawnItem(location, chest, length2, height2, VectorUtils.getRandomDirection());
				int _itemTaskId = particleTask(particle2, _item);

				Tasks.wait(ticks2 + RandomUtils.randomInt(0, randomMax), () -> {
					Tasks.cancel(_itemTaskId);
					Location _location = removeItem(_item);
					explodeContents(_location);
				});
			}

		});
	}

	private void explodeContents(Location location) {
		new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(location).play();

		if (items.isEmpty())
			return;

		for (ItemStack itemStack : items) {
			Item _item = spawnItem(location, itemStack, length2, height2, VectorUtils.getRandomDirection());
			int _itemTaskId = particleTask(particle2, _item);

			Tasks.wait(ticks2 + RandomUtils.randomInt(0, randomMax), () -> {
				Tasks.cancel(_itemTaskId);

				if (!givenItems.contains(itemStack)) {
					givenItems.add(itemStack);
					ItemStack presentItem = new ItemBuilder(itemStack).lore(Pugmas21.LORE).build();
					PlayerUtils.giveItem(player, presentItem);
				}

				Location _location = removeItem(_item);
				new SoundBuilder(Sound.ENTITY_CHICKEN_EGG).location(_location).play();
			});
		}

	}

	@NotNull
	private Location removeItem(Item item) {
		Location location = item.getLocation();
		item.remove();
		return location;
	}

	private int particleTask(Particle particle1, Item item) {
		return Tasks.repeat(0, TickTime.TICK, () -> {
			if (!item.isOnGround())
				new ParticleBuilder(particle1).count(1).extra(0).location(item.getLocation()).spawn();
		});
	}

	@NotNull
	private Item spawnItem(Location location, ItemStack itemStack, double length, double height, Vector direction) {
		Item _item = location.getWorld().dropItem(location, itemStack);
		_item.setCanPlayerPickup(false);
		_item.setCanMobPickup(false);
		_item.setVelocity(direction.multiply(length).add(new Vector(0, height, 0)));
		return _item;
	}
}
