package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Aliases("nick")
@Permission("nickname.use")
public class NicknameCommand extends CustomCommand {
	private final NerdService service = new NerdService();

	public NicknameCommand(CommandEvent event) {
		super(event);
	}

	@Path("<nickname...>")
	void run(String nickname) {
		Nerd nerd = Nerd.of(player());
		nerd.setNickname(stripColor(nickname));
		service.save(nerd);
		send(PREFIX + "Nickname set to &e" + nerd.getNickname());
	}

	@Permission("group.staff")
	@Path("set <player> <nickname...>")
	void set(Nerd nerd, String nickname) {
		nerd.setNickname(stripColor(nickname));
		service.save(nerd);
		send(PREFIX + nerd.getName() + "'s nickname set to &e" + nerd.getNickname());
	}

	@Path("reset [player]")
	void reset(@Arg(value = "self", permission = "group.staff") Nerd nerd) {
		if (!nerd.hasNickname())
			error((isSelf(nerd) ? "You do not" : nerd.getName() + " doesn't") + " have a nickname");
		nerd.setNickname(null);
		send(PREFIX + (isSelf(nerd) ? "Nickname" : nerd.getName() + "'s nickname") + " reset");
	}

}
