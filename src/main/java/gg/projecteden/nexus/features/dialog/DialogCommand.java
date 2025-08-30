package gg.projecteden.nexus.features.dialog;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import lombok.NonNull;
import org.bukkit.Material;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogCommand extends CustomCommand {

	public DialogCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("test confirm")
	void test_confirm() {
		new DialogBuilder()
			.title(new JsonBuilder("This is a test title").bold())
			.bodyText(new JsonBuilder("This is a test 1").hover("Test hover"))
			.bodyItem(new ItemBuilder(ItemModelType.EXCLAMATION).hideTooltip().dyeColor(ColorType.LIGHT_RED).build(), " Warning")
			.bodyText("This is a test 2")
			.bodyItem(new ItemBuilder(Material.PAPER).model("ui/gui/mcmmo/acrobatics").build())
			.bodyItem(new ItemBuilder(ItemModelType.SOCIALMEDIA_INSTAGRAM).build(), "Instagram")
			.bodyItem(new ItemBuilder(Material.PAPER).model("ui/images/gamelobby/juggernaut").build())
			.inputText("test1", "Test input 1")
			.inputText("test2", new JsonBuilder("Test input 2").color(Color.RED))
			.checkbox("test3", "false by default")
			.checkbox("test4", "true by default", true)
			.confirmation()
			.onSubmit(response -> player().sendMessage("Submitted: " + response.payload()))
			.onCancel(player -> player().sendMessage("Cancelled"))
			.cancelText("Test Cancel")
			.open(player());
	}

	@Path("test multiAction [columns]")
	void test_multiAction(@Arg("2") int columns) {
		List<OptionEntry> entryList = new ArrayList<>();
		for (Dev dev : Dev.values())
			entryList.add(OptionEntry.create(dev.name().toLowerCase(), new JsonBuilder(dev.getNickname()).build(), Dev.GRIFFIN.is(dev)));

		new DialogBuilder()
			.title(new JsonBuilder("This is a test title").hover("Test hover").bold())
			.bodyText(new JsonBuilder("Button testing screen"))
			.inputText("test1", "Test input 1")
			.inputText("test2", new JsonBuilder("Test input 2").color(Color.RED))
			.checkbox("test3", "false by default")
			.checkbox("test4", "true by default", true)
			.singleOption("test5", "dev enum \"dropdown\"", entryList)
			.multiAction()
			.button("Button 1", 50, (response) -> response.getPlayer().sendMessage("Test button 1"))
			.button("Button 2", 50, (response) -> response.getPlayer().sendMessage("Test button 2"))
			.button("Button 4 (Next Menu)", (response) -> {
				new DialogBuilder()
					.title(new JsonBuilder("This is a test title").hover("Test hover").bold())
					.bodyText(new JsonBuilder("Button testing screen"))
					.inputText("test1", "Test input 1")
					.inputText("test2", new JsonBuilder("Test input 2").color(Color.RED))
					.checkbox("test3", "false by default")
					.checkbox("test4", "true by default", true)
					.multiAction()
					.button("Button 4", 50, (response2) -> response.getPlayer().sendMessage("Test button 4"))
					.button("Button 5", 50, (response2) -> response.getPlayer().sendMessage("Test button 5"))
					.button("Button 6", (response2) -> response.getPlayer().sendMessage("Test button 6"))
					.button("Button 7", (response2) -> response.getPlayer().sendMessage("Test button 7"))
					.columns(columns)
					.open(player());
			})
			.columns(columns)
			.open(player());
	}

	@Path("test notice [columns]")
	void test_notice() {
		new DialogBuilder()
			.title("Notice")
			.bodyText(new JsonBuilder("Test noticeeeeeeeeeeeeeeeee"))
			.bodyItem(new ItemBuilder(Material.PAPER).model("ui/images/gamelobby/juggernaut").build())
			.bodyBlankLines(10)
			.notice()
			.open(player());
	}

}


