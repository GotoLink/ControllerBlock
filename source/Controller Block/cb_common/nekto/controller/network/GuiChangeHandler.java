package nekto.controller.network;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import nekto.controller.container.ContainerAnimator;
import nekto.controller.core.Controller;
import nekto.controller.ref.GeneralRef;
import nekto.controller.tile.TileEntityAnimator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public final class GuiChangeHandler implements IMessageHandler<GuiChangePacket, GuiChangePacket> {
    @Override
    public GuiChangePacket onMessage(GuiChangePacket packet, MessageContext context) {
        if (packet.data.length >= 4) {
            handleGuiChange(packet, context.getServerHandler().playerEntity);
        }
        return null;
    }

    /**
     * Server method to handle a client action in AnimatorGUI or RemoteKeyHandler
     */
    private void handleGuiChange(GuiChangePacket packet, EntityPlayer player) {
        TileEntity tile = player.worldObj.getTileEntity(packet.data[1], packet.data[2], packet.data[3]);
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
                PacketHandler.sendDescription((TileEntityAnimator) tile, player.worldObj);
            } else//From RemoteKeyHandler
            {
                player.openGui(Controller.instance, GeneralRef.REMOTE_GUI_ID, player.worldObj, packet.data[1], packet.data[2], packet.data[3]);
            }
        }
    }
}
