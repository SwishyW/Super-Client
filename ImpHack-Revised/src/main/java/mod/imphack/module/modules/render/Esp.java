package mod.imphack.module.modules.render;

import java.util.List;
import java.util.stream.Collectors;

import mod.imphack.event.events.ImpHackEventRender;
import mod.imphack.module.Category;
import mod.imphack.module.Module;
import mod.imphack.setting.settings.BooleanSetting;
import mod.imphack.setting.settings.ColorSetting;
import mod.imphack.setting.settings.IntSetting;
import mod.imphack.setting.settings.ModeSetting;
import mod.imphack.util.render.ColorUtil;
import mod.imphack.util.render.Geometry;
import mod.imphack.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;

public class Esp extends Module {

	public final BooleanSetting chams = new BooleanSetting("walls", this, false);
	public final ModeSetting entityMode = new ModeSetting("entity", this, "box", "box", "highlight", "box+highlight", "outline", "outlineEsp2D", "glow", "off");
	public final ModeSetting storage = new ModeSetting("storage", this, "outline", "outline", "fill", "both", "off");
	public final ModeSetting crystalMode = new ModeSetting("crystal", this, "pretty", "pretty", "glow", "off");
	public final BooleanSetting mob = new BooleanSetting("mob", this, false);
	public final BooleanSetting item = new BooleanSetting("item", this, true);
	public final IntSetting range = new IntSetting("range", this, 100);
	public final IntSetting lineWidth = new IntSetting("lineWidth", this, 3);

	public final ColorSetting playerColor = new ColorSetting("player", this, new ColorUtil(0, 121, 194, 100));
	public final ColorSetting hostileMobColor = new ColorSetting("hostileMob", this, new ColorUtil(255, 0, 0, 100));
	public final ColorSetting passiveMobColor = new ColorSetting("passiveMob", this, new ColorUtil(0, 255, 0, 100));
	public final ColorSetting itemColor = new ColorSetting("itemColor", this, new ColorUtil(0, 121, 194, 100));
	public final ColorSetting chestColor = new ColorSetting("chest", this, new ColorUtil(255, 255, 0, 50));
	public final ColorSetting enderChestColor = new ColorSetting("enderChest", this, new ColorUtil(255, 70, 200, 50));
	public final ColorSetting shulkerBoxColor = new ColorSetting("shulkerBox", this, new ColorUtil(255, 182, 193, 50));
	public final ColorSetting otherColor = new ColorSetting("other", this, new ColorUtil(150, 150, 150, 50));

	public Esp() {
		super("Esp's", "draws esp around players and storage blocks.", Category.RENDER);
		this.addSetting(entityMode, storage, crystalMode, mob, item, chams, range, lineWidth, playerColor,
				passiveMobColor, hostileMobColor, itemColor, chestColor, enderChestColor, shulkerBoxColor, otherColor);
	}

	private static final Minecraft mc = Minecraft.getMinecraft();

	List<Entity> entities;
	
    ColorUtil playerC;
    ColorUtil playerCOutline;
    ColorUtil hostileMobC;
    ColorUtil passiveMobC;
    ColorUtil mainIntColor;
    ColorUtil containerColor;
    ColorUtil containerBox;
    int opacityGradient;

    @Override
    public void render(ImpHackEventRender event) {
    	
    	entities = mc.world.loadedEntityList.stream()
                .filter(entity -> entity != mc.player)
                .collect(Collectors.toList());
        entities.forEach(entity -> {
            defineEntityColors(entity);
            
            if(!entityMode.is("glow") && !(entity instanceof EntityEnderCrystal)) entity.setGlowing(false);
            if(entityMode.is("glow") && !mob.isEnabled() && entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntityAnimal) entity.setGlowing(false);
            if(entityMode.is("glow") && !item.isEnabled() && entity instanceof EntityItem) entity.setGlowing(false);
            
            if(!crystalMode.is("glow") && entity instanceof EntityEnderCrystal) entity.setGlowing(false);
            
            
            // players - box
            if (entityMode.is("box") && entity instanceof EntityPlayer) {
            	RenderUtil.playerEsp(entity.getEntityBoundingBox(), (float) lineWidth.getValue(), playerCOutline);
            }
            // player - highlight
            if (entityMode.is("highlight") && entity instanceof EntityPlayer) {
            	RenderUtil.drawPlayerBox(entity.getEntityBoundingBox(), (float)lineWidth.getValue(), playerC, Geometry.Quad.ALL);
            }
            // players - box+highlight
            if (entityMode.is("box+highlight") && entity instanceof EntityPlayer) {
            	RenderUtil.playerEsp(entity.getEntityBoundingBox(), (float) lineWidth.getValue(), playerCOutline);
            	RenderUtil.drawPlayerBox(entity.getEntityBoundingBox(), (float)lineWidth.getValue(), playerC, Geometry.Quad.ALL);
            }
            
            // glow Esp's
            if (entityMode.is("glow") && entity instanceof EntityPlayer) {
            	entity.setGlowing(true);
            }
            if (entityMode.is("glow") && mob.isEnabled() &&  entity instanceof EntityCreature || entity instanceof EntitySlime) {
            	entity.setGlowing(true);
            }
            if (entityMode.is("glow") && mob.isEnabled() && entity instanceof EntityAnimal) {
            	entity.setGlowing(true);
            }
            if (entityMode.is("glow") && item.isEnabled() && entity instanceof EntityItem) {
            	entity.setGlowing(true);
            }
            if (crystalMode.is("glow") && entity instanceof EntityEnderCrystal) {
            	entity.setGlowing(true);
            }
            
            // hostiles and passives - box
            if (mob.isEnabled() && !entityMode.is("outline") && !entityMode.is("glow") && !entityMode.is("off")){
                if (entity instanceof EntityCreature || entity instanceof EntitySlime) {
                    RenderUtil.drawBoundingBox(entity.getEntityBoundingBox(), 2, hostileMobC);
                }
            }
            if (mob.isEnabled() && !entityMode.is("outline") && !entityMode.is("glow") && !entityMode.is("off")){
                if (entity instanceof EntityAnimal) {
                    RenderUtil.drawBoundingBox(entity.getEntityBoundingBox(), 2, passiveMobC);
                }
            }
            
            // items
            if (item.isEnabled() && !entityMode.is("off") && !entityMode.is("glow") && entity instanceof EntityItem){
            	RenderUtil.drawBoundingBox(entity.getEntityBoundingBox(), 2, mainIntColor);
            }
        });
        
        if (storage.is("outline")) {
            mc.world.loadedTileEntityList.stream().filter(this::rangeTileCheck).forEach(tileEntity -> {
                if (tileEntity instanceof TileEntityChest){
                    containerColor = new ColorUtil(chestColor.getValue(), 255);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                }
                if (tileEntity instanceof TileEntityEnderChest){
                    containerColor = new ColorUtil(enderChestColor.getValue(), 255);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                }
                if (tileEntity instanceof TileEntityShulkerBox){
                    containerColor = new ColorUtil(shulkerBoxColor.getValue(), 255);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                }
                if(tileEntity instanceof TileEntityDispenser || tileEntity instanceof TileEntityFurnace || tileEntity instanceof TileEntityHopper || tileEntity instanceof TileEntityDropper){
                    containerColor = new ColorUtil(otherColor.getValue(), 255);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                }
            });
        }
        
        if (storage.is("both")) {
            mc.world.loadedTileEntityList.stream().filter(this::rangeTileCheck).forEach(tileEntity -> {
                if (tileEntity instanceof TileEntityChest){
                    containerColor = new ColorUtil(chestColor.getValue(), 255);
                    containerBox = new ColorUtil(chestColor.getValue());
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                    drawStorageBox(tileEntity.getPos(), containerBox);
                }
                if (tileEntity instanceof TileEntityEnderChest){
                	containerColor = new ColorUtil(enderChestColor.getValue(), 255);
                	containerBox = new ColorUtil(enderChestColor.getValue());
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                    drawStorageBox(tileEntity.getPos(), containerBox);
                }
                if (tileEntity instanceof TileEntityShulkerBox){
                	containerColor = new ColorUtil(shulkerBoxColor.getValue(), 255);
                	containerBox = new ColorUtil(shulkerBoxColor.getValue());
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                    drawBox(tileEntity.getPos(), containerBox);
                }
                if(tileEntity instanceof TileEntityDispenser || tileEntity instanceof TileEntityFurnace || tileEntity instanceof TileEntityHopper || tileEntity instanceof TileEntityDropper){
                	containerColor = new ColorUtil(otherColor.getValue(), 255);
                	containerBox = new ColorUtil(otherColor.getValue());
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), 2, containerColor);
                    drawBox(tileEntity.getPos(), containerBox);
                }
            });
        }
        
        if (storage.is("fill")) {
            mc.world.loadedTileEntityList.stream().filter(this::rangeTileCheck).forEach(tileEntity -> {
                if (tileEntity instanceof TileEntityChest){
                	containerBox = new ColorUtil(chestColor.getValue());
                    drawStorageBox(tileEntity.getPos(), containerBox);
                }
                if (tileEntity instanceof TileEntityEnderChest){
                	containerBox = new ColorUtil(enderChestColor.getValue());
                    drawStorageBox(tileEntity.getPos(), containerBox);
                }
                if (tileEntity instanceof TileEntityShulkerBox){
                	containerBox = new ColorUtil(shulkerBoxColor.getValue());
                    drawBox(tileEntity.getPos(), containerBox);
                }
                if(tileEntity instanceof TileEntityDispenser || tileEntity instanceof TileEntityFurnace || tileEntity instanceof TileEntityHopper || tileEntity instanceof TileEntityDropper){
                	containerBox = new ColorUtil(otherColor.getValue());
                    drawBox(tileEntity.getPos(), containerBox);
                }
            });
        }
        
        // crystal csgo
        
        if(crystalMode.is("csgo")) {
        	
        }
    }
    
    private void drawStorageBox(BlockPos blockPos, ColorUtil color) {
		RenderUtil.drawStorageBox(blockPos, 0.88, color, Geometry.Quad.ALL);
	}

	private void drawBox(BlockPos blockPos, ColorUtil color) {
		RenderUtil.drawBox(blockPos, 1, color, Geometry.Quad.ALL);
	}

	public void onDisable() {
		if (entities != mc.player) {
			entities.forEach(p -> p.setGlowing(false));
		}
	}

	private void defineEntityColors(Entity entity) {
		if (entity instanceof EntityPlayer) {
			playerC = new ColorUtil(playerColor.getValue());
			playerCOutline = new ColorUtil(playerColor.getValue(), 255);
		}

		if (entity instanceof EntityMob) {
			hostileMobC = new ColorUtil(hostileMobColor.getValue());
		} else if (entity instanceof EntityAnimal) {
			passiveMobC = new ColorUtil(passiveMobColor.getValue());
		} else {
			passiveMobC = new ColorUtil(passiveMobColor.getValue());
		}

		if (entity instanceof EntitySlime) {
			hostileMobC = new ColorUtil(hostileMobColor.getValue());
		}

		if (entity != null) {
			mainIntColor = new ColorUtil(itemColor.getValue());
		}
	}
	// boolean range check and opacity gradient

	private boolean rangeTileCheck(TileEntity tileEntity) {
		// the range value has to be squared for this
		if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) > range.getValue()
				* range.getValue()) {
			return false;
		}

		if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 32400) {
			opacityGradient = 50;
		} else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 16900
				&& tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 32400) {
			opacityGradient = 100;
		} else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 6400
				&& tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 16900) {
			opacityGradient = 150;
		} else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 900
				&& tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 6400) {
			opacityGradient = 200;
		} else {
			opacityGradient = 255;
		}

		return true;
	}
}