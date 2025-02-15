package gg.projecteden.nexus.features.survival;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.TameablesCommand;
import gg.projecteden.nexus.features.commands.TameablesCommand.SummonableTameableEntityType;
import gg.projecteden.nexus.features.commands.TameablesCommand.TameableEntityType;
import gg.projecteden.nexus.features.listeners.Restrictions;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

@NoArgsConstructor
public class MobNets extends Feature implements Listener {
	private static final String PREFIX = StringUtils.getPrefix(MobNets.class);

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		if (!(event.getRightClicked() instanceof LivingEntity entity))
			return;

		try {
			if (!hasMobNet(entity))
				return;

			if (entity.getHealth() <= 0)
				return;

			final PlayerInventory inventory = player.getInventory();
			final ItemStack tool = inventory.getItem(event.getHand());
			if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(tool))
				return;

			if (ItemModelType.of(tool) != ItemModelType.MOB_NET)
				return;

			if (!Restrictions.isPerkAllowedAt(event.getPlayer(), event.getRightClicked().getLocation()))
				throw new InvalidInputException("&cYou cannot use mob nets here!");

			if (TameablesCommand.isTamed(entity))
				TameablesCommand.checkOwner(player, entity);

			if (new CooldownService().check(player, "mobnet-capture-" + entity.getUniqueId(), TickTime.SECOND.x(3))) {
				final String entityName = gg.projecteden.api.common.utils.StringUtils.camelCase(entity.getType()).toLowerCase();
				final JsonBuilder error = new JsonBuilder("&3Click again to capture this &e" + entityName);

				if (SummonableTameableEntityType.isSummonable(entity.getType()))
					error.newline().next(PREFIX + "&cWarning: &3You can summon tamed " + TameableEntityType.of(entity.getType()).plural() + " with /tameables summon " + entityName);
				else if (entity.getType() == EntityType.BEE)
					error.newline().next(PREFIX + "&cWarning: &3You can capture bees by right clicking on them with a beehive or bee nest");

				throw new InvalidInputException(error);
			}

			final ItemStack mobNet = getMobNet(entity);
			entity.remove();

			if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(inventory.getItem(event.getHand())))
				inventory.setItem(event.getHand(), mobNet);
			else
				PlayerUtils.giveItem(player, mobNet);

			tool.subtract();

			new SoundBuilder(Sound.ITEM_DYE_USE)
				.location(player.getLocation())
				.category(SoundCategory.PLAYERS)
				.pitch(.1)
				.play();
		} catch (Exception ex) {
			MenuUtils.handleException(player, PREFIX, ex);
		}
	}

	@EventHandler
	public void on(EntitySpawnEvent event) {
		final Entity entity = event.getEntity();

		if (entity.getEntitySpawnReason() != SpawnReason.SPAWNER_EGG)
			return;

		if (!Nullables.isNullOrEmpty(entity.getCustomName()) && entity.getCustomName().contains("Mob Net"))
			entity.customName(null);

		new SoundBuilder(Sound.ITEM_DYE_USE)
			.location(event.getLocation())
			.category(SoundCategory.PLAYERS)
			.play();
	}

	private static boolean hasMobNet(Entity entity) {
		return hasMobNet(entity.getType());
	}

	private static boolean hasMobNet(EntityType entityType) {
		try {
			new ItemBuilder(Material.PAPER).spawnEgg(entityType);
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	@SneakyThrows
	private ItemStack getMobNet(Entity entity) {
		return new ItemBuilder(Material.PAPER)
			.spawnEgg(entity)
			.name(gg.projecteden.api.common.utils.StringUtils.camelCase(entity.getType()) + " Mob Net")
			.model("misc/mob_net/mobs/" + entity.getType().getKey().getKey())
			.build();
	}

	private static final String MODELS_DIRECTORY = "assets/minecraft/models/projecteden/items/mob_net/mobs";

	private static final String ITEM_MODELS_DIRECTORY = "assets/minecraft/items/misc/mob_net/mobs";

	public static final String ITEM_MODEL_TEMPLATE = """
	{
		"old_base_material": "<TYPE>_spawn_egg",
		"old_custom_model_data": 1,
		"model": {
			"type": "minecraft:model",
			"model": "projecteden/items/mob_net/mobs/<TYPE>",
			"tints": []
		}
	} 
	""";

	public static final String MODEL_TEMPLATE = """
	{
		"parent": "projecteden/items/mob_net/mob_net_closed",
		"textures": {
			"2": "projecteden/items/mob_net/mobs/<TYPE>"
		}
	}
	""";

	public static Map<String, Object> generate() {
		return new HashMap<>() {{
			for (EntityType entityType : EntityType.values()) {
				if (!hasMobNet(entityType))
					continue;

				final var entityTypeName = entityType.getKey().getKey();
				final Map<String, Object> variables = Map.of("TYPE", entityTypeName);

				final String itemModel = process(ITEM_MODEL_TEMPLATE, variables);
				put(ITEM_MODELS_DIRECTORY + "/" + entityTypeName + ".json", itemModel);

				final String model = process(MODEL_TEMPLATE, variables);
				put(MODELS_DIRECTORY + "/" + entityTypeName + ".json", model);
			}
		}};
	}

	private static String process(String templateString, Map<String, Object> variables) {
		final AtomicReference<String> template = new AtomicReference<>(templateString);

		BiConsumer<String, Object> consumer = (id, value) ->
			template.set(template.get().replaceAll("<" + id + ">", "" + value));

		variables.forEach(consumer);

		return template.get();
	}

}

