package me.pugabyte.bncore.features.holidays.halloween20;

import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Gate {

    private static String gateBlockData = "minecraft:spruce_fence[east=true,north=false,south=false,waterlogged=false,west=true]";

    private static Location closedL1 = new Location(Halloween20.getWorld(), 303, 59, -1992);
    private static Location closedL2 = new Location(Halloween20.getWorld(), 307, 63, -1992);

    private static Location openL1 = new Location(Halloween20.getWorld(), 298, 59, -1992);
    private static Location openL2 = new Location(Halloween20.getWorld(), 302, 63, -1992);

    private static void openGate(Player player) {openGate(player, closedL1, closedL2, openL1, openL2);}
    private static void openGate(Player player, Location c1, Location c2, Location o1, Location o2){ openGate(player, c1, c2, o1, o2, gateBlockData); }
    private static void openGate(Player player, Location c1, Location c2, Location o1, Location o2, String gateMaterial){

        if(c1.getX()>c2.getX()||c1.getY()>c2.getY()||c1.getZ()>c2.getZ()){
            Bukkit.getLogger().severe("Error in gate open function: pos1 cannot be greater than pos2");
            return;
        } else if(o1.getX()>o2.getX()||o1.getY()>o2.getY()||o1.getZ()>o2.getZ()){
            Bukkit.getLogger().severe("Error in gate open function: pos1 cannot be greater than pos2");
            return;
        }

        Tasks.repeat(0, 20, new BukkitRunnable() {

            int x_cursor_c = 0; //x cursor for closed portion.
            int x_cursor_o = 0; //x cursor for open portion.

            boolean closedPortionDone = false;
            boolean openPortionDone = false;

            @Override
            public void run() {

                player.sendMessage("x cursor: "+ x_cursor_c);

                for(int z = c1.getBlockZ(); z < c2.getBlockZ()+1; z++){
                    for(int y = c1.getBlockY(); y < c2.getBlockY()+1; y++){
                        Location targetLocation = new Location(Halloween20.getWorld(),c2.getX()-x_cursor_c, y, z);
                        player.sendBlockChange(targetLocation, Material.AIR.createBlockData());
                        if(targetLocation.equals(c1)){
                            closedPortionDone = true;
                        }
                    }
                }

                for(int z = o1.getBlockZ(); z < o2.getBlockZ()+1; z++){
                    for(int y = o1.getBlockY(); y < o2.getBlockY()+1; y++){
                        Location targetLocation = new Location(Halloween20.getWorld(),o2.getX()-x_cursor_o, y, z);
                        player.sendBlockChange(targetLocation, Bukkit.createBlockData(gateMaterial));
                        if(targetLocation.equals(o1)){
                            openPortionDone = true;
                        }
                    }
                }

                x_cursor_c += ((c1.getBlockX() + x_cursor_c) < c2.getBlockX()) ? 1 : 0;
                x_cursor_o += ((o1.getBlockX() + x_cursor_o) < o2.getBlockX()) ? 1 : 0;
                if(closedPortionDone==true&&openPortionDone) this.cancel();

            }
        });

    }

    private static void closeGate(Player player) {closeGate(player, closedL1, closedL2, openL1, openL2);}
    private static void closeGate(Player player, Location c1, Location c2, Location o1, Location o2){

        if(c1.getX()>c2.getX()||c1.getY()>c2.getY()||c1.getZ()>c2.getZ()){
            Bukkit.getLogger().severe("Error in gate close function: pos1 cannot be greater than pos2");
            return;
        } else if(o1.getX()>o2.getX()||o1.getY()>o2.getY()||o1.getZ()>o2.getZ()){
            Bukkit.getLogger().severe("Error in gate close function: pos1 cannot be greater than pos2");
            return;
        }

        Tasks.repeat(0, 20, new BukkitRunnable(){

            int x_cursor_c = 0; //x cursor for closed portion.
            int x_cursor_o = 0; //x cursor for open portion.

            boolean closedPortionDone = false; //Has animation finished for the closed portion?
            boolean openPortionDone = false; //Has animation finished for the open portion?

            @Override
            public void run() {

                for(int z = c1.getBlockZ(); z < c2.getBlockZ()+1; z++){
                    for(int y = c1.getBlockY(); y < c2.getBlockY()+1; y++){
                        Location targetLocation = new Location(Halloween20.getWorld(),c1.getX()+x_cursor_c, y, z);
                        player.sendBlockChange(targetLocation, targetLocation.getWorld().getBlockAt(targetLocation).getBlockData());
                        if(targetLocation.equals(c2)){
                            closedPortionDone = true;
                        }
                    }
                }

                for(int z = o1.getBlockZ(); z < o2.getBlockZ()+1; z++){
                    for(int y = o1.getBlockY(); y < o2.getBlockY()+1; y++){
                        Location targetLocation = new Location(Halloween20.getWorld(),o1.getX()+x_cursor_o, y, z);
                        player.sendBlockChange(targetLocation, targetLocation.getWorld().getBlockAt(targetLocation).getBlockData());
                        if(targetLocation.equals(o2)){
                            openPortionDone = true;
                        }
                    }
                }

                x_cursor_c += ((c1.getBlockX() + x_cursor_c) < c2.getBlockX()) ? 1 : 0;
                x_cursor_o += ((o1.getBlockX() + x_cursor_o) < o2.getBlockX()) ? 1 : 0;
                if(closedPortionDone==true&&openPortionDone) this.cancel();

            }
        });

    }

}
