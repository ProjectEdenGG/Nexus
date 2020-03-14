package me.pugabyte.bncore.models.poof;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import org.bukkit.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Poof {

	String sender;
	String receiver;
	@DbSerializer(LocationSerializer.class)
	Location senderLocation;
	@DbSerializer(LocationSerializer.class)
	Location receiverLocation;
	LocalDateTime timeSent;

	public Poof(String sender, String receiver, Location senderLocation, Location receiverLocation, LocalDateTime timeSent) {
		this.sender = sender;
		this.receiver = receiver;
		this.senderLocation = senderLocation;
		this.receiverLocation = receiverLocation;
		this.timeSent = timeSent;
	}

}
