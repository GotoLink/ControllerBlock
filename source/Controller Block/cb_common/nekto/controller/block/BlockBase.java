package nekto.controller.block;

import java.util.Iterator;

import nekto.controller.item.ItemBase;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockBase extends Block {
	public static int renderID;

	protected BlockBase() {
		super(Material.rock);
        setCreativeTab(CreativeTabs.tabRedstone);
        setLightOpacity(1);
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
        return getBlockPowerInput(world, x, y, z);
	}

    private int getBlockPowerInput(IBlockAccess world, int x, int y, int z)
    {
        int l = Math.max(0, world.isBlockProvidingPowerTo(x, y - 1, z, 0));
        if (l >= 15){
            return l;
        }else{
            l = Math.max(l, world.isBlockProvidingPowerTo(x, y + 1, z, 1));

            if (l >= 15){
                return l;
            }else{
                l = Math.max(l, world.isBlockProvidingPowerTo(x, y, z - 1, 2));

                if (l >= 15){
                    return l;
                }else{
                    l = Math.max(l, world.isBlockProvidingPowerTo(x, y, z + 1, 3));

                    if (l >= 15){
                        return l;
                    }else{
                        l = Math.max(l, world.isBlockProvidingPowerTo(x - 1, y, z, 4));

                        if (l >= 15){
                            return l;
                        }else{
                            return Math.max(l, world.isBlockProvidingPowerTo(x + 1, y, z, 5));
                        }
                    }
                }
            }
        }
    }

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return (side >= 0 && side <= 3);
	}

	@Override
	public void breakBlock(World world, int par2, int par3, int par4, Block par5, int par6) {
		TileEntityBase<?> tile = (TileEntityBase<?>) world.getTileEntity(par2, par3, par4);
        if(tile!=null) {
            if (!world.isRemote && tile.isPowered() && !tile.isEditing())//We only spawn items if it is powered and not in editing mode
            {
                Iterator<?> itr = tile.getBaseList().iterator();
                dropItems(world, tile, itr, par2, par3, par4);
            }
            ItemStack link = tile.getLinker();
            if (link != null && link.hasTagCompound())
                link.getTagCompound().removeTag(ItemBase.KEYTAG);
            tile.setLinker(null);
        }
		super.breakBlock(world, par2, par3, par4, par5, par6);
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
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour){
        world.markBlockForUpdate(x, y, z);
    }
}
