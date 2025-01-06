package gg.projecteden.nexus.features.store.perks.visuals;

import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.listeners.Restrictions;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
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

@Aliases("nameentity")
@Permission(EntityNameCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Visuals")
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
	@Description("Remove an entity's custom name")
	void reset() {
		name(null, false, false, false, false, false);
	}

	@Path("<name...>")
	@Description("Give an entity a custom name")
	void name(
			@Arg(max = 17) String input, // Why 17 and not 16? idk.
			@Switch boolean bold,
			@Switch boolean strikethrough,
			@Switch boolean underline,
			@Switch boolean italic,
			@Switch boolean magic
	) {
		input = StringUtils.applyFormattingToAll(input, bold, strikethrough, underline, italic, magic);
		verify(input);

		if (targetEntity instanceof ItemFrame itemFrame) {
			ItemStack item = itemFrame.getItem();
			if (Nullables.isNullOrAir(item))
				error("Empty item frames cannot be renamed");
			ItemBuilder.setName(item, input);
			itemFrame.setItem(item);
		} else {
			targetEntity.setCustomName(StringUtils.colorize(input));
			targetEntity.setCustomNameVisible(input != null);

			if (targetEntity instanceof LivingEntity livingEntity) {
				final boolean hasName = input != null;
				if (livingEntity instanceof InventoryHolder holder) {
					final boolean hasItems = !ItemUtils.nonNullOrAir(holder.getInventory().getContents()).isEmpty();
					livingEntity.setRemoveWhenFarAway(!hasName && !hasItems);
				} else
					livingEntity.setRemoveWhenFarAway(!hasName);
			}
		}
	}

	@Path("gradient <colors> <name...>")
	@Description("Give an entity a custom name with a color gradient")
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
	@Description("Give an entity a custom name with a rainbow gradient")
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
	@Description("Toggle whether an entity's custom name is always visible")
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

		if (!Restrictions.isPerkAllowedAt(player(), location()))
			error("This command is not allowed here");
	}

}
