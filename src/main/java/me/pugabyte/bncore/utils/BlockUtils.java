package me.pugabyte.bncore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Iterator;

public class BlockUtils {

	public static void updateBlockProperty(Block block, String key, String newValue){
		block.setBlockData(getBlockDataWithNewValue(block, key, newValue));
	}

	public static String getBlockProperty(Block block, String key){
		HashMap<String, String> properties  = getBlockProperties(block);
		return properties.containsKey(key) ? properties.get(key) : null;
	}

	public static boolean containsBlockProperty(Block block, String key){
		return getBlockProperty(block, key) !=null;
	}

	public static HashMap<String, String> getBlockProperties(Block block){
		return getBlockProperties(block.getState().getBlockData().getAsString());
	}

	public static HashMap<String, String> getBlockProperties(BlockData blockData){
		return getBlockProperties(blockData.getAsString());
	}

	public static HashMap<String, String> getBlockProperties(String blockDataString){
		HashMap<String, String> blockDataVariables = new HashMap<>();
		String[] variables = blockDataString.replace("]", "").split("\\[");
		String[] variableList = variables.length > 1 ? variables[1].split(",") : null;
		if(variableList!=null) for(String s:variableList) blockDataVariables.put(s.split("=")[0], s.split("=")[1]);
		return blockDataVariables;
	}

	public static BlockData getBlockDataWithNewValue(Block block, String key, String newValue){
		HashMap<String, String> variables = getBlockProperties(block);
		if(variables.containsKey(key.toLowerCase()))variables.put(key, newValue.toLowerCase());
		return getBlockDataFromList(block, variables);
	}

	public static BlockData getBlockDataFromList(Block block, HashMap<String, String> variables){
		return getBlockDataFromList(block.getType(), variables);
	}

	public static BlockData getBlockDataFromList(Material material, HashMap<String, String> variables){
		if(material==null) return null;
		if(variables!=null&&!variables.isEmpty()){
			return Bukkit.createBlockData(generateBlockDataString(material, variables));
		} else {
			return Bukkit.createBlockData(material);
		}

	}

	public static String generateBlockDataString(Material material, HashMap<String, String> values){
		if(values!=null&&!values.isEmpty()){
			StringBuilder vsb = new StringBuilder();
			Iterator i = values.keySet().iterator();
			while(i.hasNext()){
				String v = (String)i.next();
				vsb.append(String.format("%s=%s", v, i.hasNext() ? values.get(v) + "," : values.get(v)));
			}
			return String.format("minecraft:%s[%s]", material.toString().toLowerCase(), vsb.toString());
		} else {
			return String.format("minecraft:%s",material.toString()).toLowerCase();
		}
	}

}
