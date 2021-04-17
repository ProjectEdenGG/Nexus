package me.pugabyte.nexus.features.menus;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.JsonBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class BookBuilder<T extends BookBuilder<?>> {
	private final ItemStack book;
	private final BookMeta meta;

	public BookBuilder(ItemStack book) {
		this.book = book;
		meta = (BookMeta) book.getItemMeta();

		if (book.getType() == Material.WRITTEN_BOOK) {
			if (Strings.isNullOrEmpty(meta.getTitle()))
				meta.setTitle("Book Menu");
			if (Strings.isNullOrEmpty(meta.getAuthor()))
				meta.setAuthor("Server");
		}
	}

	public T addPage(JsonBuilder builder) {
		meta.addPages(builder.build());
		return self();
	}

	public ItemStack getBook() {
		book.setItemMeta(meta);
		return book;
	}

	public void open(Player player) {
		book.setItemMeta(meta);
		player.openBook(book);
	}

	abstract protected T self();

	public static class WrittenBookMenu extends BookBuilder<WrittenBookMenu> {

		public WrittenBookMenu() {
			super(new ItemStack(Material.WRITTEN_BOOK));
		}

		@Override
		protected WrittenBookMenu self() {
			return this;
		}
	}

	@Getter
	private static final HashMap<Player, EditableBookMenu> menus = new HashMap<>();

	@Deprecated // Not currently possible...
	private static class EditableBookMenu extends BookBuilder<EditableBookMenu> {
		private Consumer<BookMeta> onSign;

		public EditableBookMenu() {
			super(new ItemStack(Material.WRITABLE_BOOK));
		}

		public EditableBookMenu onSign(Consumer<BookMeta> onSign) {
			this.onSign = onSign;
			return self();
		}

		public void open(Player player) {
			BookBuilder.getMenus().put(player, this);
			super.open(player);
		}

		@Override
		protected EditableBookMenu self() {
			return this;
		}
	}

	static {
		Nexus.registerListener(new BookListener());
	}

	@NoArgsConstructor
	public static class BookListener implements Listener {

		@EventHandler
		public void onBookSign(PlayerEditBookEvent event) {
			EditableBookMenu menu = BookBuilder.getMenus().remove(event.getPlayer());
			if (menu == null)
				return;

			menu.onSign.accept(event.getNewBookMeta());
		}

	}
}
