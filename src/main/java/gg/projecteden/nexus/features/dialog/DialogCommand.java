package gg.projecteden.nexus.features.dialog;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static io.papermc.paper.registry.data.dialog.action.DialogAction.customClick;
import static io.papermc.paper.registry.data.dialog.body.DialogBody.plainMessage;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickCallback.UNLIMITED_USES;

@SuppressWarnings("UnstableApiUsage")
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
			.inputText("test1", "Test input 1")
			.inputText("test2", new JsonBuilder("Test input 2").color(Color.RED))
			.checkbox("test3", "false by default")
			.checkbox("test4", "true by default", true)
			.confirmation()
			.onSubmit(response -> player().sendMessage("Submitted: " + response.payload().string()))
			.onCancel(() -> player().sendMessage("Cancelled"))
			.cancelText("Test Cancel")
			.show(player());
	}

	@Path("test multiAction [columns]")
	void test_multiAction(@Arg("2") int columns) {
		new DialogBuilder()
			.title(new JsonBuilder("This is a test title").hover("Test hover").bold())
			.after(DialogAfterAction.NONE)
			.bodyText(new JsonBuilder("Button testing screen"))
			.inputText("test1", "Test input 1")
			.inputText("test2", new JsonBuilder("Test input 2").color(Color.RED))
			.checkbox("test3", "false by default")
			.checkbox("test4", "true by default", true)
			.multiAction()
			.button("Button 1", 50, (response) -> response.getPlayer().sendMessage("Test button 1"))
			.button("Button 2", 50, (response) -> response.getPlayer().sendMessage("Test button 2"))
			.button("Button 4", (response) -> {
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
					.show(player());
			})
			.columns(columns)
			.show(player());
	}

	public static class DialogBuilder {
		private Component title;
		private boolean closeWithEscape = false;
		private DialogAfterAction after = DialogAfterAction.CLOSE;
		private List<DialogBody> body = new ArrayList<>();
		private List<DialogInput> inputs = new ArrayList<>();

		private PlainMessageDialogBody line() {
			return plainMessage(text(""));
		}

		public DialogBuilder title(String title) {
			return this.title(new JsonBuilder(title).bold().build());
		}

		public DialogBuilder title(JsonBuilder title) {
			return this.title(title.bold().build());
		}

		public DialogBuilder title(Component title) {
			this.title = title;
			return this;
		}

		public DialogBuilder bodyText(String text) {
			return this.bodyText(new JsonBuilder(text).build());
		}

		public DialogBuilder bodyText(JsonBuilder text) {
			return this.bodyText(text.build());
		}

		public DialogBuilder bodyText(Component text) {
			this.body(plainMessage(text));
			return this;
		}

		public DialogBuilder bodyItem(ItemStack item) {
			return this.bodyItem(item, (Component) null);
		}

		public DialogBuilder bodyItem(ItemStack item, String description) {
			return this.bodyItem(item, new JsonBuilder(description));
		}

		public DialogBuilder bodyItem(ItemStack item, JsonBuilder description) {
			return this.bodyItem(item, description.build());
		}

		public DialogBuilder bodyItem(ItemStack item, Component description) {
			return this.body(
				DialogBody.item(item)
					.description(description == null ? null : DialogBody.plainMessage(description))
					.build());
		}

		public DialogBuilder body(DialogBody body) {
			if (this.body == null)
				this.body = new ArrayList<>();
			this.body.add(body);
			return this;
		}

		public DialogBuilder inputText(String key) {
			return this.inputText(key, (Component) null);
		}

		public DialogBuilder inputText(String key, String label) {
			return this.inputText(key, new JsonBuilder(label));
		}

		public DialogBuilder inputText(String key, JsonBuilder label) {
			return this.inputText(key, label.build());
		}

		public DialogBuilder inputText(String key, Component label) {
			return this.inputText(key, label, "");
		}

		public DialogBuilder inputText(String key, Component label, String initial) {
			return this.input(
				DialogInput.text(key, label == null ? text("") : label)
					.labelVisible(label != null)
					.initial(initial)
					.build()
			);
		}

		public DialogBuilder checkbox(String key, String label) {
			return this.checkbox(key, label, false);
		}

		public DialogBuilder checkbox(String key, String label, boolean initial) {
			return this.input(DialogInput.bool(key, text(label), initial, "true", "false"));
		}

		public DialogBuilder input(DialogInput input) {
			if (this.inputs == null)
				this.inputs = new ArrayList<>();
			this.inputs.add(input);
			return this;
		}

		public DialogBuilder closeWithEscape(boolean closeWithEscape) {
			this.closeWithEscape = closeWithEscape;
			return this;
		}

		public DialogBuilder after(DialogAfterAction after) {
			this.after = after;
			return this;
		}

		public ConfirmationDialogBuilder confirmation() {
			return new ConfirmationDialogBuilder(build());
		}

		public MultiActionDialogBuilder multiAction() {
			return new MultiActionDialogBuilder(build());
		}

		private @NotNull DialogBase build() {
			return DialogBase.builder(title)
				.canCloseWithEscape(closeWithEscape)
				.afterAction(after)
				.body(body)
				.inputs(inputs)
				.pause(false)
				.build();
		}

		public static void close(Player player) {
			((CraftPlayer) player).getHandle().connection.send(ClientboundClearDialogPacket.INSTANCE);
		}
	}

	@RequiredArgsConstructor
	public static class ConfirmationDialogBuilder {
		private final DialogBase base;
		private Consumer<DialogResponseView> onSubmit;
		private Runnable onCancel;
		private Component submitText = text("Submit");
		private Component cancelText = text("Cancel");

		public void show(Audience audience) {
			audience.showDialog(Dialog.create(builder -> {
				DialogActionCallback onConfirm = (response, $) -> this.onSubmit.accept(response);
				DialogActionCallback onCancel = (response, $) -> this.onCancel.run();

				builder.empty().base(base).type(DialogType.confirmation(
					ActionButton.builder(submitText).action(customClick(onConfirm, Options.builder().uses(UNLIMITED_USES).build())).build(),
					ActionButton.builder(cancelText).action(customClick(onCancel, Options.builder().uses(UNLIMITED_USES).build())).build()
				));
			}));
		}

		public ConfirmationDialogBuilder onSubmit(Consumer<DialogResponseView> onSubmit) {
			this.onSubmit = onSubmit;
			return this;
		}

		public ConfirmationDialogBuilder onCancel(Runnable onCancel) {
			this.onCancel = onCancel;
			return this;
		}

		public ConfirmationDialogBuilder submitText(String text) {
			return this.submitText(new JsonBuilder(text));
		}

		public ConfirmationDialogBuilder submitText(JsonBuilder text) {
			return this.submitText(text.build());
		}

		public ConfirmationDialogBuilder submitText(Component text) {
			this.submitText = text;
			return this;
		}

		public ConfirmationDialogBuilder cancelText(String text) {
			return this.cancelText(new JsonBuilder(text));
		}

		public ConfirmationDialogBuilder cancelText(JsonBuilder text) {
			return this.cancelText(text.build());
		}

		public ConfirmationDialogBuilder cancelText(Component text) {
			this.cancelText = text;
			return this;
		}
	}

	@RequiredArgsConstructor
	public static class MultiActionDialogBuilder {
		private final DialogBase base;
		private final List<ActionButton> actions = new ArrayList<>();
		private ActionButton exitAction;
		private int columns = 2;

		public void show(Audience audience) {
			audience.showDialog(Dialog.create(builder -> {
				builder.empty().base(base).type(DialogType.multiAction(actions, exitAction, columns));
			}));
		}

		public MultiActionDialogBuilder button(String label, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(), 150, action);
		}

		public MultiActionDialogBuilder button(String label, int width, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(), width, action);
		}

		public MultiActionDialogBuilder button(String label, String tooltip, int width, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(tooltip), width, action);
		}

		public MultiActionDialogBuilder button(JsonBuilder label, JsonBuilder tooltip, int width, DialogResponseCallback action) {
			return this.button(label.build(), tooltip.build(), width, action);
		}

		public MultiActionDialogBuilder button(Component label, Component tooltip, int width, DialogResponseCallback action) {
			this.actions.add(
				ActionButton.builder(label)
					.tooltip(tooltip)
					.width(width)
					.action(customClick(((response, audience) -> {
						action.accept(new DialogResponse(response, audience));
					}), Options.builder().uses(UNLIMITED_USES).build()))
					.build()
			);
			return this;
		}

		public MultiActionDialogBuilder exitButton(String label) {
			return this.exitButton(label, response -> {});
		}

		public MultiActionDialogBuilder exitButton(String label, DialogResponseCallback action) {
			return this.exitButton(new JsonBuilder(label), new JsonBuilder(), 150, action);
		}

		public MultiActionDialogBuilder exitButton(String label, String tooltip, int width, DialogResponseCallback action) {
			return this.exitButton(new JsonBuilder(label), new JsonBuilder(tooltip), width, action);
		}

		public MultiActionDialogBuilder exitButton(JsonBuilder label, JsonBuilder tooltip, int width, DialogResponseCallback action) {
			return this.exitButton(label.build(), tooltip.build(), width, action);
		}

		public MultiActionDialogBuilder exitButton(Component label, Component tooltip, int width, DialogResponseCallback action) {
			this.exitAction = ActionButton.builder(label)
				.tooltip(tooltip)
				.width(width)
				.action(customClick(((response, audience) -> {
					var dialogResponse = new DialogResponse(response, audience);
					action.accept(dialogResponse);
					dialogResponse.closeDialog();
				}), Options.builder().uses(UNLIMITED_USES).build()))
				.build();
			return this;
		}

		public MultiActionDialogBuilder columns(int columns) {
			this.columns = columns;
			return this;
		}
	}

	@Data
	@RequiredArgsConstructor
	public static class DialogResponse {
		private final Player player;
		DialogResponseView response;

		public DialogResponse(DialogResponseView response, Audience audience) {
			this.response = response;
			if (audience instanceof Player player)
				this.player = player;
			else
				this.player = null;
		}

		public String getText(String key) {
			return this.response.getText(key);
		}

		public Boolean getBoolean(String key) {
			return this.response.getBoolean(key);
		}

		public Float getFloat(String key) {
			return this.response.getFloat(key);
		}

		public void closeDialog() {
			DialogBuilder.close(this.player);
		}
	}

	@FunctionalInterface
	public interface DialogResponseCallback {
		void accept(DialogResponse response);
	}

	@Builder(buildMethodName = "_build")
	public static class SingleInputDialogBuilder {
		private String title;
		private String body;
		private String label;
		private Consumer<String> onConfirm;
		private Runnable onCancel;

		private PlainMessageDialogBody line() {
			return plainMessage(text(""));
		}

		public void show(Audience audience) {
			audience.showDialog(Dialog.create(builder -> {
				DialogActionCallback onConfirm = (dialog, $) -> this.onConfirm.accept(dialog.getText("input"));
				DialogActionCallback onCancel = (dialog, $) -> this.onCancel.run();

				List<DialogBody> body = new ArrayList<>(List.of(line(), line(), line(), line()));
				if (isNotNullOrEmpty(this.body))
					body.add(plainMessage(text(this.body)));

				var base = DialogBase.builder(text(title))
					.externalTitle(text("External title???"))
					.canCloseWithEscape(false)
					.afterAction(DialogAfterAction.CLOSE)
					.body(body)
					.inputs(List.of(DialogInput.text("input", text(label == null ? "" : label)).build())
				).build();

				var type = DialogType.confirmation(
					ActionButton.builder(text("Confirm")).action(customClick(onConfirm, Options.builder().build())).build(),
					ActionButton.builder(text("Cancel")).action(customClick(onCancel, Options.builder().build())).build()
				);

				builder.empty().base(base).type(type);
			}));
		}

		@SuppressWarnings("unused")
		public static class SingleInputDialogBuilderBuilder {

			public void show(Audience audience) {
				if (audience instanceof Player player)
					player.closeInventory();
				_build().show(audience);
			}

		}
	}
}


