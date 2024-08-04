package org.kybe;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Maze insta kill 
 *
 * @author kybe236
 */
public class Main extends Plugin {
	
	@Override
	public void onLoad() {
		
		//logger
		this.getLogger().info("[KYBES-MACE-KILLER] loaded");
		
		//creating and registering a new module
		final MaceKiller macekiller = new MaceKiller();
		RusherHackAPI.getModuleManager().registerFeature(macekiller);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("[KYBES-MACE-KILLER] unloaded");
	}
	
}