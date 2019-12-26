package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public final class DeathSwap extends TeamlessMechanic {
    @Override
    public String getName() {
        return "Death Swap";
    }

    @Override
    public String getDescription() {
        return "Trap players by swapping with them!";
    }

    @Override
    public void onStart(Match match){
        randomTeleport(match.getMinigamers());
        for(Minigamer minigamer : match.getMinigamers()){
            for(Minigamer toHide : match.getMinigamers()) {
                if(minigamer != toHide) {
                    minigamer.getPlayer().hidePlayer(BNCore.getInstance(), toHide.getPlayer());
                }
            }
        }
        Utils.wait(60 * 20, () -> delay(match));
    }

    private void randomTeleport(List<Minigamer> minigamers) {
        if(minigamers.get(0).getTeam().getSpawnpoints().size() < 2){
            minigamers.get(0).getMatch().broadcast("&cArena not properly setup. There must be two spawnpoints.");
            minigamers.get(0).getMatch().end();
            return;
        }
        Location one = minigamers.get(0).getTeam().getSpawnpoints().get(0);
        Location two = minigamers.get(0).getTeam().getSpawnpoints().get(1);
        for(Minigamer minigamer : minigamers){
            minigamer.teleport((minigamer.getPlayer().getWorld().getHighestBlockAt(
                    randomInt(one.getBlockX(), two.getBlockX()),
                    randomInt(two.getBlockZ(), two.getBlockZ())
            )).getLocation().add(0, 2, 0));
        }
    }

    @Override
    public void kill(Minigamer minigamer){
        minigamer.getMatch().broadcast("&c" + minigamer.getPlayer().getDisplayName() + " has died! ("
                + (minigamer.getMatch().getMinigamers().size() - 1)
                + minigamer.getMatch().getArena().getMaxPlayers() + ")");
        for(Minigamer player : minigamer.getMatch().getMinigamers()){
            player.getPlayer().showPlayer(minigamer.getPlayer());
            minigamer.getPlayer().showPlayer(player.getPlayer());
        }
        if(minigamer.getMatch().getMinigamers().size() > 2){
            minigamer.quit();
            return;
        }
        for(Minigamer player : minigamer.getMatch().getMinigamers()){
            if(player != minigamer){
                player.scored();
                break;
            }
        }
        minigamer.quit();
    }

    public void swap(Match match){
        match.broadcast("&aSwapping!");
        ArrayList<Minigamer> swappingList = new ArrayList<>(match.getMinigamers());
        if(match.getMinigamers().size() % 2 != 0){
            Location one = swappingList.get(0).getPlayer().getLocation();
            Location two = swappingList.get(1).getPlayer().getLocation();
            Location three = swappingList.get(2).getPlayer().getLocation();
            swappingList.get(0).teleport(three);
            swappingList.get(1).teleport(one);
            swappingList.get(2).teleport(two);
            swappingList.remove(0);
            swappingList.remove(0);
            swappingList.remove(0);
        }
        while(swappingList.size() > 0){
            Location one = swappingList.get(0).getPlayer().getLocation();
            Location two = swappingList.get(1).getPlayer().getLocation();
            swappingList.get(0).teleport(two);
            swappingList.get(1).teleport(one);
            swappingList.remove(0);
            swappingList.remove(0);
        }
        delay(match);
    }

    public void delay(Match match){
        if(!(match.getMinigamers().size() > 1)){
            match.end();
            return;
        }
        int delayTime = randomInt(0, 120);
        Utils.wait(delayTime * 20, () -> swap(match));
    }

    public int randomInt(int min, int max){
        if(min >= max){
            int temp = max;
            max = min;
            min = temp;
        }
        return (int)((Math.random() * ((max - min) + 1)) + min) * 20;
    }

}
