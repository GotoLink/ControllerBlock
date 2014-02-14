package nekto.controller.gui;

import nekto.controller.container.ContainerAnimator;
import nekto.controller.container.ContainerBase;
import nekto.controller.core.Controller;
import nekto.controller.item.ItemBase;
import nekto.controller.network.GuiChangePacket;
import nekto.controller.network.PacketHandler;
import nekto.controller.tile.TileEntityAnimator;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AnimatorGUI extends GuiContainer {
	private boolean remote;

	public AnimatorGUI(InventoryPlayer par1InventoryPlayer, TileEntityAnimator par2TileEntity, boolean isRemote) {
		super(new ContainerAnimator(par1InventoryPlayer, par2TileEntity, !isRemote));
		this.remote = isRemote;
		if (remote) {
            field_146999_f = 256;
            field_147000_g = 256;
		}
	}

	@Override
	protected void func_146979_b(int par1, int par2) {
		if (!remote) {
			String s = "Animator Block";
			this.field_146289_q.drawString(s, this.field_146999_f / 2 - this.field_146289_q.getStringWidth(s) / 2, 6, 4210752);
			int delay = ((ContainerAnimator) this.field_147002_h).getDelay() + 2;
			if (Controller.tickDisplay)
				s = (delay) + "ticks";
			else {
				s = Float.toString((delay) * 0.05F);
				if (s.length() > 3)
					s = s.substring(0, 4);
				s = s + "s";
			}
			this.field_146289_q.drawString(s, 131 - this.field_146289_q.getStringWidth(s) / 2, 87, 0);
		} else {
			TileEntityBase<?> control = ((ContainerBase) this.field_147002_h).getControl();
			String s = "Linked to";
			this.field_146289_q.drawString(s, this.field_146999_f / 2 - this.field_146289_q.getStringWidth(s) / 2, 39, 4210752);
			s = control.getName() + " @";
			this.field_146289_q.drawString(s, this.field_146999_f / 2 - this.field_146289_q.getStringWidth(s) / 2, 48, 4210752);
			s = control.field_145851_c + ", " + control.field_145848_d + ", " + control.field_145849_e;
			this.field_146289_q.drawString(s, this.field_146999_f / 2 - this.field_146289_q.getStringWidth(s) / 2, 57, 4210752);
		}
		refreshButtonsText();
	}

	@Override
	protected void func_146976_a(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_146297_k.renderEngine.bindTexture(new ResourceLocation("controller", getTexture(remote)));
		this.drawTexturedModalRect(field_147003_i, field_147009_r, 0, 0, this.field_146999_f, this.field_147000_g);
	}

	private static String getTexture(boolean remote) {
		return "textures/gui/" + (remote ? "remote" : "controller") + "gui.png";
	}

	@Override
	public void initGui() {
		super.initGui();
		//id, x, y, width, height, text
		if (remote) {
            field_146292_n.add(new GuiButton(0, field_147003_i + 86, field_147009_r + 125, 82, 20, ((ContainerAnimator) this.field_147002_h).getFrame()));
            field_146292_n.add(new GuiButton(1, field_147003_i + 109, field_147009_r + 159, 40, 20, ((ContainerAnimator) this.field_147002_h).getCorner()));
            field_146292_n.add(new GuiButton(2, field_147003_i + 99, field_147009_r + 193, 60, 20, "Reset Link"));
		} else {
            field_146292_n.add(new GuiButton(0, field_147003_i + 149, field_147009_r + 81, 19, 20, "+"));
            field_146292_n.add(new GuiButton(1, field_147003_i + 96, field_147009_r + 81, 19, 20, "-"));
            field_146292_n.add(new GuiButton(2, field_147003_i + 10, field_147009_r + 81, 82, 20, ((ContainerAnimator) this.field_147002_h).getMode()));
            field_146292_n.add(new GuiButton(3, field_147003_i + 32, field_147009_r + 19, 60, 20, "Reset Link"));
            field_146292_n.add(new GuiButton(4, field_147003_i + 96, field_147009_r + 19, 70, 20, "Force Reset"));
            field_146292_n.add(new GuiButton(5, field_147003_i + 96, field_147009_r + 50, 70, 20, ((ContainerAnimator) this.field_147002_h).getMax()));
            field_146292_n.add(new GuiButton(6, field_147003_i + 10, field_147009_r + 50, 82, 20, ((ContainerAnimator) this.field_147002_h).getFrame()));
		}
	}

	private void refreshButtonsText() {
		if (!remote) {
			((GuiButton) this.field_146292_n.get(2)).field_146126_j = ((ContainerAnimator) this.field_147002_h).getMode();
			((GuiButton) this.field_146292_n.get(5)).field_146126_j = ((ContainerAnimator) this.field_147002_h).getMax();
			((GuiButton) this.field_146292_n.get(6)).field_146126_j = ((ContainerAnimator) this.field_147002_h).getFrame();
		} else {
			((GuiButton) this.field_146292_n.get(0)).field_146126_j = ((ContainerAnimator) this.field_147002_h).getFrame();
			((GuiButton) this.field_146292_n.get(1)).field_146126_j = ((ContainerAnimator) this.field_147002_h).getCorner();
		}
	}

	@Override
	protected void func_146284_a(GuiButton guibutton) {
		super.func_146284_a(guibutton);
		TileEntityAnimator animator = (TileEntityAnimator) ((ContainerAnimator) this.field_147002_h).getControl();
		int[] data = null;
		switch (guibutton.field_146127_k) {
		case 3:
		case 4://One of the "Reset" button has been pressed
			ItemStack stack = this.field_147002_h.getSlot(0).getStack();
			if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(ItemBase.KEYTAG)) {
				data = stack.getTagCompound().getIntArray(ItemBase.KEYTAG);
				break;
			}
		default:
			break;
		}
        GuiChangePacket packet = null;
        if (data != null) {
			int[] cData = new int[4 + data.length];
			cData[0] = guibutton.field_146127_k;
			cData[1] = animator.field_145851_c;
			cData[2] = animator.field_145848_d;
			cData[3] = animator.field_145849_e;
			for (int i = 0; i < data.length; i++)
				cData[i + 4] = data[i];
			packet = new GuiChangePacket(remote, cData);
		} else {
			packet = new GuiChangePacket(remote, guibutton.field_146127_k, animator.field_145851_c, animator.field_145848_d, animator.field_145849_e);
		}
		if (packet != null)
            PacketHandler.sendGuiChange(packet);
		refreshButtonsText();
	}
}
