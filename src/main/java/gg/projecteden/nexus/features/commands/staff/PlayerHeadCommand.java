package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.commands.staff.PlayerHeadCommand.PERMISSION;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

@Aliases("skull")
@Permission(PERMISSION)
@Redirect(from = "/donorskull", to = "/playerhead")
public class PlayerHeadCommand extends CustomCommand {
	public static final String PERMISSION = "essentials.skull";

	public PlayerHeadCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[owner]")
	@Cooldown(value = TickTime.DAY, bypass = Group.STAFF)
	void run(@Arg(value = "self", permission = Group.STAFF) Nerd owner) {
		giveItem(new ItemBuilder(Material.PLAYER_HEAD).name("&f" + owner.getNickname() + "'s Head").skullOwner(owner).build());
	}

	@Path("getId")
	@Permission(Group.ADMIN)
	void getId() {
		final ItemStack tool = getTool();
		final Block block = getTargetBlock();

		String id = null;
		if (!isNullOrAir(tool))
			id = Nexus.getHeadAPI().getItemID(tool);
		else if (!BlockUtils.isNullOrAir(block))
			id = Nexus.getHeadAPI().getBlockID(block);
		else
			error("You must be holding or looking at a head");

		ItemStack item = Nexus.getHeadAPI().getItemHead(id);
		send(json(PREFIX + item.getItemMeta().getDisplayName() + " &3head ID: &e" + id).copy(id).hover("&eClick to copy"));
	}

}
