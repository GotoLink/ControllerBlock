package nekto.controller.block;

import java.util.Iterator;

import nekto.controller.item.ItemBase;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockBase extends Block {
	public static int renderID;

	protected BlockBase() {
		super(Material.rock);
        setCreativeTab(CreativeTabs.tabBlock);
	}

    @Override
    public boolean hasTileEntity(int meta){
        return true;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return renderID;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (world instanceof World) {
			if (((World) world).isBlockIndirectlyGettingPowered(x, y, z)) {
				return 15;
			} else
				return 8;
		} else
			return super.getLightValue(world, x, y, z);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return (side >= 0 && side <= 3);
	}

	@Override
	public void breakBlock(World world, int par2, int par3, int par4, Block par5, int par6) {
		TileEntityBase<?> tile = (TileEntityBase<?>) world.getTileEntity(par2, par3, par4);
		if (!world.isRemote && tile.isPowered() && !tile.isEditing())//We only spawn items if it is powered and not in editing mode
		{
			Iterator<?> itr = tile.getBaseList().iterator();
			dropItems(world, tile, itr, par2, par3, par4);
		}
		ItemBase linker = tile.getLinker();
		if (linker != null) {
			linker.resetLinker();
		}
		super.breakBlock(world, par2, par3, par4, par5, par6);
	}

	protected void setUnactiveBlocks(World par1World, Iterator<Object[]> itr) {
		while (itr.hasNext()) {
			Object[] block = itr.next();
			if (block != null && block.length > 4 && par1World.getBlock((Integer) block[1], (Integer) block[2], (Integer) block[3]) != block[0]) {
				par1World.setBlock((Integer) block[1], (Integer) block[2], (Integer) block[3], (Block) block[0], (Integer) block[4], 3);
			}
		}
	}

	protected void setActiveBlocks(World par1World, Iterator<Object[]> itr) {
		while (itr.hasNext()) {
			Object[] block = itr.next();
			if (block != null && block.length > 4) {
				if (par1World.getBlock((Integer) block[1], (Integer) block[2], (Integer) block[3]) != block[0])
					itr.remove();
				else
					par1World.setBlockToAir((Integer) block[1], (Integer) block[2], (Integer) block[3]);
			}
		}
	}

	protected void dropItems(World world, TileEntityBase<?> tile, Iterator<?> itr, int par2, int par3, int par4) {
		float f = world.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
		while (itr.hasNext()) {
			Object[] elem = (Object[]) itr.next();
			EntityItem item = new EntityItem(world, par2 + f, par3 + f1, par4 + f2, new ItemStack((Block)elem[0], 1, (Integer) elem[4]));
			world.spawnEntityInWorld(item);
		}
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
		TileEntityBase<?> tile = (TileEntityBase<?>) par1World.getTileEntity(par2, par3, par4);
		boolean flag = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
		if (tile.previousState != flag) {
			if (tile.getBaseList().size() > 0)
				onRedstoneChange(par1World, par2, par3, par4, par5, flag, tile);
			tile.setState(flag);
		}
	}

	protected abstract void onRedstoneChange(World par1World, int par2, int par3, int par4, Block par5, boolean powered, TileEntityBase<?> tile);
}
