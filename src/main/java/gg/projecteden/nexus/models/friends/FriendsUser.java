package gg.projecteden.nexus.models.friends;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.profiles.FriendsCommand;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "friends_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class FriendsUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<UUID> friends = new HashSet<>();
	private Set<UUID> requests_sent = new HashSet<>();
	private Map<UUID, Boolean> requests_received = new HashMap<>();

	private void save() {
		new FriendsUserService().save(this);
	}

	public boolean isFriendsWith(FriendsUser user) {
		return friends.contains(user.getUniqueId());
	}

	public List<UUID> getUnreadReceived() {
		return requests_received.keySet().stream().filter(uuid -> !requests_received.get(uuid)).toList();
	}

	public void clearUnread() {
		requests_received.keySet().stream().filter(uuid -> !requests_received.get(uuid)).forEach(uuid -> requests_received.put(uuid, true));
		save();
	}

	public boolean receivedContains(FriendsUser user) {
		return requests_received.containsKey(user.getUuid());
	}

	public void receivedAdd(FriendsUser user) {
		this.requests_received.put(user.getUuid(), false);
	}

	public void sentAdd(FriendsUser user) {
		this.requests_sent.add(user.getUuid());
	}

	public void addFriend(FriendsUser user) {
		clearRequests(user);

		friends.add(user.getUuid());
		user.getFriends().add(this.uuid);

		notifyAdd(user);
		user.notifyAdd(this);

		user.save();
		save();
	}

	public void removeFriend(FriendsUser user) {
		friends.remove(user.getUuid());
		user.getFriends().remove(this.uuid);

		notifyRemove(user);
		user.notifyRemove(this);

		user.save();
		save();
	}

	public void sendRequest(FriendsUser toUser) {
		if (this.receivedContains(toUser)) {
			addFriend(toUser);
			return;
		}

		if (toUser.getRequests_received().containsKey(uuid)) {
			sendMessage("&cYou've already sent a request to " + toUser.getNickname());
			return;
		}

		this.sentAdd(toUser);
		toUser.receivedAdd(this);

		if (toUser.isOnline())
			toUser.notifyRequest(this);

		sendMessage(
			json(FriendsCommand.PREFIX)
				.next("&3Sent a friend request to &e" + toUser.getNickname()).group()
				.next("&3 &3 || &3 ").group()
				.next("&c&lCancel?").hover("&eClick &3to cancel").command("/friends cancel " + toUser.getNickname()).group()
		);

		toUser.save();
		save();
	}

	public void denyRequest(FriendsUser fromUser) {
		clearRequests(fromUser);

		sendMessage(FriendsCommand.PREFIX + "&3You denied &e" + fromUser.getNickname() + "'s &3friend request");
		fromUser.sendMessage(FriendsCommand.PREFIX + "&e" + this.getNickname() + " &3denied your friend request");
	}

	public void cancelSent(FriendsUser toUser) {
		clearRequests(toUser);

		sendMessage(FriendsCommand.PREFIX + "&3You cancelled your friend request to &e" + toUser.getNickname());
		toUser.sendMessage(FriendsCommand.PREFIX + "&e" + this.getNickname() + " &3cancelled their friend request");
	}

	public void clearRequests(FriendsUser fromUser) {
		requests_received.remove(fromUser.getUuid());
		requests_sent.remove(fromUser.getUuid());

		fromUser.requests_sent.remove(this.getUuid());
		fromUser.requests_received.remove(this.getUuid());

		fromUser.save();
		save();
	}

	public void notifyRequest(FriendsUser fromUser) {
		sendMessage(
			json(FriendsCommand.PREFIX)
				.next("&e" + fromUser.getNickname() + " &3has sent you a friend request ").group()
				.next("&a&lAccept").hover("&eClick &3to accept").command("/friends accept " + fromUser.getNickname()).group()
				.next("&3 &3 || &3 ").group()
				.next("&c&lDeny").hover("&eClick &3to deny").command("/friends deny " + fromUser.getNickname()).group()
		);
	}

	public void notifyAdd(FriendsUser fromUser) {
		sendMessage(FriendsCommand.PREFIX + "&3You and &e" + fromUser.getNickname() + " &3are now friends");
	}

	public void notifyRemove(FriendsUser fromUser) {
		sendMessage(FriendsCommand.PREFIX + "&3You and &e" + fromUser.getNickname() + " &3are no longer friends");
	}
}
