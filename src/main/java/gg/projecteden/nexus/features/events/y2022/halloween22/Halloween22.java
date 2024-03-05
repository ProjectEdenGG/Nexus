package gg.projecteden.nexus.features.events.y2022.halloween22;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22Entity;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22NPC;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22QuestItem;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22QuestReward;
import gg.projecteden.nexus.features.events.y2022.halloween22.quests.Halloween22QuestTask;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.warps.WarpType;

@QuestConfig(
	tasks = Halloween22QuestTask.class,
	npcs = Halloween22NPC.class,
	entities = Halloween22Entity.class,
	items = Halloween22QuestItem.class,
	rewards = Halloween22QuestReward.class,
	start = @Date(m = 10, d = 15, y = 2022),
	end = @Date(m = 11, d = 10, y = 2022),
	world = "events",
	region = "halloween22",
	warpType = WarpType.HALLOWEEN22
)
public class Halloween22 extends EdenEvent {

	public static Halloween22 get() {
		return Features.get(Halloween22.class);
	}

//	private final List<Location> bloodDripLocations = new ArrayList<>() {{
//		add(loc(2, 122, 57));
//		add(loc(3, 122, 54));
//		add(loc(-3, 122, 54));
//		add(loc(-2, 121, 57));
//		add(loc(-4, 120, 55));
//		add(loc(-2, 120, 53));
//		add(loc(4, 120, 55));
//		add(loc(3, 119, 56));
//		add(loc(1, 119, 56));
//		add(loc(-1, 119, 57));
//		add(loc(-2, 118, 55));
//		add(loc(1, 118, 55));
//	}};
//
//	private final Location heartLocation = loc(0, 123, 55);
//	private final Location bloodLocation = loc(0, 121, 55);
//
//	public void particles() {
//		SoundBuilder heartbeat = new SoundBuilder(CustomSound.HEARTBEAT).location(heartLocation).volume(2).pitch(0.1);
//		ParticleBuilder heartParticles = new ParticleBuilder(Particle.CRIMSON_SPORE).location(heartLocation).offset(3, 2, 3).count(50).extra(0);
//		Tasks.repeat(0, TickTime.SECOND.x(2.8), () -> {
//			heartbeat.play();
//
//			if (hasPlayersNearby(heartParticles.location(), 25))
//				heartParticles.spawn();
//
//		});
//
//		SoundBuilder bloodGush = new SoundBuilder(CustomSound.BLOOD_GUSHING).location(bloodLocation).volume(0.8).pitch(0.1);
//		Tasks.repeat(0, TickTime.TICK.x(16), bloodGush::play);
//
//		ParticleBuilder bloodDrip = new ParticleBuilder(Particle.FALLING_LAVA).extra(0.1).count(1);
//		Tasks.repeat(0, TickTime.SECOND, () -> {
//			if (!hasPlayersNearby(heartLocation, 25))
//				return;
//
//			for (Location location : bloodDripLocations) {
//				if (location == null || !location.isChunkLoaded())
//					continue;
//
//				if (RandomUtils.chanceOf(25)) {
//					Tasks.wait(RandomUtils.randomInt(1, 20), () -> bloodDrip.location(location.toCenterLocation().add(0, 0.45, 0)).spawn());
//				}
//			}
//		});
//	}

}
