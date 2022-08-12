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

import java.util.ArrayList;
import java.util.List;
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
	List<UUID> friends = new ArrayList<>();
	List<UUID> requests_sent = new ArrayList<>();
	List<UUID> requests_received = new ArrayList<>();

	private void save() {
		new FriendsUserService().save(this);
	}

	public boolean isFriendsWith(FriendsUser user) {
		return friends.contains(user.getUniqueId());
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
		if (requests_received.contains(toUser.getUuid())) {
			addFriend(toUser);
			return;
		}

		requests_sent.add(toUser.getUuid());
		toUser.requests_received.add(this.uuid);

		if (toUser.isOnline())
			toUser.notifyRequest(this);

		sendMessage(FriendsCommand.PREFIX + "Sent a friend request to &e" + toUser.getNickname());

		toUser.save();
		save();
	}

	public void denyRequest(FriendsUser fromUser) {
		clearRequests(fromUser);

		notifyRequestDenied(fromUser);
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

	public void notifyRequestDenied(FriendsUser fromUser) {
		sendMessage(FriendsCommand.PREFIX + "&e" + fromUser.getNickname() + " &3Denied your friend request");
	}

	public void notifyAdd(FriendsUser fromUser) {
		sendMessage(FriendsCommand.PREFIX + "&3You and &e" + fromUser.getNickname() + " &3are now friends");
	}

	public void notifyRemove(FriendsUser fromUser) {
		sendMessage(FriendsCommand.PREFIX + "&3You and &e" + fromUser.getNickname() + " &3are no longer friends");
	}
}
