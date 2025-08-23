package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.handler.NBTHandlers;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.ItemBuilder;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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

		boolean saddled = wasSaddled(pig);
		if (pig.getPersistentDataContainer().has(NBT_KEY, PersistentDataType.BYTE))
			saddled = oldWasSaddled(pig);

		pig.getPersistentDataContainer().remove(NBT_KEY);
		setSaddle(pig, saddled ? SaddleState.SADDLED : SaddleState.UNSADDLED);
	}

	private static boolean oldWasSaddled(Pig pig) {
		final Byte value = pig.getPersistentDataContainer().get(NBT_KEY, PersistentDataType.BYTE);
		return value != null && value == 1;
	}

	private static void setSaddle(Pig pig, SaddleState state) {
		var saddle = new ItemBuilder(Material.SADDLE).components(nbt -> {
			ReadWriteNBT readWriteNBT = NBT.createNBTObject();
			readWriteNBT.setString("slot", "saddle");
			readWriteNBT.setString("asset_id", "minecraft:technoblade_" + state.name().toLowerCase());
			nbt.set("minecraft:equippable", readWriteNBT, NBTHandlers.STORE_READWRITE_TAG);
		}).build();

		pig.getEquipment().setItem(EquipmentSlot.SADDLE, saddle);
	}

	@SuppressWarnings("UnstableApiUsage")
	private static boolean wasSaddled(Pig pig) {
		var saddle = pig.getEquipment().getItem(EquipmentSlot.SADDLE);
		if (isNullOrAir(saddle))
			return false;

		ItemMeta meta = saddle.getItemMeta();
		if (meta == null)
			return false;

		var model = meta.getEquippable().getModel();
		if (model == null)
			return false;

		var assetId = model.getKey();
		return assetId.contains("technoblade_saddled");
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof Pig pig))
			return;

		if (pig.getVariant() != Variant.TEMPERATE)
			return;

		technoblade(pig);
	}

	@EventHandler
	public void on(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Pig pig))
			return;

		if (pig.getVariant() != Variant.TEMPERATE)
			return;

		event.getDrops().removeIf(itemStack -> Nullables.isNotNullOrAir(itemStack) && itemStack.getType() == Material.SADDLE);

		if (!wasSaddled(pig))
			return;

		event.getDrops().add(new ItemStack(Material.SADDLE));
	}

	@EventHandler
	public void on(EntityMountEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!(event.getMount() instanceof Pig pig))
			return;

		if (pig.getVariant() != Variant.TEMPERATE)
			return;

		if (wasSaddled(pig))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Pig pig))
			return;

		if (pig.getVariant() != Variant.TEMPERATE)
			return;

		final ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
		if (isNullOrAir(item))
			return;

		if (item.getType() != Material.SADDLE)
			return;

		event.setCancelled(true);

		if (wasSaddled(pig))
			return;

		item.subtract();
		setSaddle(pig, SaddleState.SADDLED);
	}

	public enum SaddleState {
		SADDLED,
		UNSADDLED,
	}

}


