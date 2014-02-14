package nekto.controller.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import nekto.controller.container.ContainerAnimator;
import nekto.controller.core.Controller;
import nekto.controller.ref.GeneralRef;
import nekto.controller.tile.TileEntityAnimator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;

public class GuiChangeHandler {

    @SubscribeEvent
    public void onServerMessage(FMLNetworkEvent.ServerCustomPacketEvent event) {
        GuiChangePacket packet = new GuiChangePacket();
        packet.fromBytes(event.packet.payload());
        if(packet.data.length>=4){
            handleGuiChange(packet, ((NetHandlerPlayServer) event.handler).field_147369_b);
        }
    }

    /**
     * Server method to handle a client action in AnimatorGUI or RemoteKeyHandler
     */
    private static void handleGuiChange(GuiChangePacket packet, EntityPlayer player) {
        TileEntity tile = player.worldObj.func_147438_o(packet.data[1], packet.data[2], packet.data[3]);
        if (tile instanceof TileEntityAnimator) {
            if (packet.data[0] >= 0)//From AnimatorGUI
            {
                if (!packet.remote) {
                    PacketHandler.handleBlockData(player, (TileEntityAnimator) tile, packet.data);
                } else {
                    PacketHandler.handleRemoteData(player, (TileEntityAnimator) tile, packet.data);
                }
                if (player.openContainer instanceof ContainerAnimator && ((ContainerAnimator) player.openContainer).getControl() == tile)
                    player.openContainer.detectAndSendChanges();
                PacketHandler.sendDescription((TileEntityAnimator)tile, player.worldObj);
            } else//From RemoteKeyHandler
            {
                player.openGui(Controller.instance, GeneralRef.REMOTE_GUI_ID, player.worldObj, packet.data[1], packet.data[2], packet.data[3]);
            }
        }
    }
}
