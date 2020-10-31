package me.pugabyte.bncore.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils.Gradient;
import me.pugabyte.bncore.utils.StringUtils.Rainbow;
import me.pugabyte.bncore.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Aliases("nameentity")
@Permission("entityname.use")
public class EntityNameCommand extends CustomCommand {

	public EntityNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	void name() {
		name(null);
	}

	@Path("<name...>")
	void name(String name) {
		LivingEntity targetEntity = Utils.getTargetEntity(player());
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

}
