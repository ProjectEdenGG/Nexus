package me.pugabyte.bncore.models.tip;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("tip")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Tip extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<TipType> received = new ArrayList<>();

	public boolean show(TipType tipType) {
		if (!isOnline())
			throw new PlayerNotOnlineException(getOfflinePlayer());

		if (!received.contains(tipType)) {
			received.add(tipType);
			new TipService().save(this);
			return true;
		}

		if (tipType.getPermissions().length > 0) {
			boolean hasPerm = false;
			for (String permission : tipType.getPermissions()) {
				if (getPlayer().hasPermission(permission))
					hasPerm = true;
			}

			if (!hasPerm)
				return false;
		}

		if (tipType.getRetryChance() > 0)
			return RandomUtils.chanceOf(tipType.getRetryChance());

		return true;
	}

	@NoArgsConstructor
	public enum TipType {
		CONCRETE(1),
		LWC_CHEST(1, "rank.guest"),
		LWC_FURNACE(1, "rank.guest"),
		RESOURCE_WORLD_STORAGE(15);

		@Getter
		private int retryChance;
		@Getter
		private String[] permissions = new String[]{};

		TipType(int retryChance) {
			this.retryChance = retryChance;
		}

		TipType(String... permissions) {
			this.permissions = permissions;
		}

		TipType(int retryChance, String... permissions) {
			this.retryChance = retryChance;
			this.permissions = permissions;
		}
	}

}
