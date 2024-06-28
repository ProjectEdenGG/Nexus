package gg.projecteden.nexus.features.resourcepack.decoration.store;

import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreManager.StoreType;
import gg.projecteden.nexus.utils.PacketUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class TargetData {
	@NonNull UUID playerUUID;

	BuyableData buyableData;

	Entity currentEntity;
	Entity oldEntity;

	Location currentSkullLocation;

	public TargetData(Player player) {
		playerUUID = player.getUniqueId();
	}

	public void update(Entity newEntity) {
		oldEntity = currentEntity;
		currentEntity = newEntity;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}

	public void setupTargetHDB(@NonNull Skull skull, @NonNull ItemStack skullItem, StoreType storeType) {
		ArmorStand armorStand = (ArmorStand) PacketUtils.spawnSkullArmorStand(getPlayer(), skull, skullItem).getBukkitEntity();
		debug("target: skull " + armorStand.getType());

		update(armorStand);
		setCurrentSkullLocation(skull.getLocation());
		buyableData = new BuyableData(skullItem, storeType.currency);
	}

	public void setupTargetEntity(@NonNull Entity entity, @NonNull ItemStack entityItem, StoreType storeType) {
		debug("target: entity " + entity.getType());

		update(entity);
		currentSkullLocation = null;

		buyableData = new BuyableData(entityItem, storeType.currency);
	}

	public void glowCurrentEntity() {
		if (currentEntity == null)
			return;

		glowEntity(currentEntity, true);
	}

	private void glowEntity(Entity entity, boolean glowing) {
		PacketUtils.glow(getPlayer(), entity, glowing);
	}

	private void debug(String message) {
		DecorationStoreManager.debug(getPlayer(), message);
	}

	public void unglow() {
		unglowOldEntity();
		unglowEntity(currentEntity);
	}

	public void unglowOldEntity() {
		unglowEntity(oldEntity);
	}

	private void unglowEntity(Entity entity) {
		debug("unglowEntity");
		if (entity == null) {
			debug(" entity == null");
			return;
		}
		debug(" unglowing entity");
		glowEntity(entity, false);

		if (entity instanceof ArmorStand) {
			PacketUtils.entityDestroy(getPlayer(), entity);
		}
	}
}
