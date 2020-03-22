package me.pugabyte.bncore.models.staffwarp;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import org.bukkit.Location;

import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "staff_warp")
public class StaffWarp {
	private String name;
	@DbSerializer(LocationSerializer.class)
	private Location location;
}

