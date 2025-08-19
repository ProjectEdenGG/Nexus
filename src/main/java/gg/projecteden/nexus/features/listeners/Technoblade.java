package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Pig.Variant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Technoblade implements Listener {

	private static final NamespacedKey NBT_KEY = Objects.requireNonNull(NamespacedKey.fromString("technoblade.hadsaddle", Nexus.getInstance()));

	static {
		for (World world : Bukkit.getWorlds())
			for (Entity entity : world.getEntities())
				if (entity instanceof Pig pig)
					technoblade(pig);
	}

	private static void technoblade(Pig pig) {
		if (pig.getVariant() != Variant.TEMPERATE)
			return;

		if (getNbtKey(pig) != null)
			// Already handled
			return;

		setWasSaddled(pig, pig.hasSaddle());
		pig.setSaddle(true);
	}

	private static void setWasSaddled(Pig pig, boolean hasSaddle) {
		pig.getPersistentDataContainer().set(NBT_KEY, PersistentDataType.BYTE, hasSaddle ? (byte) 1 : (byte) 0);
	}

	private static boolean wasSaddled(Pig pig) {
		final Byte value = getNbtKey(pig);
		return value != null && value == 1;
	}

	@Nullable
	private static Byte getNbtKey(Pig pig) {
		return pig.getPersistentDataContainer().get(NBT_KEY, PersistentDataType.BYTE);
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		if (event.getEntity() instanceof Pig pig)
			technoblade(pig);
	}

	@EventHandler
	public void on(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Pig pig))
			return;

		if (!wasSaddled(pig))
			event.getDrops().removeIf(itemStack -> Nullables.isNotNullOrAir(itemStack) && itemStack.getType() == Material.SADDLE);
	}

	@EventHandler
	public void on(EntityMountEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!(event.getMount() instanceof Pig pig))
			return;

		if (wasSaddled(pig))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Pig pig))
			return;

		final ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
		if (Nullables.isNullOrAir(item))
			return;

		if (item.getType() != Material.SADDLE)
			return;

		if (wasSaddled(pig))
			return;

		setWasSaddled(pig, true);
		event.getPlayer().getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
		event.setCancelled(true);
	}

}
