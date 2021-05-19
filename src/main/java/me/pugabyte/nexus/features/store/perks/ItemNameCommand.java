package me.pugabyte.nexus.features.store.perks;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Switch;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.StringUtils.Gradient;
import me.pugabyte.nexus.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

import static me.pugabyte.nexus.features.store.perks.ItemNameCommand.PERMISSION;
import static me.pugabyte.nexus.utils.StringUtils.applyFormattingToAll;

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
		ItemBuilder.setName(getToolRequired(), applyFormattingToAll(input, bold, strikethrough, underline, italic, magic));
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
		if (length > 50)
			error("Max length is 50, input was " + length);
	}

}
