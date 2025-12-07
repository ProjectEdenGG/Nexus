package gg.projecteden.nexus.features.listeners;

import de.tr7zw.nbtapi.NBT;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;

public class DeathItemDespawnTimer implements Listener {
	private static final NamespacedKey DEATH_KEY = new NamespacedKey(Nexus.getInstance(), "death");
	private static final long DEATH_TIMER = TickTime.MINUTE.x(-5);

	@EventHandler
	public void on(PlayerDeathEvent event) {
		event.getDrops().forEach(drop -> {
			drop.editPersistentDataContainer(pdc -> {
				pdc.set(DEATH_KEY, PersistentDataType.BOOLEAN, true);
			});
		});
	}

	@EventHandler
	public void on(ItemSpawnEvent event) {
		var item = event.getEntity();
		var itemStack = item.getItemStack();
		var death = itemStack.getPersistentDataContainer().has(DEATH_KEY);

		if (!death)
			return;

		itemStack.editPersistentDataContainer(pdc -> {
			pdc.remove(DEATH_KEY);
		});

		NBT.modify(item, nbt -> {
			nbt.setShort("Age", (short) DEATH_TIMER);
		});
	}

}
