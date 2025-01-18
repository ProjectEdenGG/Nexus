package gg.projecteden.nexus.features.profiles;

import gg.projecteden.nexus.features.profiles.providers.FriendsProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUser.PrivacySettingType;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Aliases("friend")
public class FriendsCommand extends CustomCommand {
	public static String PREFIX = StringUtils.getPrefix("Friends");
	private static final FriendsUserService userService = new FriendsUserService();
	private FriendsUser user;
	private static final ProfileUserService profileUserService = new ProfileUserService();

	public FriendsCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path
	@HideFromWiki
	@HideFromHelp
	void menu() {
		new FriendsProvider(player(), player(), null).open(player());
	}

	@Path("list")
	@Description("List your friends")
	void list() {
		menu();
	}

	@Path("of <player>")
	@Description("View a player's friends list")
	void of(FriendsUser target) {
		ProfileUser targetProfile = profileUserService.get(target);
		if (targetProfile.canNotView(PrivacySettingType.FRIENDS, player()))
			error(target.getNickname() + "'s privacy settings prevent you from accessing this");

		new FriendsProvider(target.getOfflinePlayer(), player(), null).open(player());
	}

	@Path("(add|invite) <player>")
	@Description("Send a player a friend request")
	void add(FriendsUser target) {
		if (isSelf(target))
			error("You cannot add yourself as a friend");

		if (user.isFriendsWith(target))
			error("You're already friends with " + target.getNickname());

		user.sendRequest(target);
	}

	@Path("remove <player>")
	@Description("Remove a player from your friends list")
	void remove(FriendsUser target) {
		if (isSelf(target))
			error("You cannot remove yourself as a friend");

		if (!user.isFriendsWith(target))
			error("You are not friends with " + target.getNickname());

		user.removeFriend(target);
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("accept <player>")
	void acceptRequest(Player player) {
		FriendsUser target = userService.get(player);
		if (!user.receivedContains(target))
			error("You do not have a pending friend request from " + target.getNickname());

		user.addFriend(target);
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("deny <player>")
	void denyRequest(Player player) {
		FriendsUser target = userService.get(player);
		if (!user.receivedContains(target))
			error("You do not have a pending friend request from " + target.getNickname());

		user.denyRequest(target);
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("cancel <player>")
	void cancelRequest(Player player) {
		FriendsUser target = userService.get(player);
		if (!user.getRequests_sent().contains(target.getUuid()))
			error("You do not have a pending friend request to " + target.getNickname());

		user.cancelSent(target);
	}

	// TODO: notify player of missed friend requests while offline, if any received is false

}
