package gg.projecteden.nexus.features.mcmmo.mastery.skills.fishing;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SpeedBoat implements Listener {

	public static final ItemStack ITEM = new ItemBuilder(Material.OAK_BOAT)
		.name("&eSpeed Boat")
		.model("survival/mcmmoreset/skills/fishing/speed_boat")
		.build();

	private static final NamespacedKey SPEED_BOAT_KEY = NamespacedKey.minecraft("speed_boat");

	@EventHandler
	public void onSpawn(EntityPlaceEvent event) {
		if (!(event.getEntity() instanceof Boat boat)) return;
		Player player = event.getPlayer();
		if (player == null) return;
		ItemStack item = player.getInventory().getItem(event.getHand());
		if (!ItemUtils.isModelMatch(item, ITEM)) return;

		makeFastBoat(boat);
	}

	private final List<PendingBoatDispense> pendingBoatDispenses = new ArrayList<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDispense(BlockDispenseEvent event) {
		if (!ItemUtils.isModelMatch(event.getItem(), ITEM)) return;

		pendingBoatDispenses.add(new PendingBoatDispense(
			event.getBlock().getWorld(),
			event.getVelocity().clone(),
			Bukkit.getCurrentTick()
		));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof Boat boat)) return;

		int currentTick = Bukkit.getCurrentTick();
		pendingBoatDispenses.removeIf(pending -> pending.tick() < currentTick);

		PendingBoatDispense pending = pendingBoatDispenses.stream()
			.filter(it -> it.tick() == currentTick)
			.filter(it -> it.world().equals(boat.getWorld()))
			.filter(it -> it.position().distanceSquared(boat.getLocation().toVector()) < 1)
			.findFirst()
			.orElse(null);

		if (pending == null) return;
		pendingBoatDispenses.remove(pending);

		makeFastBoat(boat);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAdd(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof Boat boat)) return;
		if (!boat.getPersistentDataContainer().has(SPEED_BOAT_KEY)) return;

		makeFastBoat(boat);
	}

	private record PendingBoatDispense(World world, Vector position, int tick) { }

	private void makeFastBoat(Boat boat) {
		boat.setMaxSpeed(0.8);
		boat.getPersistentDataContainer().set(SPEED_BOAT_KEY, PersistentDataType.BOOLEAN, true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBoatDrop(EntityDropItemEvent event) {
		if (!(event.getEntity() instanceof Boat boat)) return;
		if (!boat.getPersistentDataContainer().has(SPEED_BOAT_KEY)) return;
		if (event.getItemDrop().getItemStack().getType() != boat.getBoatMaterial()) return;

		event.getItemDrop().setItemStack(ITEM.clone());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getDismounted() instanceof Boat boat))
			return;

		Tasks.wait(1, () -> {
			if (!boat.isValid()) return;
			OnlinePlayers.where().filter(p -> p.getWorld().equals(boat.getWorld())).forEach(p -> {
				p.hideEntity(Nexus.getInstance(), boat);
				p.showEntity(Nexus.getInstance(), boat);
			});
		});
	}

	static {
		Nexus.registerListener(new SpeedBoat());
	}

}
