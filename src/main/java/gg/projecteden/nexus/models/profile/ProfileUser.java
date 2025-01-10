package gg.projecteden.nexus.models.profile;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.UUID;

@Data
@Entity(value = "profile_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class ProfileUser implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private ChatColor backgroundColor = ChatColor.WHITE;

	public Color getBukkitBackgroundColor() {
		return ColorType.toBukkitColor(this.backgroundColor);
	}

}
