package gg.projecteden.nexus.models.curiosity;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "curiosity", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Curiosity implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<CuriosityReward> earned = new HashSet<>();

	public boolean has(CuriosityReward reward) {
		return earned.contains(reward);
	}

	public void give(CuriosityReward reward) {
		earned.add(reward);
		if (isOnline()) {
			PlayerUtils.giveItem(getOnlinePlayer(), reward.getItem());
			PlayerUtils.send(getOnlinePlayer(), "");
			PlayerUtils.send(getOnlinePlayer(), "&3You earned &e" + StringUtils.pretty(reward.getItem()) + " &3for learning!");
		}
	}

	public enum CuriosityReward {
		COOKIES(new ItemStack(Material.COOKIE, 16)),
		PUMPKIN_PIE(new ItemStack(Material.PUMPKIN_PIE, 12)),
		CAKE(new ItemStack(Material.CAKE));

		@Getter
		private final ItemStack item;

		CuriosityReward(ItemStack item) {
			this.item = item;
		}
	}

}
