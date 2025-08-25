package gg.projecteden.nexus.features.dialog;

import gg.projecteden.nexus.framework.features.Feature;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.Builder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickCallback.Options;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static io.papermc.paper.registry.data.dialog.action.DialogAction.customClick;
import static io.papermc.paper.registry.data.dialog.body.DialogBody.plainMessage;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnstableApiUsage")
public class DialogFeature extends Feature {

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
