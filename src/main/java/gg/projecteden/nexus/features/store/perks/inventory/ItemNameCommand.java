package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Aliases("nameitem")
@Permission(ItemNameCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class ItemNameCommand extends CustomCommand {
	public static final String PERMISSION = "itemname.use";
	public static final String RENAME_SETTING_ERROR = "&cCannot rename this item";

	public ItemNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	@Description("Remove an item's custom name")
	void reset() {
		name(null, false, false, false, false, false);
	}

	@Path("resetAll <material>")
	@Description("Give an item a custom name")
	void reset(Material material) {
		int count = 0;
		for (ItemStack content : inventory().getContents()) {
			if (Nullables.isNullOrAir(content))
				continue;

			if (content.getType() != material)
				continue;

			if (!new ItemBuilder(content).is(ItemSetting.RENAMEABLE))
				continue;

			final ItemMeta meta = content.getItemMeta();

			DecorationConfig decorationConfig = DecorationConfig.of(content);
			if (decorationConfig != null)
				meta.setDisplayName(StringUtils.colorize("&f" + decorationConfig.getName()));
			else
				meta.setDisplayName(null);

			content.setItemMeta(meta);
			++count;
		}

		send(PREFIX + "Reset item names of " + count + " " + camelCase(material));
	}

	@Path("<name...>")
	@Description("Give an item a custom name with a color gradient")
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

		if (!new ItemBuilder(tool).is(ItemSetting.RENAMEABLE))
			error(RENAME_SETTING_ERROR);

		DecorationConfig decorationConfig = DecorationConfig.of(tool);
		if (decorationConfig != null && input == null)
			input = "&f" + decorationConfig.getName();

		final String name = StringUtils.applyFormattingToAll(input, bold, strikethrough, underline, italic, magic);
		ItemBuilder.setName(tool, name);
		send(PREFIX + "Name of &e" + camelCase(tool.getType()).toLowerCase() + " &3" + (name == null ? "reset" : "set to &e" + name));
	}

	@Path("gradient <colors> <name...>")
	@Description("Give an item a custom name with a rainbow gradient")
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
	@Description("Toggle whether an item's custom name is always visible")
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
	@Description("Print an item's custom name in chat for copying")
	void copy() {
		final ItemStack tool = getToolRequired();
		if (!tool.getItemMeta().hasDisplayName())
			error("This item does not have a custom name");
		final String displayName = tool.getItemMeta().getDisplayName();
		send(json(PREFIX + displayName).hover("&fClick to copy").copy(StringUtils.decolorize(displayName)));
	}

}
