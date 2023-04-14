package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;

@Aliases("fw127")
@Permission(Group.ADMIN)
public class Firework127Command extends CustomCommand {

	public Firework127Command(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Receive a firework rocket with power 127")
	void run() {
		PlayerUtils.giveItem(player(), new ItemBuilder(Material.FIREWORK_ROCKET).fireworkPower(127).build());
	}

}
