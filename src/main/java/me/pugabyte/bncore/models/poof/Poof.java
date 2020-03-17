package me.pugabyte.bncore.models.poof;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import org.bukkit.Location;

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
	LocalDateTime timeSent;
	boolean expired = false;
}
