package org.kybe;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Example rusherhack plugin
 *
 * @author John200410
 */
public class Main extends Plugin {
	
	@Override
	public void onLoad() {
		
		//logger
		this.getLogger().info("[KYBES-MAZE-KILLER] loaded");
		
		//creating and registering a new module
		final MazeKiller mazekiller = new MazeKiller();
		RusherHackAPI.getModuleManager().registerFeature(mazekiller);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("[KYBES-MAZE-KILLER] unloaded");
	}
	
}