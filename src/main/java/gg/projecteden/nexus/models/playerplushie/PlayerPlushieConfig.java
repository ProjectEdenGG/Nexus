package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.RandomUtils.randomElement;

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
	public static final Map<Integer, Pair<Pose, UUID>> ACTIVE_SUBSCRIPTIONS = new ConcurrentHashMap<>();

	public void addSubscription(Pose pose, UUID uuid) {
		subscriptions.computeIfAbsent(pose, $ -> new ArrayList<>()).add(uuid);
	}

	public static PlayerPlushieConfig get() {
		return new PlayerPlushieConfigService().get0();
	}

	public static int randomActive() {
		if (ACTIVE_SUBSCRIPTIONS.isEmpty())
			generate();

		return randomElement(ACTIVE_SUBSCRIPTIONS.keySet());
	}

	public static final Material MATERIAL = Material.LAPIS_LAZULI;
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
		ACTIVE_SUBSCRIPTIONS.clear();
		return new HashMap<>() {{
			final var subscriptions = new HashMap<>(PlayerPlushieConfig.get().getSubscriptions());

			List.of(Dev.GRIFFIN, Dev.WAKKA).forEach(dev -> {
				for (Pose pose : Pose.values())
					subscriptions.computeIfAbsent(pose, $ -> new ArrayList<>()).add(dev.getUuid());
			});

			subscriptions.forEach((pose, uuids) -> {
				int index = pose.getStartingIndex();
				for (UUID uuid : uuids) {
					++index;

					if (!new PlayerPlushieUserService().get(uuid).isSubscribedAt(pose.getTier()))
						return;

					final String poseName = pose.name().toLowerCase();
					final String file = "/%s/%s.json".formatted(poseName, uuid);
					final String template = ITEM_TEMPLATE.formatted(poseName, SkinCache.of(uuid).getModel(), "players", uuid);
					put(DIRECTORY + file, template);

					ACTIVE_SUBSCRIPTIONS.put(index, new Pair<>(pose, uuid));
				}
			});

			final String overrides = MATERIAL_TEMPLATE.formatted(Utils.sortByKey(ACTIVE_SUBSCRIPTIONS).entrySet().stream()
				.map(entry -> PREDICATE_TEMPLATE.formatted(entry.getKey(), entry.getValue().getFirst().name().toLowerCase(), entry.getValue().getSecond().toString()))
				.collect(Collectors.joining(",")));

			put("%s/%s.json".formatted(CustomModel.getVanillaSubdirectory(), MATERIAL.name().toLowerCase()), overrides);
		}};
	}

}
