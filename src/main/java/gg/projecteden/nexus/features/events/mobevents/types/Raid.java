package gg.projecteden.nexus.features.events.mobevents.types;

import gg.projecteden.nexus.features.events.mobevents.annotations.Type;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobOptions;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpellCastEvent;

import java.util.Arrays;
import java.util.List;

@Type(MobEventType.RAID)
public class Raid extends IMobEvent implements Listener {
	private static final List<Spell> blockedSpells = Arrays.asList(Spell.BLINDNESS, Spell.SUMMON_VEX);

	public Raid() {
		this.name = "Illager Raid";
		this.ignoreLight = true;
		this.mobOptionsList = Arrays.asList(
			new MobOptions(EntityType.PILLAGER, 50, 10, 30),
			new MobOptions(EntityType.VINDICATOR, 10, 3, 30, Difficulty.HARD),
			new MobOptions(EntityType.WITCH, 10, 2, 30, Difficulty.HARD),
			new MobOptions(EntityType.EVOKER, 5, 1, 30, Difficulty.HARD),
			new MobOptions(EntityType.RAVAGER, 5, 1, 30, Difficulty.HARD),
			new MobOptions(EntityType.ILLUSIONER, 5, 1, 30, Difficulty.EXPERT)
		);
	}

	@Override
	public void notifySound(List<Player> players) {
		new SoundBuilder(Sound.EVENT_RAID_HORN).volume(75).receivers(players).play();
	}

	@Override
	protected Entity handleEntity(Entity entity, DifficultyUser user, MobOptions mobOptions) {
		if (entity instanceof Evoker evoker)
			new SoundBuilder(Sound.ENTITY_EVOKER_PREPARE_WOLOLO).location(evoker).volume(5).play();

		if (entity instanceof Raider raider) {
			raider.setPatrolLeader(false);
			raider.setCanJoinRaid(false);
		}

		return entity;
	}

	@EventHandler
	public void onSpellCast(EntitySpellCastEvent event) {
		Spellcaster spellcaster = event.getEntity();
		if (!this.applies(spellcaster))
			return;

		if (blockedSpells.contains(event.getSpell()))
			event.setCancelled(true);
	}
}
