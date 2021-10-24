package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.utils.Utils;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
@Entity(value = "player_plushie_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class PlayerPlushieConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Map<Pose, List<UUID>> subscriptions = new ConcurrentHashMap<>();

	public void addSubscription(Pose pose, UUID uuid) {
		subscriptions.computeIfAbsent(pose, $ -> new ArrayList<>()).add(uuid);
	}

	public static PlayerPlushieConfig get() {
		return new PlayerPlushieConfigService().get0();
	}

	private static final Material MATERIAL = Material.LAPIS_LAZULI;
	private static final String SUBDIRECTORY = "projecteden/decoration/plushies/player";
	private static final String DIRECTORY = "assets/minecraft/models/" + SUBDIRECTORY;

	public static final String ITEM_TEMPLATE = """
		{
			"parent": "%s/%%s/template_%%s",
			"textures": {
				"0": "%s/%%s/%%s"
			}
		}
	""".formatted(SUBDIRECTORY, SUBDIRECTORY);

	public static final String MATERIAL_TEMPLATE = """
		{
			"parent": "minecraft:item/generated",
			"textures": {
				"layer0": "minecraft:item/%s"
			},
			"overrides": [
				%%s
			]
		}
	""".formatted(MATERIAL.name().toLowerCase());

	public static final String PREDICATE_TEMPLATE = """
		{"predicate": {"custom_model_data": %%d}, "model": "%s/%%s/%%s"}
	""".formatted(SUBDIRECTORY);

	public static Map<String, String> generate() {
		return new HashMap<>() {{
			final Map<Integer, Pair<String, String>> models = new LinkedHashMap<>();

			PlayerPlushieConfig.get().getSubscriptions().forEach((pose, uuids) -> {
				int index = pose.getStartingIndex();
				for (UUID uuid : uuids) {
					++index;

					if (!new PlayerPlushieUserService().get(uuid).isSubscribedAt(pose.getTier()))
						return;

					final String poseName = pose.name().toLowerCase();
					final String file = "/%s/%s.json".formatted(poseName, uuid);
					final String template = ITEM_TEMPLATE.formatted(poseName, SkinCache.of(uuid).getModel(), poseName, uuid);
					put(DIRECTORY + file, template);

					models.put(index, new Pair<>(poseName, uuid.toString()));
				}
			});

			final String overrides = MATERIAL_TEMPLATE.formatted(Utils.sortByKey(models).entrySet().stream()
				.map(entry -> PREDICATE_TEMPLATE.formatted(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond()))
				.collect(Collectors.joining(",")));

			put("%s/%s.json".formatted(CustomModel.getVanillaSubdirectory(), MATERIAL.name().toLowerCase()), overrides);
		}};
	}


}
