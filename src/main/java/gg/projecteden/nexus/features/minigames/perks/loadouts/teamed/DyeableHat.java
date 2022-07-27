package gg.projecteden.nexus.features.minigames.perks.loadouts.teamed;

import gg.projecteden.nexus.features.minigames.models.perks.common.TeamHatPerk;
import gg.projecteden.nexus.utils.ColorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DyeableHat implements TeamHatPerk {
	private final @NotNull String name;
	private final int price;
	private final @NotNull List<String> description;
	private final @NotNull ItemStack dyeableBase;

	public DyeableHat(@NotNull String name, int price, @NotNull String description, @NotNull ItemStack dyeableBase) {
		this(name, price, Collections.singletonList(description), dyeableBase);
	}

	@Override
	public ItemStack getColorItem(ColorType color) {
		ItemStack item = dyeableBase.clone();
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color.getBukkitColor());
		item.setItemMeta(meta);
		return item;
	}
}
