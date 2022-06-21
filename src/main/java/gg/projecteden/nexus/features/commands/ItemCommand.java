package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

@Aliases("i")
@Permission("essentials.item")
public class ItemCommand extends CustomCommand {

	public ItemCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<type> [amount] [nbt...]")
	void run(ItemStack item, @Arg(min = 1, max = 2304, minMaxBypass = Group.STAFF) Integer amount, @Arg(permission = Group.STAFF) String nbt) {
		item.setAmount(amount == null ? item.getType().getMaxStackSize() : amount);
		PlayerUtils.giveItem(player(), item, nbt);
	}

	@Path("rp <material> <id>")
	@Permission(Group.STAFF)
	void rp(Material material, int id) {
		PlayerUtils.giveItem(player(), new ItemBuilder(material).modelId(id).build());
	}

	@Path("tag <tag> [amount]")
	void tag(Tag<?> tag, @Arg("1") int amount) {
		tag.getValues().forEach(tagged -> {
			if (tagged instanceof Material material)
				run(new ItemStack(material), amount, null);
			else if (tagged instanceof CustomBlock customBlock)
				run(customBlock.get().getItemStack(), amount, null);
			else
				error("Unsupported tag type");
		});
	}

}
