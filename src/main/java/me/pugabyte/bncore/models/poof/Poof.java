package me.pugabyte.bncore.models.poof;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Poof {
	@NonNull
	String sender;
	@NonNull
	String receiver;
	@NonNull
	@DbSerializer(LocationSerializer.class)
	Location senderLocation;
	@NonNull
	@DbSerializer(LocationSerializer.class)
	Location receiverLocation;
	@NonNull
	LocalDateTime timeSent = LocalDateTime.now();
	boolean expired = false;

	public Poof(@NonNull Player sender, @NonNull Player receiver) {
		this.sender = sender.getUniqueId().toString();
		this.receiver = receiver.getUniqueId().toString();
		this.senderLocation = sender.getLocation();
		this.receiverLocation = receiver.getLocation();
	}

	public OfflinePlayer getSenderPlayer() {
		return Utils.getPlayer(sender);
	}

	public OfflinePlayer getReceiverPlayer() {
		return Utils.getPlayer(receiver);
	}

}
