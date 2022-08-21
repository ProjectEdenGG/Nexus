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
		new FriendsProvider(player(), player(), null).open(player());
	}

	@Path("list")
	void list() {
		menu();
	}

	@Path("of <player>")
	@Permission(Group.STAFF)
	void of(FriendsUser target) {
		new FriendsProvider(target.getOfflinePlayer(), player(), null).open(player());
	}

	@Path("(add|invite) <player>")
	void add(FriendsUser target) {
		if (isSelf(target))
			error("You cannot add yourself as a friend");

		if (user.isFriendsWith(target))
			error("You're already friends with " + target.getNickname());

		user.sendRequest(target);
	}

	@Path("remove <player>")
	void remove(FriendsUser target) {
		if (isSelf(target))
			error("You cannot remove yourself as a friend");

		if (!user.isFriendsWith(target))
			error("You're not friends with " + target.getNickname());

		user.removeFriend(target);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("accept <player>")
	void acceptRequest(Player player) {
		FriendsUser target = userService.get(player);
		if (!user.receivedContains(target))
			error("You do not have an active friend request from " + target.getNickname());

		user.addFriend(target);
	}

	@HideFromHelp
	@Path("deny <player>")
	void denyRequest(Player player) {
		FriendsUser target = userService.get(player);
		if (!user.receivedContains(target))
			error("You do not have an active friend request from " + target.getNickname());

		user.denyRequest(target);
	}

	@HideFromHelp
	@Path("cancel <player>")
	void cancelRequest(Player player) {
		FriendsUser target = userService.get(player);
		if (!user.getRequests_sent().contains(target.getUuid()))
			error("You do not have an active sent friend request to " + target.getNickname());

		user.cancelSent(target);
	}

	// TODO: notify player of missed friend requests while offline, if any received is false

}
