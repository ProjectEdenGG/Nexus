package me.pugabyte.nexus.features.store.perks;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.StringUtils.Gradient;
import me.pugabyte.nexus.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;

@Aliases("nameitem")
@Permission("itemname.use")
public class ItemNameCommand extends CustomCommand {

	public ItemNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	void reset() {
		name(null);
	}

	@Path("<name...>")
	void name(String name) {
		verify(name);
		ItemBuilder.setName(getToolRequired(), name);
	}

	@Path("gradient <color1> <color2> <name...>")
	void gradient(ChatColor color1, ChatColor color2, String input) {
		verify(input);
		name(Gradient.of(color1, color2).apply(input));
	}

	@Path("rainbow <name...>")
	void rainbow(String input) {
		verify(input);
		name(Rainbow.apply(input));
	}

	private void verify(String input) {
		if (input == null)
			return;

		int length = StringUtils.stripColor(input).length();
		if (length > 50)
			error("Max length is 50, input was " + length);
	}

}
