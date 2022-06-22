package gg.projecteden.nexus.features.store.perks;

import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.features.listeners.Restrictions.isPerkAllowedAt;
import static gg.projecteden.nexus.features.store.perks.EntityNameCommand.PERMISSION;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.applyFormattingToAll;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

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

			if (!hasPermission(Group.STAFF) && (isInvulnerable || hasAI || isFixed || isMarker))
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
		verify(input);

		if (targetEntity instanceof ItemFrame itemFrame) {
			ItemStack item = itemFrame.getItem();
			if (isNullOrAir(item))
				error("Empty item frames cannot be renamed");
			ItemBuilder.setName(item, input);
			itemFrame.setItem(item);
		} else {
			targetEntity.setCustomName(colorize(input));
			targetEntity.setCustomNameVisible(input != null);

			final boolean hasName = input != null;
			if (targetEntity instanceof InventoryHolder holder) {
				final boolean hasItems = !ItemUtils.nonNullOrAir(holder.getInventory().getContents()).isEmpty();
				targetEntity.setPersistent(hasItems || hasName);
			} else
				targetEntity.setPersistent(hasName);
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
		verify(input);
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
		verify(input);
		name(Rainbow.apply(input), bold, strikethrough, underline, italic, magic);
	}

	@Path("visible [enable]")
	void visible(Boolean enable) {
		if (enable == null)
			enable = !targetEntity.isCustomNameVisible();

		targetEntity.setCustomNameVisible(enable);
		send(PREFIX + "Name tag visibility " + (enable ? "&aenabled" : "&cdisabled"));
	}

	private void verify(String input) {
		if (input == null)
			return;

		if (Censor.isCensored(player(), input)) {
			String message = "&cEntity name content by " + nickname() + " was censored: &e" + input;
			Broadcast.staff().prefix("Censor").message(message).send();
			error("Inappropriate input");
		}

		if (!isPerkAllowedAt(player(), location()))
			error("This command is not allowed here");
	}

}
