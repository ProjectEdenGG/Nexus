package me.pugabyte.nexus.features.events;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EventStoreProvider extends MenuUtils implements InventoryProvider {

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemStack tokens = new ItemBuilder(Material.BOOK).name("Tokens").build();
		ItemStack paintings = new ItemBuilder(Material.PAINTING).name("Pre-imported Paintings").build();
		ItemStack images = new ItemBuilder(Material.FLOWER_BANNER_PATTERN).name("Import an images").build();
		ItemStack hdbHeads = new ItemBuilder(Material.PLAYER_HEAD).name("HDB Heads").build();
		ItemStack emojiHeads = new ItemBuilder(Material.PLAYER_HEAD).name("Emoji Heads").build();
		ItemStack particles = new ItemBuilder(Material.REDSTONE).name("Particles").build();
		ItemStack wings = new ItemBuilder(Material.ELYTRA).name("Particle Wings").build();
		ItemStack emotes = new ItemBuilder(Material.PAPER).name("Emotes").build();
		ItemStack songs = new ItemBuilder(Material.JUKEBOX).name("Songs").build();
		ItemStack store = new ItemBuilder(Material.CHEST).name("Store").build();

		addCloseItem(contents);
		contents.set(0, 8, ClickableItem.empty(tokens));

		contents.set(1, 1, ClickableItem.empty(paintings));
		contents.set(1, 3, ClickableItem.empty(images));
		contents.set(1, 5, ClickableItem.empty(hdbHeads));
		contents.set(1, 7, ClickableItem.empty(emojiHeads));
		contents.set(3, 0, ClickableItem.empty(particles));
		contents.set(3, 2, ClickableItem.empty(wings));
		contents.set(3, 4, ClickableItem.empty(emotes));
		contents.set(3, 6, ClickableItem.empty(songs));
		contents.set(3, 8, ClickableItem.empty(store));
	}
}
