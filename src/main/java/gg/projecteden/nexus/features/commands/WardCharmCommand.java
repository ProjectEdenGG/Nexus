package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.recipes.functionals.WardCharm;
import gg.projecteden.nexus.features.recipes.menu.CustomCraftingRecipeMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NonNull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import static gg.projecteden.nexus.features.recipes.functionals.WardCharm.NBT_KEY;
import static gg.projecteden.nexus.features.recipes.functionals.WardCharm.SUPPORTED_TYPES;
import static gg.projecteden.nexus.utils.ItemUtils.isModelMatch;

public class WardCharmCommand extends CustomCommand {

	public WardCharmCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("ward [--nearest]")
	@Permission(Group.ADMIN)
	@Description("Ward the nearest or target entity")
	void ward(@Switch boolean nearest) {
		LivingEntity entity;
		if (nearest) {
			entity = world().getEntitiesByClass(LivingEntity.class).stream()
				.filter(entity1 -> entity1.getType() != EntityType.PLAYER)
				.min((e1, e2) -> Double.compare(Distance.distance(e1, player()).get(), Distance.distance(e2, player()).get()))
				.orElseThrow(() -> new InvalidInputException("No living entities found"));

			double distance = distance(entity).getRealDistance();
			if (distance > 20)
				error("Nearest entity is too far away (" + distance + " blocks)");
		} else {
			entity = getTargetLivingEntityRequired();
		}

		WardCharm.ward(entity, player());
		var named = new JsonBuilder();
		if (entity.customName() != null)
			named = json(" &3named ").next(entity.customName());

		send(json(PREFIX + "Warded &e" + camelCase(entity.getType())).next(named));
	}

	@Path("recipe")
	@Description("View the crafting recipe for the Ward Charm")
	void recipe() {
		var recipe = CustomRecipes.getRecipes().values()
			.stream()
			.filter(nexusRecipe -> isModelMatch(nexusRecipe.getResult(), WardCharm.getItem()))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Could not find recipe"));

		new CustomCraftingRecipeMenu(recipe).open(player());
	}

	@Path("check")
	@Description("Check if an entity is warded")
	void check() {
		var entity = getTargetEntityRequired();
		var pdc = entity.getPersistentDataContainer();
		boolean has = pdc.has(NBT_KEY, PersistentDataType.STRING);
		send(PREFIX + "Ward status of " + camelCase(entity.getType()) + ": " + (has ? "&aWarded" : "&cNot warded"));
	}

	@Path("unward")
	@Description("Unward an entity")
	void unward() {
		var entity = getTargetEntityRequired();
		var pdc = entity.getPersistentDataContainer();

		if (!pdc.has(NBT_KEY, PersistentDataType.STRING))
			error("That entity is not warded");

		ConfirmationMenu.builder()
			.title("Unward " + camelCase(entity.getType()) + "?")
			.onConfirm(e -> {
				pdc.remove(NBT_KEY);
				send(PREFIX + "Unwarded " + camelCase(entity.getType()));
			})
			.open(player());
	}

	@Path("wardable [page]")
	@Description("View the list of entity types that can be warded")
	void wardable(@Arg("1") int page) {
		send(PREFIX + "Wardable entity types");
		new Paginator<EntityType>()
			.values(SUPPORTED_TYPES)
			.formatter(((entityType, index) -> json(index + " &e" + camelCase(entityType))))
			.command("/wardcharm wardable")
			.page(page)
			.send();
	}
}
