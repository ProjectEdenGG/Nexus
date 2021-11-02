package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Pugmas21QuestItem {
	BALLOON(new ItemBuilder(Material.PAPER).name("Hot Air Balloon")),
	BALLOON_BASKET(new ItemBuilder(Material.PAPER).name("Hot Air Balloon Basket")),
	BALLOON_ENVELOPE(new ItemBuilder(Material.PAPER).name("Hot Air Balloon Envelope")),
	BOTTLED_CLOUD(new ItemBuilder(Material.PAPER).name("Bottled Cloud")),
	CRYSTAL_PIECE(new ItemBuilder(Material.PAPER).name("Crystal Piece")),
	CRYSTAL(new ItemBuilder(Material.PAPER).name("Crystal")),
	ELF_EARS(new ItemBuilder(Material.PAPER).name("Elf Ears")),
	PIRATE_HAT(new ItemBuilder(Material.PAPER).name("Pirate Hat")),
	PUGMAS_COOKIE_RECIPE(new ItemBuilder(Material.PAPER).name("Pugmas Cookie Recipe")),
	RIBBON(new ItemBuilder(Material.PAPER).name("Ribbon")),
	;

	private final ItemBuilder itemBuilder;

	public ItemStack get() {
		return itemBuilder.build();
	}


}
