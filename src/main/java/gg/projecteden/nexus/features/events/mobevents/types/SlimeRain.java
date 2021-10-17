package gg.projecteden.nexus.features.events.mobevents.types;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.mobevents.MobEventUtils;
import gg.projecteden.nexus.features.events.mobevents.MobEvents;
import gg.projecteden.nexus.features.events.mobevents.annotations.Type;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobOptions;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.stream.Stream;

@Type(MobEventType.SLIME_RAIN)
public class SlimeRain extends IMobEvent {

	public SlimeRain() {
		super.initialize();

		this.name = "Slime Rain";
		this.ignoreLight = true;
		this.ignoreFloor = true;
		this.ignoreY = true;
		this.mobOptionsList = Arrays.asList(
			new MobOptions(EntityType.SLIME, 75.0, 20, 30),
			new MobOptions(EntityType.MAGMA_CUBE, 25.0, 15, 30, Difficulty.HARD)
		);
	}

	@Override
	public void startEvent(World world, Player debugger) {
		world.setStorm(true);

		Tasks.wait(TickTime.SECOND.x(5), () -> super.startEvent(world, debugger));
	}

	@Override
	public void endEvent(World world, Player debugger) {
		super.endEvent(world, debugger);

		Tasks.wait(TickTime.SECOND.x(5), () -> world.setStorm(false));
	}

	@Override
	protected boolean extraChecks(Player player, MobOptions mobOptions, DifficultyUser user) {
		return !MobEventUtils.failChance(user.getDifficulty(), 85, 5);
	}

	@Override
	protected Stream<Entity> filterMobCap(EntityType entityType, Stream<Entity> entities) {
		entities = entities.filter(entity -> {
			if (entity instanceof Slime slime) {
				if (entity instanceof MagmaCube)
					return true;
				if (slime.getSize() <= 1)
					return false;
			}
			return true;
		});

		return entities;
	}

	@Override
	public Location handleLocation(Location location, MobOptions mobOptions) {
		Block block = location.getBlock();
		if (block.getLightFromSky() == 0)
			return null;

		if (block.getLightFromSky() > 12) {
			location = location.toHighestLocation().add(0, 80, 0);
			if (!MaterialTag.ALL_AIR.isTagged(location.getBlock()))
				return null;
		}

		return location;
	}

	@Override
	public Entity handleEntity(Entity entity, DifficultyUser user, MobOptions mobOptions) {
		if (entity instanceof Slime slime)
			MobEventUtils.slimeSize(slime, user.getDifficulty());

		entity.setMetadata(MobEvents.METADATA_NO_FALL_DAMAGE, new FixedMetadataValue(Nexus.getInstance(), true));

		return entity;
	}

	@EventHandler
	public void onSlimeSplit(SlimeSplitEvent event) {
		Slime slime = event.getEntity();
		if (!this.applies(slime))
			return;

		event.setCount(2);
	}

}
