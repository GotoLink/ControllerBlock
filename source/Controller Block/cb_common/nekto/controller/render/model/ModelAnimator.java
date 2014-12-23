package nekto.controller.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public final class ModelAnimator extends ModelBase {
    private final ModelRenderer Cage0;
    private final ModelRenderer Cage1;
    private final ModelRenderer Frame0;
    private final ModelRenderer Frame1;
    private final ModelRenderer Orb0;
    private final ModelRenderer Orb1;

    public ModelAnimator() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Orb0 = new ModelRenderer(this, 0, 28);
        this.Orb0.mirror = true;
        this.Orb0.setRotationPoint(-1.0F, 13.0F, -1.0F);
        this.Orb0.addBox(0.0F, 0.0F, 0.0F, 2, 5, 2);
        this.Orb1 = new ModelRenderer(this, 0, 20);
        this.Orb1.mirror = true;
        this.Orb1.setRotationPoint(-1.0F, 13.0F, -1.0F);
        this.Orb1.addBox(-1.0F, 1.0F, -1.0F, 4, 3, 4);
        this.Cage0 = new ModelRenderer(this, 50, 0);
        this.Cage0.mirror = true;
        this.Cage0.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Cage0.addBox(5.0F, 11.0F, 7.0F, 2, 10, 1);
        this.Cage0.addBox(-7.0F, 11.0F, 7.0F, 2, 10, 1);
        this.Cage0.addBox(-7.0F, 11.0F, -8.0F, 2, 10, 1);
        this.Cage0.addBox(5.0F, 11.0F, -8.0F, 2, 10, 1);
        this.Cage1 = new ModelRenderer(this, 50, 0);
        this.Cage1.mirror = true;
        this.Cage1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Cage1.addBox(-7.0F, 11.0F, 7.0F, 2, 10, 1);
        this.Cage1.addBox(-7.0F, 11.0F, -8.0F, 2, 10, 1);
        this.Cage1.addBox(5.0F, 11.0F, -8.0F, 2, 10, 1);
        this.Cage1.addBox(5.0F, 11.0F, 7.0F, 2, 10, 1);
        this.Cage1.rotateAngleY = (float) Math.PI / 2;
        this.Frame0 = new ModelRenderer(this, 0, 0);
        this.Frame0.mirror = true;
        this.Frame0.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Frame0.addBox(-7.0F, 8.0F, -8.0F, 14, 3, 16);
        this.Frame0.addBox(-7.0F, 21.0F, -8.0F, 14, 3, 16);
        this.Frame1 = new ModelRenderer(this, 30, 20);
        this.Frame1.mirror = true;
        this.Frame1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Frame1.addBox(-8.0F, 8.0F, -7.0F, 1, 3, 14);
        this.Frame1.addBox(-8.0F, 21.0F, -7.0F, 1, 3, 14);
        this.Frame1.addBox(7.0F, 8.0F, -7.0F, 1, 3, 14);
        this.Frame1.addBox(7.0F, 21.0F, -7.0F, 1, 3, 14);
    }

    public void render(float f) {
        this.Cage0.render(f);
        this.Cage1.render(f);
        this.Frame0.render(f);
        this.Frame1.render(f);
    }

    public void renderOrb(float f) {
        this.Orb0.render(f);
        this.Orb1.render(f);
    }
}
