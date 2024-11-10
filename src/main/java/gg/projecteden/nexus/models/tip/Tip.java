package gg.projecteden.nexus.models.tip;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventory;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Data
@Entity(value = "tip", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Tip implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<TipType> received = new ArrayList<>();

	public boolean show(TipType tipType) {
		if (!isOnline())
			throw new PlayerNotOnlineException(this);

		if (!received.contains(tipType)) {
			received.add(tipType);
			new TipService().save(this);
			return true;
		}

		if (!new CooldownService().check(uuid, "Tip-" + tipType.name(), tipType.getCooldown()))
			return false;

		if (tipType.getPredicate() != null)
			if (!tipType.getPredicate().test(getOnlinePlayer()))
				return false;

		if (tipType.getRetryChance() > 0)
			return RandomUtils.chanceOf(tipType.getRetryChance());

		return true;
	}

	public enum TipType {
		CONCRETE(1, TickTime.HOUR, player -> player.hasPermission(Group.NON_STAFF)),
		LWC_CHEST(1, TickTime.MINUTE.x(15), player -> Rank.of(player) == Rank.GUEST),
		LWC_FURNACE(1, TickTime.MINUTE.x(15), player -> Rank.of(player) == Rank.GUEST),
		RESOURCE_WORLD_STORAGE(15),
		RESOURCE_WORLD_WARNING(15, TickTime.HOUR.get()),
		SPAM_ATTACK(50, TickTime.MINUTE.x(5)),
		AUTOSORT_SORT_INVENTORY(1, TickTime.WEEK, player -> player.hasPermission(AutoInventory.PERMISSION)),
		AUTOSORT_SORT_CHESTS(1, TickTime.WEEK, player -> player.hasPermission(AutoInventory.PERMISSION)),
		AUTOSORT_REFILL(1, TickTime.WEEK, player -> player.hasPermission(AutoInventory.PERMISSION)),
		AUTOSORT_DEPOSIT_ALL(1, TickTime.WEEK, player -> player.hasPermission(AutoInventory.PERMISSION)),
		AUTOSORT_DEPOSIT_QUICK(1, TickTime.WEEK, player -> player.hasPermission(AutoInventory.PERMISSION));

		@Getter
		private final int retryChance;
		@Getter
		private long cooldown;
		@Getter
		private Predicate<Player> predicate;

		TipType(int retryChance) {
			this.retryChance = retryChance;
		}

		TipType(int retryChance, long cooldown) {
			this.retryChance = retryChance;
			this.cooldown = cooldown;
		}

		TipType(int retryChance, TickTime cooldown, Predicate<Player> predicate) {
			this(retryChance, cooldown.get(), predicate);
		}

		TipType(int retryChance, long cooldown, Predicate<Player> predicate) {
			this.retryChance = retryChance;
			this.cooldown = cooldown;
			this.predicate = predicate;
		}
	}

}
