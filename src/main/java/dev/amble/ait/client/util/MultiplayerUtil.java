package dev.amble.ait.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

public class MultiplayerUtil {
	/**
	 * Tries to add a server to the server list if it doesn't already exist.
	 *
	 * @param name    the name of the server
	 * @param address the ip address of the server
	 */
	public static void tryAddServer(String name, String address) {
		MinecraftClient client = MinecraftClient.getInstance();
		ServerList serverList = new ServerList(client);
		serverList.loadFile();

		for (int i = 0; i < serverList.size(); i++) {
			ServerInfo server = serverList.get(i);
			if (server.address.equalsIgnoreCase(address)) {
				return;
			}
		}

		ServerInfo newServer = new ServerInfo(name, address, false);
		serverList.add(newServer, false);
		serverList.saveFile();
	}
}
