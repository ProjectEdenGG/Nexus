package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.parchment.OptionalPlayerLike;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.papermc.paper.registry.data.dialog.action.DialogAction.customClick;
import static io.papermc.paper.registry.data.dialog.body.DialogBody.plainMessage;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickCallback.UNLIMITED_USES;

// https://misode.github.io/dialog/

@SuppressWarnings("UnstableApiUsage")
public class DialogUtils {

	public static class DialogBuilder {
		private Component title;
		private boolean closeWithEscape = true;
		private DialogAfterAction after = DialogAfterAction.NONE;
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

		public DialogBuilder bodyBlankLine() {
			return this.bodyText("&f");
		}

		public DialogBuilder bodyBlankLines(int lines) {
			for (int i = 0; i < lines; i++)
				this.bodyBlankLine();
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

		public DialogBuilder inputText(String key, String label, String initial) {
			return this.inputText(key, new JsonBuilder(label).build(), initial);
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

		public DialogBuilder singleOption(String key, String label, List<OptionEntry> entries) {
			return this.input(DialogInput.singleOption(key, new JsonBuilder(label).build(), entries).build());
		}

		public DialogBuilder input(DialogInput input) {
			if (this.inputs == null)
				this.inputs = new ArrayList<>();
			this.inputs.add(input);
			return this;
		}

		public DialogBuilder preventEscape() {
			return closeWithEscape(false);
		}

		public DialogBuilder allowEscape() {
			return closeWithEscape(true);
		}

		public DialogBuilder closeWithEscape(boolean closeWithEscape) {
			this.closeWithEscape = closeWithEscape;
			return this;
		}

		public DialogBuilder closeOnSubmit() {
			return after(DialogAfterAction.CLOSE);
		}

		public DialogBuilder waitOnSubmit() {
			return after(DialogAfterAction.WAIT_FOR_RESPONSE);
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

		public NoticeDialogBuilder notice() {
			return new NoticeDialogBuilder(build());
		}

		private @NotNull DialogBase build() {
			if (title == null)
				title = new JsonBuilder("&c&lMISSING TITLE").build();

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
	public static class ConfirmationDialogBuilder extends DialogOpener {
		private final DialogBase base;
		private ActionButton onCancel;
		private ActionButton onSubmit;
		private Component cancelText = text("Cancel");
		private Component submitText = text("Submit");

		public void open(Player player) {
			player.showDialog(Dialog.create(builder -> {
				builder.empty().base(base).type(DialogType.confirmation(this.onCancel, this.onSubmit));
			}));
		}

		public ConfirmationDialogBuilder onCancel(Consumer<Player> onCancel) {
			return this.onCancel(
				ActionButton.builder(cancelText)
					.action(customClick((response, audience) -> {
						onCancel.accept((Player) audience);
					}, Options.builder().uses(UNLIMITED_USES).build()))
					.build()
			);
		}

		public ConfirmationDialogBuilder onCancel(ActionButton actionButton) {
			this.onCancel = actionButton;
			return this;
		}

		public ConfirmationDialogBuilder onSubmit(DialogResponseCallback onSubmit) {
			return this.onSubmit(
				ActionButton.builder(submitText)
					.action(customClick((response, audience) -> {
						onSubmit.accept(new DialogResponse(response, audience));
					}, Options.builder().uses(UNLIMITED_USES).build()))
					.build()
			);
		}

		public ConfirmationDialogBuilder onSubmit(ActionButton actionButton) {
			this.onSubmit = actionButton;
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
	public static class MultiActionDialogBuilder extends DialogOpener {
		private final DialogBase base;
		@Getter
		private final List<ActionButton> actions = new ArrayList<>();
		private ActionButton exitButton;
		private int columns = 2;
		private int defaultButtonWidth = 150;

		public void open(Player player) {
			player.showDialog(Dialog.create(builder -> {
				builder.empty().base(base).type(DialogType.multiAction(actions, exitButton, columns));
			}));
		}

		public MultiActionDialogBuilder defaultButtonWidth(int defaultButtonWidth) {
			this.defaultButtonWidth = defaultButtonWidth;
			return this;
		}

		public MultiActionDialogBuilder button(String label) {
			return this.button(label, defaultButtonWidth, response -> {});
		}

		public MultiActionDialogBuilder button(String label, int width) {
			return this.button(label, width, response -> {});
		}

		public MultiActionDialogBuilder button(String label, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(), defaultButtonWidth, action);
		}

		public MultiActionDialogBuilder button(String label, int width, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(), width, action);
		}

		public MultiActionDialogBuilder button(String label, String tooltip) {
			return this.button(new JsonBuilder(label), new JsonBuilder(tooltip), defaultButtonWidth, response -> {});
		}

		public MultiActionDialogBuilder button(String label, String tooltip, int width) {
			return this.button(new JsonBuilder(label), new JsonBuilder(tooltip), width, response -> {});
		}

		public MultiActionDialogBuilder button(String label, String tooltip, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(tooltip), defaultButtonWidth, action);
		}

		public MultiActionDialogBuilder button(String label, String tooltip, int width, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(tooltip), width, action);
		}

		public MultiActionDialogBuilder button(String label, JsonBuilder tooltip, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), tooltip, defaultButtonWidth, action);
		}

		public MultiActionDialogBuilder button(String label, JsonBuilder tooltip, int width, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), tooltip, width, action);
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
			return exitButton(
				ActionButton.builder(label)
					.tooltip(tooltip)
					.width(width)
					.action(customClick((response, audience) -> {
						DialogResponse dialogResponse = new DialogResponse(response, audience);
						action.accept(dialogResponse);
						dialogResponse.closeDialog();
					}, Options.builder().uses(UNLIMITED_USES).build()))
					.build()
			);
		}

		public MultiActionDialogBuilder exitButton(ActionButton exitActionButton) {
			this.exitButton = exitActionButton;
			return this;
		}

		public MultiActionDialogBuilder columns(int columns) {
			this.columns = columns;
			return this;
		}
	}

	@RequiredArgsConstructor
	public static class NoticeDialogBuilder extends DialogOpener {
		private final DialogBase base;
		private ActionButton button;

		public void open(Player player) {
			player.showDialog(Dialog.create(builder -> {
				builder.empty().base(base).type(DialogType.notice(button));
			}));
		}

		public NoticeDialogBuilder button(String label) {
			return this.button(label, response -> {});
		}

		public NoticeDialogBuilder button(String label, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(), 150, action);
		}

		public NoticeDialogBuilder button(String label, String tooltip, int width, DialogResponseCallback action) {
			return this.button(new JsonBuilder(label), new JsonBuilder(tooltip), width, action);
		}

		public NoticeDialogBuilder button(JsonBuilder label, JsonBuilder tooltip, int width, DialogResponseCallback action) {
			return this.button(label.build(), tooltip.build(), width, action);
		}

		public NoticeDialogBuilder button(Component label, Component tooltip, int width, DialogResponseCallback action) {
			return button(
				ActionButton.builder(label)
					.tooltip(tooltip)
					.width(width)
					.action(customClick((response, audience) -> {
						DialogResponse dialogResponse = new DialogResponse(response, audience);
						action.accept(dialogResponse);
						dialogResponse.closeDialog();
					}, Options.builder().uses(UNLIMITED_USES).build()))
					.build()
			);
		}

		public NoticeDialogBuilder button(ActionButton actionButton) {
			this.button = actionButton;
			return this;
		}
	}

	public static abstract class DialogOpener {
		public void open(OptionalPlayerLike hasPlayer) {
			Player player = hasPlayer.getPlayer();
			if (player == null || !player.isOnline())
				throw new PlayerNotOnlineException(player);
			open(player);
		}

		public abstract void open(Player player);
	}

	@Data
	@RequiredArgsConstructor
	public static class DialogResponse {
		private final Player player;
		private final DialogResponseView response;

		public DialogResponse(DialogResponseView response, Audience audience) {
			this.response = response;
			if (audience instanceof Player _player)
				this.player = _player;
			else
				this.player = null;
		}

		public String payload() {
			return this.response.payload().string();
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

}
