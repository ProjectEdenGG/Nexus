package me.pugabyte.nexus.models.tip;

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
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Time;

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

		if (!new CooldownService().check(getUuid(), "Tip." + tipType.name(), tipType.getCooldown()))
			return false;

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

	public enum TipType {
		CONCRETE(1, Time.HOUR, "group.nonstaff"),
		LWC_CHEST(1, Time.MINUTE.x(15), "rank.guest"),
		LWC_FURNACE(1, Time.MINUTE.x(15), "rank.guest"),
		RESOURCE_WORLD_STORAGE(15);

		@Getter
		@NonNull
		private int retryChance;
		@Getter
		private int cooldown;
		@Getter
		private String[] permissions = new String[]{};

		TipType(int retryChance) {
			this.retryChance = retryChance;
		}

		TipType(int retryChance, Time cooldown, String... permissions) {
			this(retryChance, cooldown.get(), permissions);
		}

		TipType(int retryChance, int cooldown, String... permissions) {
			this.retryChance = retryChance;
			this.cooldown = cooldown;
			this.permissions = permissions;
		}
	}

}
