package me.pugabyte.bncore.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils.Gradient;
import me.pugabyte.bncore.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;

@Aliases("nameitem")
@Permission("itemname.use")
public class ItemNameCommand extends CustomCommand {

	public ItemNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	void name() {
		name(null);
	}

	@Path("<name...>")
	void name(String name) {
		ItemBuilder.setName(getToolRequired(), name);
	}

	@Path("gradient <color1> <color2> <name...>")
	void gradient(ChatColor color1, ChatColor color2, String input) {
		name(Gradient.of(color1, color2).apply(input));
	}

	@Path("rainbow <name...>")
	void rainbow(String input) {
		name(Rainbow.apply(input));
	}

}
