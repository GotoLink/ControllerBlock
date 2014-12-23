package nekto.controller.network;

import cpw.mods.fml.common.network.IGuiHandler;
import nekto.controller.container.ContainerAnimator;
import nekto.controller.ref.GeneralRef;
import nekto.controller.tile.TileEntityAnimator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {
    public void registerRenderThings() {
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityAnimator)
            if (ID == GeneralRef.GUI_ID) {
                return new ContainerAnimator(player.inventory, (TileEntityAnimator) tile, true);
            } else if (ID == GeneralRef.REMOTE_GUI_ID) {
                return new ContainerAnimator(player.inventory, (TileEntityAnimator) tile, false);
            }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public World getClientWorld() {
        return null;
    }
}
