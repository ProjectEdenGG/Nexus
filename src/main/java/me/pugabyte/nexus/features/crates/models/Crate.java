package me.pugabyte.nexus.features.crates.models;

import com.destroystokyo.paper.ParticleBuilder;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.crates.Crates;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.utils.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public abstract class Crate {

	public Player player;
	public boolean inUse = false;
	public CrateLoot loot;
	public List<Hologram> crateHologram;
	public Location location;
	public Item spawnedItem;

	public abstract CrateType getCrateType();

	public abstract List<String> getCrateHologramLines();

	public Color[] getBandColors() {
		return new Color[]{Color.WHITE, Color.WHITE};
	}

	public void spawnHologram() {
		List<Hologram> holograms = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Hologram hologram = HologramsAPI.createHologram(Nexus.getInstance(), getCrateType().getCenteredLocation().clone().add(0, 2.5, 0));
			for (String line : getCrateHologramLines())
				hologram.appendTextLine(StringUtils.colorize(line));
			hologram.getVisibilityManager().setVisibleByDefault(true);
			holograms.add(hologram);
		}
		crateHologram = holograms;
	}

	public void deleteHologram() {
		for (Hologram hologram : crateHologram)
			hologram.delete();
	}

	public void showHologram() {
		for (Hologram hologram : crateHologram)
			hologram.getVisibilityManager().setVisibleByDefault(true);
	}

	public void hideHologram() {
		for (Hologram hologram : crateHologram)
			hologram.getVisibilityManager().setVisibleByDefault(false);
	}

	public void openCrate(Player player) {
		if (inUse) return;
		this.player = player;
		this.location = getCrateType().getCenteredLocation();
		inUse = true;
		pickCrateLoot();
		if (!canHoldItems(player)) return;
		takeKey();
		hideHologram();
		playAnimation(location).thenAccept(finalLocation -> {
			playFinalParticle(finalLocation);
			spawnItem(finalLocation, loot.getDisplayItem());
		});
		Tasks.wait(Time.SECOND.x(7), this::reset);
	}

	public void openMultiple(Player player, int amount) {
		if (inUse) return;
		this.player = player;
		this.location = getCrateType().getCenteredLocation();
		MenuUtils.ConfirmationMenu.builder()
				.title("Open " + amount + " Crates?")
				.onConfirm(e -> {
					player.closeInventory();
					pickCrateLoot();
					if (!canHoldItems(player)) return;
					takeKey();
					hideHologram();
					playAnimation(location).thenAccept(finalLocation -> {
						AtomicInteger wait = new AtomicInteger(0);
						Tasks.wait(Time.SECOND.x(wait.getAndAdd(1)), () -> {
							playFinalParticle(finalLocation);
							spawnItem(finalLocation, loot.getDisplayItem());
						});
						List<Integer> tasks = new ArrayList<>();
						for (int i = 0; i < amount - 1; i++) {
							int j = i;
							tasks.add(Tasks.wait(Time.SECOND.x(wait.getAndAdd(1)), () -> {
								pickCrateLoot();
								if (!canHoldItems(player)) {
									tasks.forEach(Tasks::cancel);
									return;
								}
								removeItem();
								takeKey();
								playFinalParticle(finalLocation);
								spawnItem(finalLocation, loot.getDisplayItem());
								if (j == amount - 2)
									Tasks.wait(Time.SECOND.x(3), this::reset);
							}));
						}
					});
				})
				.open(player);
	}

	public boolean canHoldItems(Player player) {
		if (!PlayerUtils.hasRoomFor(player, loot.getItems().toArray(new ItemStack[0]))) {
			PlayerUtils.send(player, Crates.PREFIX + "You must clear room in your inventory before you can open crates");
			reset();
			return false;
		}
		return true;
	}

	public void pickCrateLoot() {
		Map<CrateLoot, Double> original = new HashMap<>();
		Crates.getLootByType(getCrateType()).stream().filter(CrateLoot::isActive)
				.forEach(crateLoot -> original.put(crateLoot, crateLoot.getWeight()));

		LinkedHashMap<CrateLoot, Double> sorted = new LinkedHashMap<>();
		original.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.forEachOrdered(e -> sorted.put(e.getKey(), e.getValue()));

		LinkedHashMap<CrateLoot, Double> percentages = new LinkedHashMap<>();
		double max = 0;
		for (double i : sorted.values())
			max += i;
		for (Map.Entry<CrateLoot, Double> entry : sorted.entrySet())
			percentages.put(entry.getKey(), ((entry.getValue() / max) * 100));

		LinkedHashMap<CrateLoot, Integer> normalized = new LinkedHashMap<>();
		LinkedHashMap<CrateLoot, Double> temp = new LinkedHashMap<>();
		while (percentages.values().toArray(new Double[0])[0] < 1) {
			for (Map.Entry<CrateLoot, Double> entry : percentages.entrySet()) {
				temp.put(entry.getKey(), percentages.get(entry.getKey()) * 10);
			}
			percentages = temp;
		}
		percentages.forEach((key, value) -> normalized.put(key, value.intValue()));

		LinkedHashMap<Integer, List<CrateLoot>> combined = new LinkedHashMap<>();
		normalized.forEach((key, value) -> {
			if (!combined.containsKey(value))
				combined.put(value, new ArrayList<>());
			combined.get(value).add(key);
		});

		int rarity = 0;
		Integer[] percents = normalized.values().toArray(new Integer[0]);
		int random = (int) (Math.random() * percents[percents.length - 1]) + 1;
		for (int i : percents)
			if (random <= i) {
				rarity = i;
				break;
			}

		List<CrateLoot> list = combined.get(rarity);
		int random2 = (int) (Math.random() * list.size());
		loot = list.get(random2);
	}

	public CompletableFuture<Location> playAnimation(Location location) {
		final AtomicDouble radius = new AtomicDouble(.2);
		final AtomicReference<Double> y = new AtomicReference<>(0d);
		final AtomicDouble t = new AtomicDouble(0);
		final AtomicReference<Location> locationReference = new AtomicReference<>(location);
		final CompletableFuture<Location> finalLocation = new CompletableFuture<>();

		int taskId = Tasks.repeat(0, 1, () -> {
			y.updateAndGet(v -> v - Math.PI / 16);
			for (int i = 0; i < 50; i++) {
				for (int band = 0; band < 2; band++) {
					double x = radius.get() * (2 * Math.PI - t.get()) * Math.cos(t.get() + y.get() + band * Math.PI);
					double yPos = 0.5 * t.get();
					double z = radius.get() * (2 * Math.PI - t.get()) * Math.sin(t.get() + y.get() + band * Math.PI);
					locationReference.set(location.clone().add(x, yPos, z));
					new ParticleBuilder(org.bukkit.Particle.REDSTONE)
							.color(getBandColors()[band])
							.count(1)
							.location(locationReference.get())
							.spawn();
				}
				t.set(t.get() + .002);
			}
		});

		Tasks.wait(Time.SECOND.x(3), () -> {
			finalLocation.complete(locationReference.get());
			Tasks.cancel(taskId);
		});

		return finalLocation;
	}

	public void playFinalParticle(Location location) {
		List<Material> dyes = Arrays.stream(Material.values()).filter(material -> material.name().contains("DYE"))
				.collect(Collectors.toList());

		for (int i = 0; i < 50; i++) {
			new ParticleBuilder(org.bukkit.Particle.ITEM_CRACK)
					.location(location)
					.count(5)
					.offset(.1, .1, .1)
					.extra(1)
					.data(new ItemStack(dyes.get((int) (Math.random() * dyes.size()))))
					.spawn();
		}
	}

	public Item spawnItem(Location location, ItemStack itemStack) {
		Item item = location.getWorld().dropItem(location, itemStack);
		item.setVelocity(new Vector(0, 0, 0));
		item.setCanPlayerPickup(false);
		item.setCustomNameVisible(true);
		item.setCustomName(StringUtils.colorize(loot.getTitle()));
		spawnedItem = item;
		return item;
	}

	public void removeItem() {
		spawnedItem.remove();
		ItemUtils.giveItems(player, loot.getItems());
	}

	public void takeKey() {
		ItemStack key = getCrateType().getKey();
		for (ItemStack item : player.getInventory().getContents())
			if (ItemUtils.isFuzzyMatch(key, item)) {
				item.setAmount(item.getAmount() - 1);
				break;
			}
	}

	public void reset() {
		inUse = false;
		showHologram();
		removeItem();
	}
}
