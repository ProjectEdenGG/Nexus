package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.recipes.functionals.WardCharm;
import gg.projecteden.nexus.features.recipes.menu.CustomCraftingRecipeMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.NonNull;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;

import static gg.projecteden.nexus.features.recipes.functionals.WardCharm.NBT_KEY;
import static gg.projecteden.nexus.features.recipes.functionals.WardCharm.SUPPORTED_TYPES;
import static gg.projecteden.nexus.utils.ItemUtils.isModelMatch;

public class WardCharmCommand extends CustomCommand {

	public WardCharmCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("recipe")
	void recipe() {
		var recipe = CustomRecipes.getRecipes().values()
			.stream()
			.filter(nexusRecipe -> isModelMatch(nexusRecipe.getResult(), WardCharm.getItem()))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Could not find recipe"));

		new CustomCraftingRecipeMenu(recipe).open(player());
	}

	@Path("unward")
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
