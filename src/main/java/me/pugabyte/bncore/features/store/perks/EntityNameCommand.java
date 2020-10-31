package me.pugabyte.bncore.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.commands.models.events.TabEvent;
import me.pugabyte.bncore.utils.StringUtils.Gradient;
import me.pugabyte.bncore.utils.StringUtils.Rainbow;
import me.pugabyte.bncore.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@Aliases("nameentity")
@Permission("entityname.use")
public class EntityNameCommand extends CustomCommand {
	private final LivingEntity targetEntity;

	public EntityNameCommand(@NonNull CommandEvent event) {
		super(event);
		targetEntity = Utils.getTargetEntity(player());
		if (!(event instanceof TabEvent) && targetEntity == null)
			error("You must be looking at an entity");
	}

	@Path("(null|none|reset)")
	void name() {
		name(null);
	}

	@Path("<name...>")
	void name(String name) {
		if (stripColor(name).length() > 17) // Why 17 and not 16? idk.
			error("Name cannot be longer than 17 characters");

		targetEntity.setCustomName(colorize(name));
		targetEntity.setCustomNameVisible(name != null);
	}

	@Path("gradient <color1> <color2> <name...>")
	void gradient(ChatColor color1, ChatColor color2, String input) {
		name(Gradient.of(color1, color2).apply(input));
	}

	@Path("rainbow <name...>")
	void rainbow(String input) {
		name(Rainbow.apply(input));
	}

	@Path("visible [enable]")
	void visible(Boolean enable) {
		if (enable == null)
			enable = !targetEntity.isCustomNameVisible();

		targetEntity.setCustomNameVisible(enable);
		send(PREFIX + "Name tag visibility " + (enable ? "&aenabled" : "&cdisabled"));
	}

}
