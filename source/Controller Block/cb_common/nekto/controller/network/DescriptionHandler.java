package nekto.controller.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import nekto.controller.animator.Mode;
import nekto.controller.tile.TileEntityAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

public class DescriptionHandler {
    @SubscribeEvent
    public void onMessage(FMLNetworkEvent.ClientCustomPacketEvent event) {
        DescriptionPacket packet = new DescriptionPacket();
        packet.fromBytes(event.packet.payload());
        if(packet.data.length == 7){
            handleDescriptionPacket(packet);
        }
    }

    /**
     * Client method to handle a packet describing the TileEntityAnimator from
     * server
     *
     * @param packet
     */
    @SideOnly(Side.CLIENT)
    private void handleDescriptionPacket(DescriptionPacket packet) {
        int x = packet.data[0];
        int y = packet.data[1];
        int z = packet.data[2];
        TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
        if (te instanceof TileEntityAnimator) {
            TileEntityAnimator animator = (TileEntityAnimator) te;
            animator.setEditing(packet.edit);
            if (!animator.isEditing() && animator.getStackInSlot(0) != null)
                PacketHandler.resetRemote(animator.getStackInSlot(0));
            animator.setFrame(packet.data[3]);
            animator.setMaxFrame(packet.data[4]);
            animator.setCount(packet.data[5]);
            animator.resetDelay();
            animator.setDelay(packet.data[6]);
            animator.setMode(Mode.values()[packet.mode]);
        }
    }

}
