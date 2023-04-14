package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.RangeArgumentValidator.Range;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@WikiConfig(rank = "Operator", feature = "Misc")
public class GiveCommand extends CustomCommand {

	public GiveCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Give a player an item")
	void run(
		Player player,
		ItemStack item,
		@Optional @Range(min = 1, max = 2304, bypass = Group.STAFF) Integer amount,
		@Optional @Permission(Group.STAFF) String nbt
	) {
		if (!isSeniorStaff())
			if (worldGroup() != WorldGroup.CREATIVE)
				permissionError();
			else if (!isSelf(player))
				error("You cannot give items to other players, only yourself");

		item.setAmount(amount == null ? item.getType().getMaxStackSize() : amount);
		PlayerUtils.giveItem(player, item, nbt);
	}

}
