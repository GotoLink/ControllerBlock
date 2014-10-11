/*
 *  Author: Sam6982
 */
package nekto.controller.item;

import nekto.controller.core.Controller;
import nekto.controller.tile.TileEntityBase;
import nekto.controller.tile.TileEntityController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

public class ItemLinker extends ItemBase {
	public ItemLinker() {
		super();
		setUnlocalizedName("linker");
	}

	@Override
	protected Class<? extends TileEntityBase<?>> getControl() {
		return TileEntityController.class;
	}

	@Override
	protected String getControlName() {
		return Controller.controller.getLocalizedName();
	}

	@Override
	protected boolean onControlUsed(TileEntityBase<?> tempTile, EntityPlayer player, ItemStack stack, TileEntityBase link) {
		if (super.onControlUsed(tempTile, player, stack, link)) {
			return true;
		} else if (ItemStack.areItemStackTagsEqual(tempTile.getLinker(), stack)) {
			tempTile.setLinker(null);
			tempTile.setEditing(false);
			stack.getTagCompound().removeTag(KEYTAG);
			player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_2, getControlName()));
			return true;
		}
		return false;
	}
}
