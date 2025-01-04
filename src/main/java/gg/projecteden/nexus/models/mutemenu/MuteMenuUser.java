package gg.projecteden.nexus.models.mutemenu;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PreLoad;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.*;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Data
@Entity(value = "mute_menu_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class MuteMenuUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<MuteMenuItem> muted = new HashSet<>();
	private Map<MuteMenuItem, Integer> volumes = new ConcurrentHashMap<>();
	private Map<EntityType, Integer> entityVolumes = new ConcurrentHashMap<>();
	private Map<SoundGroup, Integer> soundGroupVolumes = new ConcurrentHashMap<>();

	public boolean hasMuted(MuteMenuItem item) {
		if (volumes.containsKey(item))
			return getVolume(item) == 0.0;
		else if (item.getDefaultVolume() != null && item.getDefaultVolume() == 0)
			return true;

		if (item.name().startsWith("CHANNEL_"))
			return !new ChatterService().get(uuid).hasJoined(StaticChannel.valueOf(item.name().replace("CHANNEL_", "")).getChannel());
		else
			return muted.contains(item);
	}

	@PreLoad
	void fixPreLoad(DBObject dbObject) {
		DBObject map = (DBObject) dbObject.get("entityVolumes");
		if (map != null && map.containsField("SNOWMAN"))
			map.put(EntityType.SNOW_GOLEM.name(), map.removeField("SNOWMAN"));
	}

	public void setVolume(MuteMenuItem item, int volume) {
		volumes.put(item, volume);
	}

	public int getVolume(MuteMenuItem item) {
		return volumes.getOrDefault(item, item.getDefaultVolume());
	}

	public void setVolume(EntityType entity, int volume) {
		entityVolumes.put(entity, volume);
	}

	public int getVolume(EntityType entity) {
		return entityVolumes.getOrDefault(entity, 100);
	}

	public void setVolume(SoundGroup soundGroup, int volume) {
		soundGroupVolumes.put(soundGroup, volume);
	}

	public int getVolume(SoundGroup soundGroup) {
		return soundGroupVolumes.getOrDefault(soundGroup, 100);
	}

	public void unmute(MuteMenuItem item) {
		muted.remove(item);
	}

	public void mute(MuteMenuItem item) {
		muted.add(item);
	}

	public static boolean hasMuted(HasUniqueId player, MuteMenuItem item) {
		if (item == null) return false;
		MuteMenuService service = new MuteMenuService();
		MuteMenuUser user = service.get(player);
		return user.hasMuted(item);
	}

	public static Integer getVolume(HasUniqueId player, MuteMenuItem item) {
		if (item == null) return null;
		MuteMenuService service = new MuteMenuService();
		MuteMenuUser user = service.get(player);
		return user.getVolume(item);
	}

	@Getter
	@AllArgsConstructor
	public enum SoundGroup {
		PORTALS(Material.END_PORTAL_FRAME, sound -> sound.name().asString().contains("portal")),
		;

		private final Material material;
		private final int modelId;
		private final Predicate<Sound> predicate;

		SoundGroup(Material material, Predicate<Sound> predicate) {
			this(material, 0, predicate);
		}

		SoundGroup(CustomMaterial material, Predicate<Sound> predicate) {
			this.material = material.getMaterial();
			this.modelId = material.getModelId();
			this.predicate = predicate;
		}

		public static SoundGroup of(Sound sound) {
			for (SoundGroup group : values())
				if (group.predicate.test(sound))
					return group;

			return null;
		}

		public ItemStack getItem() {
			return new ItemBuilder(material).modelId(modelId).name(StringUtils.camelCase(this)).build();
		}
	}

}
