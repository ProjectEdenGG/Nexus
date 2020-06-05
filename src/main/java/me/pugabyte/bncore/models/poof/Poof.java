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
	private String sender;
	@NonNull
	private String receiver;
	@NonNull
	@DbSerializer(LocationSerializer.class)
	private Location teleportLocation;
	@NonNull
	private PoofType type;
	@NonNull
	private LocalDateTime timeSent = LocalDateTime.now();
	private boolean expired = false;


	public Poof(@NonNull Player sender, @NonNull Player receiver, PoofType type) {
		this.sender = sender.getUniqueId().toString();
		this.receiver = receiver.getUniqueId().toString();
		if (type == PoofType.POOF)
			this.teleportLocation = receiver.getLocation();
		else
			this.teleportLocation = sender.getLocation();
		this.type = type;
	}

	public OfflinePlayer getSenderPlayer() {
		return Utils.getPlayer(sender);
	}

	public OfflinePlayer getReceiverPlayer() {
		return Utils.getPlayer(receiver);
	}

	public enum PoofType {
		POOF,
		POOF_HERE
	}

}
