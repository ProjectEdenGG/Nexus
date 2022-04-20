package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.EnumUtils;
import me.lexikiq.HasLocation;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class EdenEvent extends Feature implements Listener {

	public QuestConfig getConfig() {
		return getClass().getAnnotation(QuestConfig.class);
	}

	public boolean isEventActive() {
		final LocalDateTime now = LocalDateTime.now();
		return now.isAfter(getStart().atStartOfDay()) && now.isBefore(getEnd().atStartOfDay());
	}

	public boolean isAtEvent(HasLocation hasLocation) {
		final Location location = hasLocation.getLocation();
		if (!location.getWorld().getName().equals(getConfig().world()))
			return false;

		return new WorldGuardUtils(location).isInRegion(location, getConfig().region());
	}

	public boolean shouldHandle(Player player) {
		return isEventActive() && isAtEvent(player);
	}

	@NotNull
	public LocalDate getStart() {
		return toLocalDate(getConfig().start());
	}

	@NotNull
	public LocalDate getEnd() {
		return toLocalDate(getConfig().end());
	}

	@NotNull
	private LocalDate toLocalDate(Date config) {
		return LocalDate.of(config.y(), config.m(), config.d());
	}

	public <T extends InteractableEntity> T interactableOf(Entity entity) {
		for (Enum<? extends InteractableEntity> _entity : EnumUtils.valuesExcept(getConfig().entities())) {
			final InteractableEntity interactableEntity = (InteractableEntity) _entity;
			if (interactableEntity.getPredicate() != null)
				if (interactableEntity.getPredicate().test(entity))
					return (T) interactableEntity;
		}

		return null;
	}

	public <T extends InteractableNPC> T interactableOf(NPC npc) {
		for (Enum<? extends InteractableNPC> _npc : EnumUtils.valuesExcept(getConfig().npcs())) {
			final InteractableNPC interactableNPC = (InteractableNPC) _npc;
			if (interactableNPC.getPredicate() != null)
				if (interactableNPC.getPredicate().test(npc))
					return (T) interactableNPC;
		}

		return null;
	}

	@EventHandler
	public void _onNPCRightClick(NPCRightClickEvent event) {
		final Player player = event.getClicker();
		if (!shouldHandle(player))
			return;

		final Easter22NPC npc = interactableOf(event.getNPC());
		if (npc == null)
			return;

		event.setCancelled(true);
		new QuesterService().edit(event.getClicker(), quester -> quester.interact(npc, event));
	}

	@EventHandler
	public void _onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		final Player player = event.getPlayer();
		if (!shouldHandle(player))
			return;

		final InteractableEntity entity = interactableOf(event.getRightClicked());
		if (entity == null)
			return;

		event.setCancelled(true);
		new QuesterService().edit(event.getPlayer(), quester -> quester.interact(entity, event));
	}

	@EventHandler
	public void _onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!shouldHandle(player))
			return;

		new QuesterService().edit(player, quester -> quester.interact(event));
	}

}
