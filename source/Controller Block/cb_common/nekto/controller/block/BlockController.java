/*
 *  Author: Sam6982
 */
package nekto.controller.block;

import java.util.Iterator;

import nekto.controller.tile.TileEntityBase;
import nekto.controller.tile.TileEntityController;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockController extends BlockBase {
	public BlockController() {
		super();
        func_149663_c("controller");
	}

	@Override
	public TileEntity func_149915_a(World world, int i) {
		return new TileEntityController();
	}

	@Override
	public void onRedstoneChange(World par1World, int par2, int par3, int par4, Block par5, boolean powered, TileEntityBase<?> tile) {
		if (powered) {
			setActiveBlocks(par1World, (Iterator<Object[]>) tile.getBaseList().iterator());
		} else {
			setUnactiveBlocks(par1World, (Iterator<Object[]>) tile.getBaseList().iterator());
		}
	}
}
