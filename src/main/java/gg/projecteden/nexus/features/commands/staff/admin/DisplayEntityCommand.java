package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.resourcepack.models.font.CustomFont;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static java.lang.Float.parseFloat;

@Permission(Group.ADMIN)
public class DisplayEntityCommand extends CustomCommand {

	public DisplayEntityCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("edit item")
	@Description("Update the item on the nearest item display")
	void edit_item() {
		itemDisplay().setItemStack(getToolRequired());
		send(PREFIX + "Item updated");
	}

	@Path("get item")
	@Description("Get the item on the nearest item display")
	void get_item() {
		giveItem(itemDisplay().getItemStack());
	}

	@Path("edit text background <color>")
	@Description("Set the text background color on the nearest text display")
	void edit_text_background(ChatColor color) {
		textDisplay().setBackgroundColor(ColorType.toBukkitColor(color));
	}

	@Path("edit text text <text...> [--font]")
	@Description("Set the text background color on the nearest text display")
	void edit_text_text(String text, @Switch @Arg("default") CustomFont font) {
		textDisplay().text(new JsonBuilder(text).font(font).build());
	}

	@Path("get text scale")
	@Description("Get the scale of the nearest text display")
	void get_text_scale() {
		Transformation transformation = textDisplay().getTransformation();
		send(PREFIX + "Scale: " + transformation.getScale());
	}

	@Path("edit text scale <scale>")
	@Description("Set the scale of the nearest text display")
	void edit_text_scale(float scale) {
		Transformation transformation = textDisplay().getTransformation();
		textDisplay().setTransformation(new Transformation(
			transformation.getTranslation(),
			transformation.getLeftRotation(),
			new Vector3f(scale, scale, scale),
			transformation.getRightRotation()
		));
	}

	@Path("edit text rotation [--yaw] [--pitch]")
	@Description("Set the scale of the nearest text display")
	void edit_text_rotation(@Switch String yaw, @Switch String pitch) {
		if (yaw == null) yaw = String.valueOf(textDisplay().getYaw());
		if (pitch == null) pitch = String.valueOf(textDisplay().getPitch());
		if ("current".equalsIgnoreCase(yaw)) yaw = String.valueOf(player().getLocation().multiply(-1).getYaw());
		if ("current".equalsIgnoreCase(pitch)) pitch = String.valueOf(player().getLocation().multiply(-1).getPitch());

		if (!Utils.isDouble(yaw))
			error("Invalid yaw " + yaw);
		if (!Utils.isDouble(pitch))
			error("Invalid pitch " + pitch);

		textDisplay().setRotation(parseFloat(yaw), parseFloat(pitch));
	}

	@NotNull
	private ItemDisplay itemDisplay() {
		final Entity nearestEntityRequired = getNearestEntityRequired();
		if (!(nearestEntityRequired instanceof ItemDisplay itemDisplay))
			throw new InvalidInputException("Nearest entity is not an item display");
		return itemDisplay;
	}

	@NotNull
	private TextDisplay textDisplay() {
		final Entity nearestEntityRequired = getNearestEntityRequired();
		if (!(nearestEntityRequired instanceof TextDisplay textDisplay))
			throw new InvalidInputException("Nearest entity is not a text display");
		return textDisplay;
	}

	@NotNull
	private BlockDisplay blockDisplay() {
		final Entity nearestEntityRequired = getNearestEntityRequired();
		if (!(nearestEntityRequired instanceof BlockDisplay blockDisplay))
			throw new InvalidInputException("Nearest entity is not a block display");
		return blockDisplay;
	}

}
