package gg.projecteden.nexus.models.playerplushie;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose.Animated;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Tier;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.utils.ImageUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
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
	public static final Map<Integer, Pair<Pose, UUID>> ALL_SUBSCRIPTIONS = new ConcurrentHashMap<>();
	public static final Map<Integer, Pair<Pose, UUID>> ACTIVE_SUBSCRIPTIONS = new ConcurrentHashMap<>();

	public void addSubscription(Pose pose, UUID uuid) {
		subscriptions.computeIfAbsent(pose, $ -> new ArrayList<>()).add(uuid);
	}

	public static PlayerPlushieConfig get() {
		return new PlayerPlushieConfigService().get0();
	}

	// NEVER REMOVE FROM LIST, ONLY ADD
	private static List<Dev> OWNERS = List.of(Dev.GRIFFIN);
	private static List<Dev> ADMINS = List.of(Dev.WAKKA, Dev.BLAST, Dev.LEXI, Dev.ARBY, Dev.FILID);

	public Map<Pose, List<UUID>> getSubscriptions() {
		final ConcurrentHashMap<Pose, List<UUID>> subscriptions = new ConcurrentHashMap<>(this.subscriptions);

		OWNERS.forEach(owner -> {
			subscriptions.computeIfAbsent(Pose.FUNKO_POP_OWNER, $ -> new ArrayList<>()).add(owner.getUuid());

			for (Pose pose : Pose.values())
				if (pose.getTier() != Tier.SERVER)
					subscriptions.computeIfAbsent(pose, $ -> new ArrayList<>()).add(owner.getUuid());
		});

		ADMINS.forEach(admin -> {
			subscriptions.computeIfAbsent(Pose.FUNKO_POP_ADMIN, $ -> new ArrayList<>()).add(admin.getUuid());

			for (Pose pose : Pose.values())
				if (pose.getTier() != Tier.SERVER)
					subscriptions.computeIfAbsent(pose, $ -> new ArrayList<>()).add(admin.getUuid());
		});

		return subscriptions;
	}

	public static int randomActive() {
		if (ACTIVE_SUBSCRIPTIONS.isEmpty())
			generate();

		return randomElement(ACTIVE_SUBSCRIPTIONS.keySet());
	}

	public static final Material MATERIAL = Material.LAPIS_LAZULI;
	private static final String SUBDIRECTORY = "projecteden/decoration/plushies/player";
	private static final String TEXTURES_SUBDIRECTORY = SUBDIRECTORY + "/players";
	private static final String MODELS_DIRECTORY = "assets/minecraft/models/" + SUBDIRECTORY;
	private static final String TEXTURES_DIRECTORY = "assets/minecraft/textures/" + TEXTURES_SUBDIRECTORY;

	private static final Map<String, Object> GLOBAL_VARIABLES = Map.of(
		"SUBDIRECTORY", SUBDIRECTORY,
		"TEXTURES_SUBDIRECTORY", TEXTURES_SUBDIRECTORY
	);

	private static String process(String templateString, Map<String, Object> variables) {
		final AtomicReference<String> template = new AtomicReference<>(templateString);

		BiConsumer<String, Object> consumer = (id, value) ->
			template.set(template.get().replaceAll("<" + id + ">", "" + value));

		variables.forEach(consumer);
		GLOBAL_VARIABLES.forEach(consumer);

		return template.get();
	}

	public static final String ITEM_TEMPLATE = """
		{
			"parent": "<SUBDIRECTORY>/<POSE>/template_<SKIN_TYPE>",
			"textures": {
				"0": "<TEXTURES_SUBDIRECTORY>/<UUID>"
			}
		}
	""";

	public static final String ANIMATED_ITEM_TEMPLATE = """
		{
			"parent": "<SUBDIRECTORY>/<POSE>/template_<SKIN_TYPE>",
			"textures": {
				"0": "<TEXTURES_SUBDIRECTORY>/<UUID>",
				"1": "<TEXTURES_SUBDIRECTORY>/<POSE>/<UUID>"
			}
		}
	""";

	public static final String ANIMATION_TEMPLATE = """
		{
			"animation": {
				"frametime": <FRAME_TIME>,
				"frames": <FRAMES>
			}
		}
		""";

	public static final String MATERIAL_TEMPLATE = """
		{
			"parent": "minecraft:item/generated",
			"textures": {
				"layer0": "minecraft:item/%s"
			},
			"overrides": [
				<OVERRIDES>
			]
		}
	""".formatted(MATERIAL.name().toLowerCase());

	public static final String PREDICATE_TEMPLATE = """
		{"predicate": {"custom_model_data": <ID>}, "model": "<SUBDIRECTORY>/<POSE>/<UUID>"}
	""";

	public static final String MISSING_TEXTURE_PREDICATE_TEMPLATE = """
		{"predicate": {"custom_model_data": <ID>}, "model": "minecraft:item/barrier"}
	""";

	@SneakyThrows
	public static Map<String, Object> generate() {
		ALL_SUBSCRIPTIONS.clear();
		return new HashMap<>() {{
			final var subscriptions = new HashMap<>(PlayerPlushieConfig.get().getSubscriptions());

			subscriptions.forEach((pose, uuids) -> {
				int index = pose.getStartingIndex();
				for (UUID uuid : uuids) {
					try {
						++index;

						final String poseName = pose.name().toLowerCase();

						put(TEXTURES_DIRECTORY + "/%s.png".formatted(uuid), SkinCache.of(uuid).retrieveImage());
						put(MODELS_DIRECTORY + "/%s/%s.json".formatted(poseName, uuid), process(pose.isAnimated() ? ANIMATED_ITEM_TEMPLATE : ITEM_TEMPLATE, Map.of(
							"POSE", poseName,
							"SKIN_TYPE", SkinCache.of(uuid).getModel().name().toLowerCase(),
							"UUID", uuid)
						));

						if (pose.isAnimated()) {
							final Animated config = pose.getAnimationConfig();
							final String animatedTextureFile = "%s/%s/%s.png".formatted(TEXTURES_DIRECTORY, poseName, uuid);
							put(animatedTextureFile, generateAnimationTexture(uuid, config.frameCount()));
							put(animatedTextureFile + ".mcmeta", process(ANIMATION_TEMPLATE, Map.of(
								"FRAME_TIME", config.frameTime(),
								"FRAMES", Arrays.stream(config.frames()).mapToObj(String::valueOf).toList())
							));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					if (new PlayerPlushieUserService().get(uuid).isSubscribedAt(pose)) {
						ALL_SUBSCRIPTIONS.put(index, new Pair<>(pose, uuid));
						ACTIVE_SUBSCRIPTIONS.put(index, new Pair<>(pose, uuid));
					} else
						ALL_SUBSCRIPTIONS.put(index, new Pair<>(null, uuid));
				}
			});

			final String overrides = process(MATERIAL_TEMPLATE, Map.of("OVERRIDES", Utils.sortByKey(ALL_SUBSCRIPTIONS).entrySet().stream()
				.map(entry -> {
					if (entry.getValue().getFirst() == null)
						return process(MISSING_TEXTURE_PREDICATE_TEMPLATE, Map.of("ID", entry.getKey()));

					return process(PREDICATE_TEMPLATE, Map.of(
						"ID", entry.getKey(),
						"POSE", entry.getValue().getFirst().name().toLowerCase(),
						"UUID", entry.getValue().getSecond().toString()
					));
				})
				.collect(Collectors.joining(","))));

			put("%s/%s.json".formatted(CustomModel.getVanillaSubdirectory(), MATERIAL.name().toLowerCase()), overrides);
		}};
	}

	@NotNull
	private static BufferedImage generateAnimationTexture(UUID uuid, int frames) {
		final BufferedImage skin = SkinCache.of(uuid).retrieveImage();
		final BufferedImage texture = ImageUtils.newImage(frames * 64, frames * frames * 64);
		final Graphics graphics = texture.getGraphics();
		for (int frame = 0; frame < frames; frame++)
			graphics.drawImage(skin, frame * 64, frame * ((frames + 1) * 64), null);
		graphics.dispose();
		return texture;
	}

}
