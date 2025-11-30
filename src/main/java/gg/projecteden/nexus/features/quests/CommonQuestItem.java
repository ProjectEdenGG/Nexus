package gg.projecteden.nexus.features.quests;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum CommonQuestItem implements QuestItem {
	COIN_POUCH(new ItemBuilder(ItemModelType.EVENT_COIN_POUCH).name("&oCoin Pouch").lore("&3Stores currency").undroppable().untrashable().unframeable()),
	DISCOUNT_CARD(new ItemBuilder(ItemModelType.EVENT_DISCOUNT_CARD).name("&oDiscount Card").lore("&3Shop prices lowered by &e20%")),

	BASIC_BACKPACK(new ItemBuilder(BackpackTier.BASIC.create()).undroppable().unstorable().untrashable()),
	IRON_BACKPACK(new ItemBuilder(BackpackTier.IRON.create()).undroppable().unstorable().untrashable()),
	GOLD_BACKPACK(new ItemBuilder(BackpackTier.GOLD.create()).undroppable().unstorable().untrashable()),
	DIAMOND_BACKPACK(new ItemBuilder(BackpackTier.DIAMOND.create()).undroppable().unstorable().untrashable()),
	NETHERITE_BACKPACK(new ItemBuilder(BackpackTier.NETHERITE.create()).undroppable().unstorable().untrashable()),
	;

	private final ItemBuilder itemBuilder;

	public static final String COIN_POUCH_NBT_KEY = "quests_coin_pouch";
	public static final double DISCOUNT_CARD_PERCENT = 0.20;

	@Override
	public ItemBuilder getItemBuilder() {
		ItemBuilder result = itemBuilder.itemFlags(ItemFlags.HIDE_ALL);

		if (this == COIN_POUCH)
			result.nbt(nbtItem -> nbtItem.setInteger(COIN_POUCH_NBT_KEY, 0));

		return result;
	}

	@Override
	public ItemStack get() {
		return getItemBuilder().build();
	}

	public ItemModelType getItemModel() {
		return ItemModelType.of(get());
	}

	public boolean isInInventoryOf(HasUniqueId uuid) {
		return Quester.of(uuid).has(get());
	}
}
