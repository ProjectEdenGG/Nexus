package gg.projecteden.nexus.features.store.perks;

import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static gg.projecteden.nexus.features.store.perks.ItemNameCommand.PERMISSION;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.applyFormattingToAll;
import static gg.projecteden.nexus.utils.StringUtils.decolorize;

@Aliases("nameitem")
@Permission(PERMISSION)
public class ItemNameCommand extends CustomCommand {
	public static final String PERMISSION = "itemname.use";

	public ItemNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	void reset() {
		name(null, false, false, false, false, false);
	}

	@Path("resetAll <material>")
	void reset(Material material) {
		int count = 0;
		for (ItemStack content : inventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			if (content.getType() != material)
				continue;

			final ItemMeta meta = content.getItemMeta();
			meta.setDisplayName(null);
			content.setItemMeta(meta);
			++count;
		}

		send(PREFIX + "Reset item names of " + count + " " + camelCase(material));
	}

	@Path("<name...>")
	void name(
			String input,
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		verify(input);

		final ItemStack tool = getToolRequired();
		final String name = applyFormattingToAll(input, bold, strikethrough, underline, italic, magic);
		ItemBuilder.setName(tool, name);
		send(PREFIX + "Name of &e" + camelCase(tool.getType()).toLowerCase() + " &3" + (name == null ? "reset" : "set to &e" + name));
	}

	@Path("gradient <colors> <name...>")
	void gradient(
			@Arg(type = ChatColor.class) List<ChatColor> colors,
			String input,
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		verify(input);
		name(Gradient.of(colors).apply(input), bold, strikethrough, underline, italic, magic);
	}

	@Path("rainbow <name...>")
	void rainbow(
			String input,
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		verify(input);
		name(Rainbow.apply(input), bold, strikethrough, underline, italic, magic);
	}

	private void verify(String input) {
		if (input == null)
			return;

		int length = StringUtils.stripColor(input).length();
		if (length > 50 && !isAdmin())
			error("Max length is 50, input was " + length);

		if (Censor.isCensored(player(), input)) {
			String message = "&cItem name content by " + nickname() + " was censored: &e" + input;
			Broadcast.staff().prefix("Censor").message(message).send();
			error("Inappropriate input");
		}
	}

	@Path("copy")
	void copy() {
		final ItemStack tool = getToolRequired();
		if (!tool.getItemMeta().hasDisplayName())
			error("This item does not have a custom name");
		final String displayName = tool.getItemMeta().getDisplayName();
		send(json(PREFIX + displayName).hover("&fClick to copy").copy(decolorize(displayName)));
	}

}
