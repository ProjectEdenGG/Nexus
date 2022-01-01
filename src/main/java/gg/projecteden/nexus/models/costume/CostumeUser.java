package gg.projecteden.nexus.models.costume;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.store.gallery.GalleryPackage;
import gg.projecteden.nexus.features.store.gallery.StoreGallery;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.CostumeConverter;
import gg.projecteden.nexus.models.costume.Costume.CostumeType;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

@Data
@Entity(value = "costume_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, CostumeConverter.class})
public class CostumeUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int vouchers;

	private String activeCostume;
	private String activeDisplayCostume;
	private Set<String> ownedCostumes = new HashSet<>();

	private static final List<WorldGroup> DISABLED_WORLDS = List.of(WorldGroup.MINIGAMES);
	private static final List<GameMode> DISABLED_GAMEMODES = List.of(GameMode.SPECTATOR);

	public void setActiveCostumeId(String activeCostume) {
		this.activeCostume = activeCostume;
		if (activeCostume == null)
			sendResetPackets();
	}

	public void setActiveCostume(Costume activeCostume) {
		setActiveCostumeId(activeCostume == null ? null : activeCostume.getId());
	}

	public boolean hasActiveCostume() {
		return !isNullOrEmpty(activeCostume);
	}

	public boolean owns(CustomModel model) {
		return owns(Costume.of(model));
	}

	public boolean owns(Costume costume) {
		return ownedCostumes.contains(costume.getId());
	}

	public void sendCostumePacket() {
		if (!shouldSendPacket())
			return;

		final Costume costume = Costume.of(activeCostume);
		if (costume == null)
			return;

		sendPacket(costume.getItem(), costume.getType().getSlot());
	}

	public void sendResetPackets() {
		if (!isOnline())
			return;

		for (EquipmentSlot slot : CostumeType.getSlots())
			sendPacket(getOnlinePlayer().getInventory().getItem(slot), slot);
	}

	private void sendPacket(ItemStack item, EquipmentSlot slot) {
		final Player player = getOnlinePlayer();
		final List<Player> players = player.getWorld().getPlayers();
		PacketUtils.sendFakeItem(player, players, item, slot);
	}

	private boolean shouldSendPacket() {
		if (!isOnline())
			return false;

		final Player player = getOnlinePlayer();
		if (activeCostume == null)
			return false;

		if (DISABLED_GAMEMODES.contains(player.getGameMode()))
			return false;

		if (DISABLED_WORLDS.contains(WorldGroup.of(player)))
			return false;

		return true;
	}

	public void setActiveDisplayCostumeId(String activeDisplayCostume) {
		this.activeDisplayCostume = activeDisplayCostume;
		if (activeDisplayCostume == null)
			sendResetDisplayPackets();
	}

	public void setActiveDisplayCostume(Costume activeDisplayCostume) {
		setActiveDisplayCostumeId(activeDisplayCostume == null ? null : activeDisplayCostume.getId());
	}

	public void sendDisplayCostumePacket() {
		if (!shouldSendDisplayPacket())
			return;

		final Costume costume = Costume.of(activeDisplayCostume);
		if (costume == null)
			return;

		sendDisplayPacket(costume.getItem(), costume.getType().getSlot());
	}

	public void sendResetDisplayPackets() {
		if (!isOnline())
			return;

		for (EquipmentSlot slot : CostumeType.getSlots())
			sendDisplayPacket(new ItemStack(Material.STONE_BUTTON), slot);
	}

	private void sendDisplayPacket(ItemStack item, EquipmentSlot slot) {
		final var entity = GalleryPackage.COSTUMES.entity();
		if (entity == null)
			return;

		PacketUtils.sendFakeItem(entity, getOnlinePlayer(), item, slot);
	}

	private boolean shouldSendDisplayPacket() {
		if (!isOnline())
			return false;

		if (activeDisplayCostume == null)
			return false;

		if (!getOnlinePlayer().getWorld().equals(StoreGallery.getWorld()))
			return false;

		return true;
	}

	public void addVouchers(int amount) {
		setVouchers(vouchers + amount);
	}

	public void takeVouchers(int amount) {
		setVouchers(vouchers - amount);
	}

	public void setVouchers(int amount) {
		if (amount < 0)
			throw new InvalidInputException("You do not have enough vouchers"); // TODO NegativeBalanceException?

		vouchers = amount;
	}

}
