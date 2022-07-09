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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final String MODELS_DIRECTORY = "assets/minecraft/models/" + SUBDIRECTORY;
	private static final String TEXTURES_DIRECTORY = "assets/minecraft/textures/" + SUBDIRECTORY + "/players";

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

	public static final String ANIMATION_TEMPLATE = """
		{
			"animation": {
				"frametime": %d,
				"frames": %s
			}
		}
		""";

	public static final String PREDICATE_TEMPLATE = """
		{"predicate": {"custom_model_data": %%d}, "model": "%s/%%s/%%s"}
	""".formatted(SUBDIRECTORY);

	public static final String MISSING_TEXTURE_PREDICATE_TEMPLATE = """
		{"predicate": {"custom_model_data": %d}, "model": "minecraft:item/barrier"}
	""";

	// NEVER REMOVE FROM LIST, ONLY ADD
	private static List<Dev> OWNERS = List.of(Dev.GRIFFIN);
	private static List<Dev> ADMINS = List.of(Dev.WAKKA, Dev.BLAST, Dev.LEXI, Dev.ARBY, Dev.FILID);

	@SneakyThrows
	public static Map<String, Object> generate() {
		ACTIVE_SUBSCRIPTIONS.clear();
		return new HashMap<>() {{
			final var subscriptions = new HashMap<>(PlayerPlushieConfig.get().getSubscriptions());

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

			subscriptions.forEach((pose, uuids) -> {
				int index = pose.getStartingIndex();
				for (UUID uuid : uuids) {
					++index;

					final String poseName = pose.name().toLowerCase();
					final String modelFile = "/%s/%s.json".formatted(poseName, uuid);
					final String textureUuid = (pose.isAnimated() ? poseName + "/" : "") + uuid;
					final String textureFile = "/%s.png".formatted(textureUuid);
					final String textureMetaFile = textureFile + ".mcmeta";
					final String template = ITEM_TEMPLATE.formatted(poseName, SkinCache.of(uuid).getModel(), "players", textureUuid);

					put(MODELS_DIRECTORY + modelFile, template);
					if (!pose.isAnimated())
						put(TEXTURES_DIRECTORY + textureFile, SkinCache.of(uuid).retrieveImage());
					else {
						try {
							final Animated config = pose.getAnimationConfig();
							final int frames = config.frameCount();

							final BufferedImage skin = SkinCache.of(uuid).retrieveImage();
							final BufferedImage texture = ImageUtils.newImage(frames * 64, frames * frames * 64);
							final Graphics graphics = texture.getGraphics();
							for (int frame = 0; frame < frames; frame++)
								graphics.drawImage(skin, frame * 64, frame * ((frames + 1) * 64), null);
							graphics.dispose();
							put(TEXTURES_DIRECTORY + textureFile, texture);
							put(TEXTURES_DIRECTORY + textureMetaFile, ANIMATION_TEMPLATE.formatted(config.frameTime(), Arrays.stream(config.frames()).mapToObj(String::valueOf).toList()));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					if (new PlayerPlushieUserService().get(uuid).isSubscribedAt(pose))
						ACTIVE_SUBSCRIPTIONS.put(index, new Pair<>(pose, uuid));
					else
						ACTIVE_SUBSCRIPTIONS.put(index, null);
				}
			});

			final String overrides = MATERIAL_TEMPLATE.formatted(Utils.sortByKey(ACTIVE_SUBSCRIPTIONS).entrySet().stream()
				.map(entry -> {
					if (entry.getValue() == null)
						return MISSING_TEXTURE_PREDICATE_TEMPLATE.formatted(entry.getKey());
					return PREDICATE_TEMPLATE.formatted(entry.getKey(), entry.getValue().getFirst().name().toLowerCase(), entry.getValue().getSecond().toString());
				})
				.collect(Collectors.joining(",")));

			put("%s/%s.json".formatted(CustomModel.getVanillaSubdirectory(), MATERIAL.name().toLowerCase()), overrides);
		}};
	}

}
