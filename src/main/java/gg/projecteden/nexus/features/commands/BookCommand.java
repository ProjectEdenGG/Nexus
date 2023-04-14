package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.TabCompleter;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static gg.projecteden.nexus.utils.PlayerUtils.getPlayer;

public class BookCommand extends CustomCommand {
	private EquipmentSlot hand;
	private ItemStack book;
	private BookMeta meta;

	public BookCommand(@NonNull CommandEvent event) {
		super(event);
		if (isCommandEvent()) {
			if (event.getArgsString().equalsIgnoreCase("help"))
				return;

			hand = getHandWithToolRequired();
			book = getToolRequired();
			if (!(book.getItemMeta() instanceof BookMeta))
				error("You must be holding a written or writable book");

			meta = (BookMeta) book.getItemMeta();
		}
	}

	@NoLiterals
	@Description("Unsign a book")
	void edit() {
		checkCanEdit();

		Material type = book.getType() == Material.WRITABLE_BOOK ? Material.WRITTEN_BOOK : Material.WRITABLE_BOOK;
		ItemStack editable = new ItemStack(type, book.getAmount());
		editable.setItemMeta(meta);
		inventory().setItem(hand, editable);
	}

	@Permission(Group.STAFF)
	@Description("Set the author of a book")
	void author(@TabCompleter(Nerd.class) @Vararg String name) {
		meta.setAuthor(name);
		book.setItemMeta(meta);
		send(PREFIX + "Author set to &e" + name);
	}

	@Description("Set the title of a book")
	void title(@Vararg String title) {
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
