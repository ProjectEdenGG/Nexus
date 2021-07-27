package gg.projecteden.nexus.models.costume;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.CostumeConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

	public void sendPacket() {
		if (!shouldSendPacket())
			return;

		final Player player = getOnlinePlayer();
		final ItemStack item = activeCostume.getModel().getDisplayItem();
		final List<Player> players = player.getWorld().getPlayers();
		final ItemSlot slot = activeCostume.getType().getPacketSlot();
		PacketUtils.sendFakeItem(player, players, item, slot);
	}

	private boolean shouldSendPacket() {
		final Player player = getOnlinePlayer();
		if (activeCostume == null)
			return false;

		// TODO Fix creative duping
//		if (player.getGameMode() == GameMode.SPECTATOR)
//			return;
		if (!GameModeWrapper.of(player.getGameMode()).isSurvival())
			return false;

		if (DISABLED_WORLDS.contains(WorldGroup.of(player)))
			return false;

		return true;
	}

}
