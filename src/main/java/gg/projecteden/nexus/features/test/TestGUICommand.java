package gg.projecteden.nexus.features.test;

import gg.projecteden.nexus.features.menus.api.content.ScrollableInventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@HideFromWiki
@Permission(Group.ADMIN)
@NoArgsConstructor
public class TestGUICommand extends CustomCommand {

	public TestGUICommand(@NonNull CommandEvent event) {
		super(event);
	}

	private static class ScrollMenu extends ScrollableInventoryProvider {
		private final int pages;

		private ScrollMenu(int pages) {
			this.pages = pages;
		}

		@Override
		public int getPages() {
			return pages;
		}
	}

	@Path("scroll <pages>")
	void scroll(@Arg(min = 1, max = 10) int pages) {
		new ScrollMenu(pages).open(player());
	}


}
