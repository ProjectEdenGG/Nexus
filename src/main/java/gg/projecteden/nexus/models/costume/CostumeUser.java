package gg.projecteden.nexus.models.costume;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.CostumeConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.costume.Costume.CostumeType;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
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

	private Costume activeCostume;
	private List<Costume> ownedCostumes = new ArrayList<>();

	private static final List<WorldGroup> DISABLED_WORLDS = List.of(WorldGroup.MINIGAMES);
	private static final List<GameMode> DISABLED_GAMEMODES = List.of(GameMode.SPECTATOR);

	public void setActiveCostume(Costume activeCostume) {
		this.activeCostume = activeCostume;
		if (activeCostume == null)
			sendResetPackets();
	}

	public void sendCostumePacket() {
		if (!shouldSendPacket())
			return;

		sendPacket(activeCostume.getItem(), activeCostume.getType().getPacketSlot());
	}

	// TODO Not working
	public void sendResetPackets() {
		for (CostumeType type : CostumeType.values()) {
			ItemStack item = getOnlinePlayer().getInventory().getItem(type.getSlot());
			Tasks.wait(1, () -> sendPacket(item, type.getPacketSlot()));
		}
	}

	private void sendPacket(ItemStack item, ItemSlot slot) {
		final Player player = getOnlinePlayer();
		final List<Player> players = player.getWorld().getPlayers();
		PacketUtils.sendFakeItem(player, players, item, slot);
	}

	private boolean shouldSendPacket() {
		final Player player = getOnlinePlayer();
		if (activeCostume == null)
			return false;

		if (DISABLED_GAMEMODES.contains(player.getGameMode()))
			return false;

		if (DISABLED_WORLDS.contains(WorldGroup.of(player)))
			return false;

		return true;
	}

}
