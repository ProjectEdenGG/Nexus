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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.nexus.features.store.perks.EntityNameCommand.PERMISSION;
import static me.pugabyte.nexus.utils.StringUtils.applyFormattingToAll;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("nameentity")
@Permission(PERMISSION)
public class EntityNameCommand extends CustomCommand {
	public static final String PERMISSION = "entityname.use";
	private Entity targetEntity;

	public EntityNameCommand(@NonNull CommandEvent event) {
		super(event);
		if (isCommandEvent()) {
			targetEntity = getTargetEntityRequired();

			if (!(targetEntity instanceof LivingEntity) && !(targetEntity instanceof ItemFrame))
				error("You must be looking at a living entity or an item frame");

			boolean hasAI = targetEntity instanceof LivingEntity livingEntity && !livingEntity.hasAI();
			boolean isFixed = targetEntity instanceof ItemFrame itemFrame && itemFrame.isFixed();
			boolean isMarker = targetEntity instanceof ArmorStand armorStand && armorStand.isMarker();
			boolean isInvulnerable = targetEntity.isInvulnerable();

			if (!hasPermission("group.staff") && (isInvulnerable || hasAI || isFixed || isMarker))
				error("You cannot name that entity");
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

		if (targetEntity instanceof ItemFrame itemFrame) {
			ItemStack item = itemFrame.getItem();
			ItemBuilder.setName(item, input);
			itemFrame.setItem(item);
		} else {
			targetEntity.setCustomName(colorize(input));
			targetEntity.setCustomNameVisible(input != null);
		}
	}

	@Path("gradient <colors> <name...>")
	void gradient(
			@Arg(type = ChatColor.class) List<ChatColor> colors,
			@Arg(max = 17) String input,
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		name(Gradient.of(colors).apply(input), bold, strikethrough, underline, italic, magic);
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
