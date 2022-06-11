package gg.projecteden.nexus.models.vaults;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.utils.StringUtils.plural;

@Data
@Entity(value = "vault_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class VaultUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<Integer, List<ItemStack>> vaults = new HashMap<>();
	private int limit;

	public static final int ROWS = 4;

	public List<ItemStack> get(int page) {
		return get(page, getOnlinePlayer());
	}

	public List<ItemStack> get(int page, Player viewer) {
		if (!getRank().isAdmin())
			if (page > limit) {
				final String descriptor = PlayerUtils.isSelf(this, viewer) ? "You only own" : getNickname() + " only owns";
				throw new InvalidInputException(descriptor + " &e" + limit + plural(" &cvault", limit));
			}

		return vaults.computeIfAbsent(page, $ -> Collections.nCopies(ROWS * 9, new ItemStack(Material.AIR)));
	}

	public void update(int vault, List<ItemStack> contents) {
		vaults.put(vault, contents);
	}

	public void increaseLimit() {
		increaseLimit(1);
	}

	public void increaseLimit(int amount) {
		limit += amount;
	}

}
