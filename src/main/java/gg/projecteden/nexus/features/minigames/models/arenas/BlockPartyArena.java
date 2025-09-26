package gg.projecteden.nexus.features.minigames.models.arenas;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.ToString;
import org.bukkit.DyeColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.lights.LxBoard;
import tech.blastmc.lights.cue.CueBuilder;
import tech.blastmc.lights.cue.CueTimesBuilder;
import tech.blastmc.lights.cue.Permutation;
import tech.blastmc.lights.cue.Permutation.Color;
import tech.blastmc.lights.cue.Permutation.Effect;
import tech.blastmc.lights.cue.Permutation.Intensity;
import tech.blastmc.lights.cue.Permutation.Pitch;
import tech.blastmc.lights.cue.Permutation.StopEffect;
import tech.blastmc.lights.cue.Permutation.Yaw;
import tech.blastmc.lights.effect.EffectBuilder;
import tech.blastmc.lights.effect.EffectType;
import tech.blastmc.lights.effect.OffsetType;
import tech.blastmc.lights.map.ChannelList;
import tech.blastmc.lights.map.Group;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
@SerializableAs("BlockPartyArena")
public class BlockPartyArena extends Arena {

	private LxBoard lights = getDefaultBoard();

	public BlockPartyArena(@NotNull Map<String, Object> map) {
		super(map);

		this.lights = (LxBoard) map.getOrDefault("lights", lights);

		this.lights.getEffectRegistry().register(
			new EffectBuilder()
				.id(1)
				.effectType(EffectType.DIRECTION)
				.durationInSeconds(4)
				.offsetType(OffsetType.OFFSET_ORDERED)
				.sampler(tick -> {
					final int PERIOD_TICKS = 80;
					final int YAW_AMP_DEG = 60;
					final int PITCH_AMP_DEG = 25;
					final double PHASE_SHIFT = Math.PI / 2.0;

					int t = Math.floorMod(tick, PERIOD_TICKS);
					double theta = (2.0 * Math.PI * t) / PERIOD_TICKS;

					int dYaw   = (int) Math.round(YAW_AMP_DEG   * Math.sin(theta));
					int dPitch = (int) Math.round(PITCH_AMP_DEG * Math.sin(2.0 * theta + PHASE_SHIFT));

					return List.of(new Permutation.Yaw(dYaw), new Permutation.Pitch(dPitch));
				})
				.build()
		);

		this.lights.getEffectRegistry().register(
			new EffectBuilder()
				.id(2)
				.effectType(EffectType.COLOR)
				.offsetType(OffsetType.SYNCHRONIZED)
				.durationInSeconds(4)
				.sampler(tick -> {
					final int PERIOD_TICKS = 80;
					float phase = (tick % PERIOD_TICKS) / (float) PERIOD_TICKS;
					int desiredRgb = java.awt.Color.HSBtoRGB(phase, 1f, 1f);
					return List.of(new Permutation.Color(desiredRgb));
				})
				.build());

		this.lights.setPlugin(Nexus.getInstance());
		Tasks.wait(1, () -> this.lights.goToCue(0));
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("lights", this.lights);

		return map;
	}

	@Override
	public void onShutdown() {
		if (this.lights != null)
			this.lights.shutdown();
	}

	public static LxBoard getDefaultBoard() {
		LxBoard.Builder builder = new LxBoard.Builder()
			.plugin(Nexus.getInstance())
			.name("test")
			.channels(new ChannelList())
			.group(new Group(1, List.of(201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216)))
			.group(new Group(2, List.of(101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116)))
			.cue(new CueBuilder(5)
				.group(1, new Yaw(90), new Pitch(70))
				.group(2, new Effect(1), new Effect(2), new Intensity(100))
				.times(new CueTimesBuilder()
					.color(.25)
					.intensity(.25)
					.direction(1)
					.build())
				.build());

		for (DyeColor color : DyeColor.values()) {
			int id = (color.ordinal() * 5) + 10;
			builder.cue(new CueBuilder(id)
				.group(1, new Yaw(90), new Pitch(-70), new Color(color.getColor().asRGB()), new Intensity(100))
				.group(2, new Effect(1), new Effect(2))
				.times(new CueTimesBuilder()
					.intensity(0)
					.color(0)
					.direction(2)
					.autoFollow(0)
					.build())
				.build()
			);
			builder.cue(new CueBuilder(id + 1)
				.group(2, new Color(color.getColor().asRGB()))
				.times(new CueTimesBuilder()
					.color(.25)
					.build())
				.build()
			);
			builder.cue(new CueBuilder(id + 2)
				.group(2, new StopEffect(1))
				.times(new CueTimesBuilder()
					.direction(.5)
					.build())
				.build()
			);
		}

		return builder.build();
	}

}
