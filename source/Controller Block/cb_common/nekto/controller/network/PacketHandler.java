package nekto.controller.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import nekto.controller.animator.Mode;
import nekto.controller.core.Controller;
import nekto.controller.item.ItemBase;
import nekto.controller.ref.GeneralRef;
import nekto.controller.tile.TileEntityAnimator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PacketHandler {
	public static void handleRemoteData(EntityPlayer player, TileEntityAnimator animator, int... data) {
		switch (data[0]) {
		case 0://Frame selection
			animator.setFrame(animator.getFrame() + 1);
			break;
		case 1://Multiple block selection switch
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null && stack.getItem() instanceof ItemBase) {
				ItemBase item = (ItemBase) stack.getItem();
				item.setCornerMode(!item.isInCornerMode());
			}
			break;
		case 2://Reset button
			animator.setEditing(false);
			animator.setLinker(null);
			resetAnimator(animator);
			resetRemote(player.getCurrentEquippedItem());
			break;
		default:
			break;
		}
	}

	public static void handleBlockData(EntityPlayer player, TileEntityAnimator animator, int... data) {
		switch (data[0]) {
		case 0://"+" button has been pressed
			animator.setDelay(1);
			break;
		case 1://"-" button has been pressed
			if (animator.getDelay() > -1) {//Lower delay won't work and might crash
				animator.setDelay(-1);
			}
			break;
		case 2://"Switch button has been pressed, going LOOP->ORDER->REVERSE->RANDOM->LOOP
			int mod = animator.getMode().ordinal();
			if (mod + 1 < Mode.values().length)
				animator.setMode(Mode.values()[mod + 1]);
			else
				animator.setMode(Mode.LOOP);
			break;
		case 3:
		case 4://One of the "Reset" button has been pressed
			animator.setEditing(false);
			animator.setLinker(null);
			if (data[0] == 4)//This is a full reset
			{
				if (data.length > 6)
					if (player.worldObj.func_147438_o(data[4], data[5], data[6]) instanceof TileEntityAnimator) {
						animator = (TileEntityAnimator) player.worldObj.func_147438_o(data[4], data[5], data[6]);
					}
				resetAnimator(animator);
			}
			if (data.length > 6)//Get the item and reset it
				resetRemote(animator.getStackInSlot(0));
			break;
		case 5://Increment Max number of frames that will run
			animator.setMaxFrame(animator.getMaxFrame() + 1);
			break;
		case 6://Increment first frame to display
			animator.setFrame(animator.getFrame() + 1);
			break;
		default:
			break;
		}
	}

	public static void resetAnimator(TileEntityAnimator animator) {
		animator.setFrame(0);
		animator.setMode(Mode.ORDER);
		animator.resetDelay();
		animator.setMaxFrame(-1);
		animator.setCount(0);
	}

    public static void resetRemote(ItemStack stack) {
        if (stack.getItem() instanceof ItemBase) {
            ItemBase remote = (ItemBase) stack.getItem();
            remote.resetLinker();
            if (stack.hasTagCompound()) {
                stack.getTagCompound().removeTag(ItemBase.KEYTAG);
            }
        }
    }

    public static void sendDescription(TileEntityAnimator animator, World world) {
        ByteBuf buf = Unpooled.buffer();
        DescriptionPacket desc = new DescriptionPacket(animator);
        desc.toBytes(buf);
        FMLProxyPacket packet = new FMLProxyPacket(buf, GeneralRef.DESC_CHANNEL);
        packet.setTarget(Side.CLIENT);
        Controller.animatorDesc.sendToAllAround(packet, new NetworkRegistry.TargetPoint(world.provider.dimensionId, desc.data[0], desc.data[1], desc.data[2], 50));
    }

    public static void sendGuiChange(GuiChangePacket packet){
        ByteBuf buf = Unpooled.buffer();
        packet.toBytes(buf);
        FMLProxyPacket message = new FMLProxyPacket(buf, GeneralRef.GUI_CHANNEL);
        message.setTarget(Side.SERVER);
        Controller.guiChange.sendToServer(message);
    }
}
