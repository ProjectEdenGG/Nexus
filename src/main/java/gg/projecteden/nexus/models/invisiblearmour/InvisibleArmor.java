package gg.projecteden.nexus.models.invisiblearmour;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

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

	private Set<ArmorSlot> hide = new HashSet<>(Set.of(ArmorSlot.values()));
	private Set<ArmorSlot> hideSelf = new HashSet<>();

	public boolean isHidden(ArmorSlot slot) {
		return hide.contains(slot);
	}

	public boolean isHiddenFromSelf(ArmorSlot slot) {
		return hideSelf.contains(slot);
	}

	public boolean isShown(ArmorSlot slot) {
		return !isHidden(slot);
	}

	public boolean isShownToSelf(ArmorSlot slot) {
		return !isHiddenFromSelf(slot);
	}

	public void toggleHide(ArmorSlot slot) {
		if (isHidden(slot))
			hide.remove(slot);
		else
			hide.add(slot);
	}

	public void toggleHideSelf(ArmorSlot slot) {
		if (isHiddenFromSelf(slot))
			hideSelf.remove(slot);
		else
			hideSelf.add(slot);
	}

	public ItemStack getDisplayItem(ArmorSlot slot) {
		final PlayerInventory inventory = getOnlinePlayer().getInventory();
		final ItemStack item = inventory.getItem(slot.getSlot());
		if (isNullOrAir(item))
			return getHiddenIcon(slot);
		else
			return item;
	}

	public ItemStack getHiddenIcon(ArmorSlot slot) {
		return new ItemBuilder(CustomMaterial.ARMOR_OUTLINE_HELMET)
			.modelId(CustomMaterial.ARMOR_OUTLINE_HELMET.getModelId() + slot.ordinal())
			.name(camelCase(slot))
			.build();
	}

	public ItemStack getShownIcon(ArmorSlot slot) {
		return new ItemBuilder(CustomMaterial.ARMOR_FILLED_HELMET)
			.modelId(CustomMaterial.ARMOR_FILLED_HELMET.getModelId() + slot.ordinal())
			.name(camelCase(slot))
			.build();
	}

	public void sendPackets() {
		if (!isOnline())
			return;

		if (!isEnabled())
			return;

		if (Minigames.isMinigameWorld(getWorldGroup()))
			return;

		for (ArmorSlot slot : ArmorSlot.values())
			sendPacket(slot);
	}

	private void sendPacket(ArmorSlot slot) {
		if (!isHidden(slot))
			return;

		final Player player = getOnlinePlayer();
		final OnlinePlayers players = getPacketTargets();
		if (!isHiddenFromSelf(slot))
			players.exclude(player);

		if (slot == ArmorSlot.HELMET && new CostumeUserService().get(this).hasActiveCostumes())
			return;

		PacketUtils.sendFakeItem(player, players.get(), new ItemStack(Material.AIR), slot.getSlot());
	}

	public void sendResetPackets() {
		for (ArmorSlot slot : ArmorSlot.values())
			sendResetPacket(slot);
	}

	public void sendResetPacket(ArmorSlot slot) {
		if (!isOnline())
			return;

		final Player player = getOnlinePlayer();
		final PlayerInventory inventory = player.getInventory();
		final OnlinePlayers players = getPacketTargets();
		PacketUtils.sendFakeItem(player, players.get(), inventory.getItem(slot.getSlot()), slot.getSlot());
	}

	private OnlinePlayers getPacketTargets() {
		return OnlinePlayers.where().world(getOnlinePlayer().getWorld()).radius(100);
	}

}
