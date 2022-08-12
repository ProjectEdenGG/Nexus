package gg.projecteden.nexus.features.profiles;

import gg.projecteden.nexus.features.profiles.providers.FriendsProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Aliases("friend")
@Permission(Group.STAFF)
public class FriendsCommand extends CustomCommand {
	public static String PREFIX = StringUtils.getPrefix("Friends");
	private static final FriendsUserService userService = new FriendsUserService();
	private FriendsUser user;

	public FriendsCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path
	void menu() {
		new FriendsProvider().open(player());
	}

	@Path("list")
	void list() {
		menu();
	}

	@Path("add <player>")
	void add(FriendsUser _user) {
		if (isSelf(_user))
			error("You cannot add yourself as a friend");

		if (user.isFriendsWith(_user))
			error("You're already friends " + _user.getNickname());

		user.sendRequest(_user);
	}

	@Path("remove <player>")
	void remove(FriendsUser _user) {
		if (isSelf(_user))
			error("You cannot remove yourself as a friend");

		if (!user.isFriendsWith(_user))
			error("You're not friends with " + _user.getNickname());

		user.removeFriend(_user);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("accept <player>")
	void acceptRequest(Player player) {
		user.addFriend(userService.get(player));
	}

	@HideFromHelp
	@Path("deny <player>")
	void denyRequest(Player player) {
		user.denyRequest(userService.get(player));
	}

}
