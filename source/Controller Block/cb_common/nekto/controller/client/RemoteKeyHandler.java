package nekto.controller.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import nekto.controller.item.ItemBase;
import nekto.controller.item.ItemRemote;
import nekto.controller.network.GuiChangePacket;
import nekto.controller.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

public class RemoteKeyHandler {
	public static final KeyBinding keyBind = new KeyBinding("remote.control.key", Keyboard.KEY_R, "key.categories.item.special");

    public RemoteKeyHandler(){
        ClientRegistry.registerKeyBinding(keyBind);
    }

    @SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) {
		if (Keyboard.getEventKey() == keyBind.getKeyCode()) {
			if (Minecraft.getMinecraft().currentScreen == null) {
				EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
				if (player != null) {
					ItemStack stack = player.inventory.getCurrentItem();
					if (stack != null && stack.getItem() instanceof ItemRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey(ItemBase.KEYTAG)) {
						int[] data = stack.getTagCompound().getIntArray(ItemBase.KEYTAG);
						PacketHandler.sendGuiChange(new GuiChangePacket(true, -1, data[0], data[1], data[2]));
					}
				}
			}
		}
	}
}
