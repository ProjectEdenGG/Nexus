package gg.projecteden.nexus.features.events.mobevents.types;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.mobevents.MobEventUtils;
import gg.projecteden.nexus.features.events.mobevents.MobEvents;
import gg.projecteden.nexus.features.events.mobevents.annotations.Type;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobOptions;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

@Type(MobEventType.SLIME_RAIN)
public class SlimeRain extends IMobEvent {

	public SlimeRain() {
		this.name = "Slime Rain";
		this.ignoreLight = true;
		this.ignoreFloor = true;
		this.mobOptionsList = Arrays.asList(
			new MobOptions(EntityType.SLIME, 75.0, 20, 30),
			new MobOptions(EntityType.MAGMA_CUBE, 25.0, 15, 30)
		);
	}

	@Override
	public void startEvent(World world) {
		world.setStorm(true);

		Tasks.wait(TickTime.SECOND.x(5), () -> super.startEvent(world));
	}

	@Override
	public void endEvent(World world) {
		super.endEvent(world);

		Tasks.wait(TickTime.SECOND.x(5), () -> world.setStorm(false));
	}

	@Override
	protected boolean extraChecks(Player player, MobOptions mobOptions, DifficultyUser user) {
		return !MobEventUtils.failChance(user.getDifficulty(), 80, 15);
	}

	@Override
	public Location handleLocation(Location location, MobOptions mobOptions) {
		Block block = location.getBlock();
		if (block.getLightFromSky() == 0)
			return null;

		if (block.getLightFromSky() > 12) {
			location = block.getRelative(0, 100, 0).getLocation();
		}

		return location;
	}

	@Override
	public Entity handleEntity(Entity entity, DifficultyUser user) {
		if (entity instanceof Slime slime) {
			int minSize = 0;
			int maxSize = 2;
			switch (user.getDifficulty()) {
				case HARD:
					++minSize;
					++maxSize;
				case EXPERT:
					++minSize;
					++maxSize;
			}

			slime.setSize(RandomUtils.randomInt(minSize, maxSize));
		}

		entity.setMetadata(MobEvents.METADATA_NO_FALL_DAMAGE, new FixedMetadataValue(Nexus.getInstance(), true));

		return entity;
	}

}
