package nekto.controller.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import nekto.controller.block.BlockBase;
import nekto.controller.gui.AnimatorGUI;
import nekto.controller.network.CommonProxy;
import nekto.controller.ref.GeneralRef;
import nekto.controller.render.ControllerRenderer;
import nekto.controller.render.TileEntityAnimatorRenderer;
import nekto.controller.tile.TileEntityAnimator;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class ClientProxy extends CommonProxy {
    @Override
    public void registerRenderThings() {
        BlockBase.renderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(BlockBase.renderID, new ControllerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBase.class, new TileEntityAnimatorRenderer());
        FMLCommonHandler.instance().bus().register(new RemoteKeyHandler());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityAnimator)
            if (ID == GeneralRef.GUI_ID) {
                return new AnimatorGUI(player.inventory, (TileEntityAnimator) tile, false);
            } else if (ID == GeneralRef.REMOTE_GUI_ID) {
                return new AnimatorGUI(player.inventory, (TileEntityAnimator) tile, true);
            }
        return null;
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getWorldClient();
    }
}
