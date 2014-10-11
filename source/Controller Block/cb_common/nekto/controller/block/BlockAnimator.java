package nekto.controller.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import nekto.controller.core.Controller;
import nekto.controller.item.ItemRemote;
import nekto.controller.ref.GeneralRef;
import nekto.controller.tile.TileEntityAnimator;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAnimator extends BlockBase {
	public BlockAnimator() {
		super();
        setBlockName("animator");
	}

	@Override
	public TileEntity createTileEntity(World world, int i) {
		return new TileEntityAnimator();
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		if (par5EntityPlayer.getCurrentEquippedItem() == null || !(par5EntityPlayer.getCurrentEquippedItem().getItem() instanceof ItemRemote)) {
			if (par1World.getBlockPowerInput(par2, par3, par4)==0)//We don't want to enable any changes when block is powered
			{
				if (par1World.getTileEntity(par2, par3, par4) instanceof TileEntityAnimator)
					par5EntityPlayer.openGui(Controller.instance, GeneralRef.GUI_ID, par1World, par2, par3, par4);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void dropItems(World world, TileEntityBase<?> tile, Iterator<?> itr, int par2, int par3, int par4) {
		List<Object[]> frames;
		int index = 0;
		while (itr.hasNext()) {
			frames = (List<Object[]>) itr.next();
			if (frames != null && index != ((TileEntityAnimator) tile).getFrame())
				super.dropItems(world, tile, frames.iterator(), par2, par3, par4);
			index++;
		}
	}
}
