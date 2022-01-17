package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static gg.projecteden.nexus.utils.PlayerUtils.getPlayer;

@Description("Unsign a book or edit book metadata")
public class BookCommand extends CustomCommand {
	private EquipmentSlot hand;
	private ItemStack book;
	private BookMeta meta;

	public BookCommand(@NonNull CommandEvent event) {
		super(event);
		if (isCommandEvent()) {
			hand = getHandWithToolRequired();
			book = getToolRequired();
			if (!(book.getItemMeta() instanceof BookMeta))
				error("You must be holding a written or writable book");

			meta = (BookMeta) book.getItemMeta();
		}
	}

	@Path
	void edit() {
		checkCanEdit();

		Material type = book.getType() == Material.WRITABLE_BOOK ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK;
		ItemStack editable = new ItemStack(type, book.getAmount());
		editable.setItemMeta(meta);
		inventory().setItem(hand, editable);
	}

	@Permission(Group.STAFF)
	@Path("author <name...>")
	void author(@Arg(tabCompleter = OfflinePlayer.class) String name) {
		meta.setAuthor(name);
		book.setItemMeta(meta);
		send(PREFIX + "Author set to &e" + name);
	}

	@Path("title <title...>")
	void title(String title) {
		checkCanEdit();

		meta.setTitle(title);
		book.setItemMeta(meta);
		send(PREFIX + "Title set to &e" + title);
	}

	private void checkCanEdit() {
		if (!canEdit())
			error("You do not own this book");
	}

	private boolean canEdit() {
		return isSelf(getPlayer(meta.getAuthor())) || hasPermission(Group.STAFF);
	}

}
