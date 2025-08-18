package gg.projecteden.nexus.features.trust;

import com.griefcraft.model.Permission;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCAccessEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.trust.TrustsUser.TrustType;
import gg.projecteden.nexus.models.trust.TrustsUserService;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
public class LWCTrustModule extends JavaModule {

	@Override
	public void onAccessRequest(LWCAccessEvent event) {
		UUID owner;
		try {
			owner = UUID.fromString(event.getProtection().getOwner());
		} catch (IllegalArgumentException e) {
			return;
		}

		UUID requester = event.getPlayer().getUniqueId();
		Set<UUID> trusted = new TrustsUserService().get(owner).get(TrustType.LOCKS);

		if (trusted.contains(requester))
			event.setAccess(Permission.Access.PLAYER);

		if (Rank.of(requester).isStaff() && WorldGroup.of(event.getProtection().getBukkitWorld()) == WorldGroup.STAFF)
			event.setAccess(Permission.Access.PLAYER);
	}

}
