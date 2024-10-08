package mod.imphack.command.commands;

import mod.imphack.Client;
import mod.imphack.command.Command;
import net.minecraft.client.Minecraft;

public class Rotate extends Command {

	@Override
	public String getAlias() {
		return "rotate";
	}

	@Override
	public String getDescription() {
		return "Sets the player rotation";
	}

	@Override
	public String getSyntax() {
		return ".rotate [pitch] [yaw]";
	}

	@Override
	public void onCommand(String command, String[] args) {
		if (args[0].isEmpty() || args[1].isEmpty()) {
			Client.addChatMessage("No arguments found");
			Client.addChatMessage(this.getSyntax());
		} else {
			Minecraft.getMinecraft().player.rotationPitch = Float.parseFloat(args[0]);
			Minecraft.getMinecraft().player.rotationYaw = Float.parseFloat(args[1]);
			Minecraft.getMinecraft().player.rotationYawHead = Float.parseFloat(args[1]);
			Client.addChatMessage("Rotated player");
		}

	}

}
