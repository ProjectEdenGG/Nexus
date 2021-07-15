package me.pugabyte.nexus.features.ambience.effects.sounds;

import me.pugabyte.nexus.models.ambience.AmbienceUser;


public class SoundEvents {
	public static void update(AmbienceUser user) {
		// TODO
	}

//	public void update(MAPlayer maplayer) {
//		if(conditionsMet(maplayer)) {
//			if(maplayer.updateCooldown(id) <= 0) {
//				for(Sound sound : sounds) {
//					maplayer.getSoundPlayer().playSound(sound, maplayer.getAccessor().getX(), maplayer.getAccessor().getY(), maplayer.getAccessor().getZ(), false);
//				}
//				setCooldown(maplayer);
//			}
//		} else if (Config.ambientEvents().isStopSounds() && maplayer.getCooldown(id) > 0 /*&& isRestricted(maplayer)(so it doesn't get cut of in so many cases?)*/) {
//			//TODO: needs fading in and out, sadly not possible with current protocol
//			//      for now disabled with config option to reenable, sound stopping without fadeout is just to abrupt
//			for(Sound sound : sounds) {
//				maplayer.getLogger().log("Stop sound "+sound.getName());
//				maplayer.getAccessor().stopSound(sound.getName());
//			}
//			maplayer.setCooldown(id, 0);
//		}
//	}
}
