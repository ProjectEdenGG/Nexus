package gg.projecteden.nexus.models.invisiblearmour;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.utils.StringUtils.camelCase;

@Data
@Entity(value = "invisible_armor", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class InvisibleArmor implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	private Set<EquipmentSlot> hide = new HashSet<>(SLOTS);
	private Set<EquipmentSlot> hideSelf = new HashSet<>();

	public static final List<EquipmentSlot> SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

	public boolean isHidden(EquipmentSlot slot) {
		return hide.contains(slot);
	}

	public boolean isHiddenFromSelf(EquipmentSlot slot) {
		return hideSelf.contains(slot);
	}

	public boolean isShown(EquipmentSlot slot) {
		return !isHidden(slot);
	}

	public boolean isShownToSelf(EquipmentSlot slot) {
		return !isHiddenFromSelf(slot);
	}

	public void toggleHide(EquipmentSlot slot) {
		if (isHidden(slot))
			hide.remove(slot);
		else
			hide.add(slot);
	}

	public void toggleHideSelf(EquipmentSlot slot) {
		if (isHiddenFromSelf(slot))
			hideSelf.remove(slot);
		else
			hideSelf.add(slot);
	}

	public ItemStack getDisplayItem(EquipmentSlot slot) {
		final PlayerInventory inventory = getOnlinePlayer().getInventory();
		final ItemStack item = inventory.getItem(slot);
		if (isNullOrAir(item))
			return getHiddenIcon(slot);
		else
			return item;
	}

	public ItemStack getHiddenIcon(EquipmentSlot slot) {
		final ItemBuilder item;
		if (ResourcePack.isEnabledFor(this))
			item = new ItemBuilder(Material.ARMOR_STAND).customModelData(SLOTS.indexOf(slot) + 1);
		else
			item = new ItemBuilder(Material.RED_CONCRETE);

		return item.name(camelCase(slot)).build();
	}

	public ItemStack getShownIcon(EquipmentSlot slot) {
		final ItemBuilder item;
		if (ResourcePack.isEnabledFor(this))
			item = new ItemBuilder(Material.ARMOR_STAND).customModelData(SLOTS.indexOf(slot) + 5);
		else
			item = new ItemBuilder(Material.GREEN_CONCRETE);

		return item.name(camelCase(slot)).build();
	}

	public void sendPackets() {
		if (!isOnline())
			return;

		if (!isEnabled())
			return;

		if (Minigames.isMinigameWorld(getWorldGroup()))
			return;

		SLOTS.forEach(this::sendPacket);
	}

	private void sendPacket(EquipmentSlot slot) {
		if (!isHidden(slot))
			return;

		final Player player = getOnlinePlayer();
		final OnlinePlayers players = getPacketTargets();
		if (!isHiddenFromSelf(slot))
			players.exclude(player);

		if (slot == EquipmentSlot.HEAD && new CostumeUserService().get(this).getActiveCostume() != null)
			return;

		PacketUtils.sendFakeItem(player, players.get(), new ItemStack(Material.AIR), slot);
	}

	public void sendResetPackets() {
		SLOTS.forEach(this::sendResetPacket);
	}

	public void sendResetPacket(EquipmentSlot slot) {
		if (!isOnline())
			return;

		final Player player = getOnlinePlayer();
		final PlayerInventory inventory = player.getInventory();
		final OnlinePlayers players = getPacketTargets();
		PacketUtils.sendFakeItem(player, players.get(), inventory.getItem(slot), slot);
	}

	private OnlinePlayers getPacketTargets() {
		return OnlinePlayers.where().world(getOnlinePlayer().getWorld()).radius(100);
	}

}
