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
import me.pugabyte.nexus.utils.StringUtils.Gradient;
import me.pugabyte.nexus.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.applyFormattingToAll;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("nameentity")
@Permission("entityname.use")
public class EntityNameCommand extends CustomCommand {
	private Entity targetEntity;

	public EntityNameCommand(@NonNull CommandEvent event) {
		super(event);
		if (isCommandEvent()) {
			targetEntity = getTargetEntityRequired();
			if (!(targetEntity instanceof LivingEntity) && !(targetEntity instanceof ItemFrame))
				error("You must be looking at a living entity or an item frame");
		}
	}

	@Path("(null|none|reset)")
	void reset() {
		name(null, false, false, false, false, false);
	}

	@Path("<name...>")
	void name(
			@Arg(max = 17) String input, // Why 17 and not 16? idk.
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		input = applyFormattingToAll(input, bold, strikethrough, underline, italic, magic);

		if (targetEntity instanceof ItemFrame) {
			ItemFrame itemFrame = (ItemFrame) targetEntity;
			ItemStack item = itemFrame.getItem();
			ItemBuilder.setName(item, input);
			itemFrame.setItem(item);
		} else {
			targetEntity.setCustomName(colorize(input));
			targetEntity.setCustomNameVisible(input != null);
		}
	}

	@Path("gradient <color1> <color2> <name...>")
	void gradient(
			ChatColor color1,
			ChatColor color2,
			@Arg(max = 17) String input,
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		name(Gradient.of(color1, color2).apply(input), bold, strikethrough, underline, italic, magic);
	}

	@Path("rainbow <name...>")
	void rainbow(
			@Arg(max = 17) String input,
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		name(Rainbow.apply(input), bold, strikethrough, underline, italic, magic);
	}

	@Path("visible [enable]")
	void visible(Boolean enable) {
		if (enable == null)
			enable = !targetEntity.isCustomNameVisible();

		targetEntity.setCustomNameVisible(enable);
		send(PREFIX + "Name tag visibility " + (enable ? "&aenabled" : "&cdisabled"));
	}

}
