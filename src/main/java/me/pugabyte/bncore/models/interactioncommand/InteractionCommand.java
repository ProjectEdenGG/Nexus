package me.pugabyte.bncore.models.interactioncommand;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import org.bukkit.Location;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interaction_command")
public class InteractionCommand {
	@Id
	@DbSerializer(LocationSerializer.class)
	private Location location;
	private String command;
}
