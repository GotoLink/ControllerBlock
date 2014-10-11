/*
 *  Author: Sam6982
 */
package nekto.controller.block;

import nekto.controller.tile.TileEntityController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockController extends BlockBase {
	public BlockController() {
		super();
        setBlockName("controller");
	}

	@Override
	public TileEntity createTileEntity(World world, int i) {
		return new TileEntityController();
	}
}
