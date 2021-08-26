package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Aliases("stafflist")
public class StaffCommand extends CustomCommand {

	public StaffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Async
	void staff() {
		line();

		AtomicInteger total = new AtomicInteger();
		Map<Rank, List<Nerd>> map = new LinkedHashMap<>() {{
			Rank.STAFF_RANKS.forEach(rank -> {
				put(rank, rank.getNerds().stream().filter(Dev.KODA::isNot).toList());
				total.addAndGet(get(rank).size());
			});
		}};

		send(PREFIX + "Total: &e" + total);
		line();
		map.forEach((rank, nerds) ->
			send(rank.getColoredName() + " &f(" + nerds.size() + "):&e " + nerds.stream()
				.map(Nickname::of)
				.sorted()
				.collect(Collectors.joining("&f, &e"))));
		line();
		send(json("&3View online staff with &c/onlinestaff").command("/onlinestaff"));
		send(json("&3If you need to request a staff member's &ehelp&3, please use &c/ticket &c<message>").suggest("/ticket "));
		line();
	}

}
