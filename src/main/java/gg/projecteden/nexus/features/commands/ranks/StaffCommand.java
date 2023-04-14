package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Aliases("stafflist")
public class StaffCommand extends CustomCommand {

	public StaffCommand(CommandEvent event) {
		super(event);
	}

	@Async
	@Override
	@NoLiterals
	@Description("View all current staff members")
	public void help() {
		line();

		AtomicInteger total = new AtomicInteger();
		Rank.getStaffNerds().thenAccept(ranks -> {
			try {
				List<String> messages = new ArrayList<>();

				ranks.forEach((rank, nerds) -> {
					total.addAndGet(nerds.size());
					messages.add(rank.getColoredName() + " &f(" + nerds.size() + "):&e " + nerds.stream()
						.map(Nickname::of)
						.sorted(String.CASE_INSENSITIVE_ORDER)
						.collect(Collectors.joining("&f, &e")));
				});

				send(PREFIX + "Total: &e" + total);
				line();
				messages.forEach(this::send);
				line();
				send(json("&3View online staff with &c/onlinestaff").command("/onlinestaff"));
				send(json("&3If you need to request a staff member's &ehelp&3, please use &c/ticket &c<message>").suggest("/ticket "));
				line();
			} catch (Exception ex) {
				rethrow(ex);
			}
		});
	}

}
