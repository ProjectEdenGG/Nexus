package gg.projecteden.nexus.features.crates.gemcrafter;

import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem.TomeLevel;
import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem.TomeType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission(Group.ADMIN)
public class TomeCommand extends CustomCommand {

	public TomeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<type> <level> [player]")
	@Description("Give the specified player a tome of type/level")
	void give(TomeType type, TomeLevel level, @Arg("self") Player player) {
		PlayerUtils.giveItem(player, new TomeItem(type, level).toItemStack());
	}

}
