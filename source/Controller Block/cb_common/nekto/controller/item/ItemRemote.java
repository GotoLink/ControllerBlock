/*
 *  Author: Sam6982
 */
package nekto.controller.item;

import java.util.List;

import nekto.controller.core.Controller;
import nekto.controller.tile.TileEntityAnimator;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ItemRemote extends ItemBase {
    private final String FRAME_EDIT = StatCollector.translateToLocal("remote.frame.edit");
    private final static String FRAME_FINISH = "remote.frame.finish";
	public ItemRemote() {
		super();
		setUnlocalizedName("remote");
	}

	@SuppressWarnings("unchecked")
    @Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(stack, par2EntityPlayer, par3List, par4);
		if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(KEYTAG)) {
			int data = stack.getTagCompound().getIntArray(KEYTAG)[3];
			par3List.add(FRAME_EDIT + (data + 1));
		}
	}

	@Override
	protected Class<? extends TileEntityBase<?>> getControl() {
		return TileEntityAnimator.class;
	}

	@Override
	protected String getControlName() {
		return Controller.animator.getLocalizedName();
	}

	@Override
	protected int[] getStartData(int par4, int par5, int par6) {
		return new int[] { par4, par5, par6, 0 };
	}

	@Override
	protected void onBlockSelected(ItemStack stack, EntityPlayer player, World world, Block id, int par4, int par5, int par6, int meta, TileEntityBase link) {
        ((TileEntityAnimator) link).setFrame(stack.getTagCompound().getIntArray(KEYTAG)[3]);
		super.onBlockSelected(stack, player, world, id, par4, par5, par6, meta, link);
	}

	@Override
	protected boolean onControlUsed(TileEntityBase<?> tempTile, EntityPlayer player, ItemStack stack, TileEntityBase link) {
		if (super.onControlUsed(tempTile, player, stack, link)) {
			return true;
		} else if (ItemStack.areItemStackTagsEqual(tempTile.getLinker(), stack)) {
            int frame = ((TileEntityAnimator) tempTile).getFrame() + 1;
			player.addChatComponentMessage(new ChatComponentTranslation(FRAME_FINISH, frame, frame + 1));
			setEditAndTag(new int[] { tempTile.xCoord, tempTile.yCoord, tempTile.zCoord, frame }, stack, tempTile);
            tempTile.setLinker(stack);
			return true;
		}
		return false;
	}

    @Override
    protected void onReplaceControl(ItemStack stack, TileEntityBase tempTile){
        int frame = ((TileEntityAnimator) tempTile).getFrame();
        setEditAndTag(new int[] { tempTile.xCoord, tempTile.yCoord, tempTile.zCoord, frame }, stack, tempTile);
    }

	@Override
	protected void setEditAndTag(int[] pos, ItemStack par1ItemStack, TileEntityBase link) {
		((TileEntityAnimator) link).setFrame(pos[3]);
		super.setEditAndTag(pos, par1ItemStack, link);
	}
}
