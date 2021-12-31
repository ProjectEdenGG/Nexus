package gg.projecteden.nexus.features.commands.staff;

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
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NonNull;
import org.bukkit.Material;

import static gg.projecteden.nexus.features.commands.staff.PlayerHeadCommand.PERMISSION;

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

}
