package nekto.controller.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import nekto.controller.animator.Mode;
import nekto.controller.core.Controller;
import nekto.controller.item.ItemBase;
import nekto.controller.ref.GeneralRef;
import nekto.controller.tile.TileEntityAnimator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class PacketHandler {
	public static void handleRemoteData(EntityPlayer player, TileEntityAnimator animator, int... data) {
		switch (data[0]) {
		case 0://Frame selection
			animator.setFrame(animator.getFrame() + 1);
			break;
		case 1://Multiple block selection switch
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null && stack.getItem() instanceof ItemBase) {
				ItemBase.setCornerMode(stack, !ItemBase.isInCornerMode(stack));
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
					if (player.worldObj.getTileEntity(data[4], data[5], data[6]) instanceof TileEntityAnimator) {
						animator = (TileEntityAnimator) player.worldObj.getTileEntity(data[4], data[5], data[6]);
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
            if (stack.hasTagCompound()) {
                stack.getTagCompound().removeTag(ItemBase.KEYTAG);
            }
        }
    }

    public static void sendDescription(TileEntityAnimator animator, World world) {
        Controller.animatorDesc.sendToAllAround(new DescriptionPacket(animator), new NetworkRegistry.TargetPoint(world.provider.dimensionId, animator.xCoord, animator.yCoord, animator.zCoord, 50));
    }

    public static void sendGuiChange(GuiChangePacket packet){
        Controller.guiChange.sendToServer(packet);
    }
}
