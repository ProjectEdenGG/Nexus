package me.pugabyte.bncore.utils;

import com.google.common.base.Strings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookBuilder {
	private final ItemStack book;
	private final BookMeta meta;

	public BookBuilder() {
		this(new ItemStack(Material.WRITTEN_BOOK));
	}

	public BookBuilder(ItemStack book) {
		this.book = book;
		meta = (BookMeta) book.getItemMeta();
		if (Strings.isNullOrEmpty(meta.getTitle()))
			meta.setTitle("Book Menu");
		if (Strings.isNullOrEmpty(meta.getAuthor()))
			meta.setAuthor("Server");
	}

	public BookBuilder addPage(JsonBuilder builder) {
		meta.spigot().addPage(builder.build());
		return this;
	}

	public void open(Player player) {
		book.setItemMeta(meta);
		player.openBook(book);
	}
}
