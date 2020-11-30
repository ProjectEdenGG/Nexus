package me.pugabyte.nexus.features.store.perks;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.commands.models.events.TabEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils.Gradient;
import me.pugabyte.nexus.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("nameentity")
@Permission("entityname.use")
public class EntityNameCommand extends CustomCommand {
	private LivingEntity targetEntity;

	public EntityNameCommand(@NonNull CommandEvent event) {
		super(event);
		if (!(event instanceof TabEvent))
			targetEntity = getTargetLivingEntityRequired();
	}

	@Path("(null|none|reset)")
	void name() {
		name(null);
	}

	@Path("<name...>")
	void name(@Arg(max = 17) String name) { // Why 17 and not 16? idk.
		if (targetEntity instanceof ItemFrame) {
			ItemFrame itemFrame = (ItemFrame) targetEntity;
			ItemStack item = itemFrame.getItem();
			ItemBuilder.setName(item, name);
			itemFrame.setItem(item);
		} else {
			targetEntity.setCustomName(colorize(name));
			targetEntity.setCustomNameVisible(name != null);
		}
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
