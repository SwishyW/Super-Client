package mod.imphack.module.modules.combat;

import mod.imphack.Main;
import mod.imphack.container.ImpHackInventory;
import mod.imphack.module.Category;
import mod.imphack.module.Module;
import mod.imphack.setting.settings.BooleanSetting;
import mod.imphack.setting.settings.FloatSetting;
import mod.imphack.setting.settings.ModeSetting;
import mod.imphack.util.InventoryUtil;
import mod.imphack.util.PlayerUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {
    public Offhand() {
        super("Offhand", "Switches items in the offhand to a totem when low on health", Category.COMBAT);
        addSetting(mode);
        addSetting(fallbackMode);
        addSetting(health);
        addSetting(checks);
        addSetting(swordGap);
        addSetting(forceGap);
        addSetting(hotbar);
    }

  ModeSetting mode = new ModeSetting("Mode", this, "Crystal", "Crystal", "Gapple", "Bed", "Chorus", "Totem");
  ModeSetting fallbackMode = new ModeSetting("Fallback", this, "Crystal", "Crystal", "Gapple", "Bed", "Chorus", "Totem");
  FloatSetting health = new FloatSetting("Health", this, 0.1f);

  BooleanSetting checks = new BooleanSetting("Checks", this, true);
     BooleanSetting caFunction = new BooleanSetting("ChecksAutoCrystal", this, false);
     BooleanSetting elytraCheck = new BooleanSetting("ChecksElytra", this, false);
     BooleanSetting fallCheck = new BooleanSetting("ChecksFalling", this, false);

  BooleanSetting swordGap = new BooleanSetting("Sword Gapple", this, true);
  BooleanSetting forceGap = new BooleanSetting("Force Gapple", this, false);
  BooleanSetting hotbar = new BooleanSetting("Search Hotbar", this, false);

    
        
    

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null)
            return;

        Item searching = Items.TOTEM_OF_UNDYING;

        if (mc.player.isElytraFlying() &&  checks.isEnabled() && elytraCheck.isEnabled())
            return;

        if (mc.player.fallDistance > 5 &&  checks.isEnabled() && fallCheck.isEnabled())
            return;

        if (!Main.moduleManager.getModule("CrystalAura").isEnabled() && checks.isEnabled() && caFunction.isEnabled())
            return;

        switch (mode.getMode()) {
            case "Crystal":
                searching = Items.END_CRYSTAL;
                break;
            case "Gapple":
                searching = Items.GOLDEN_APPLE;
                break;
            case "Bed":
                searching = Items.BED;
                break;
            case "Chorus":
                searching = Items.CHORUS_FRUIT;
                break;
        }

        if (health.getValue() > PlayerUtil.getHealth())
            searching = Items.TOTEM_OF_UNDYING;

        else if (InventoryUtil.getHeldItem(Items.DIAMOND_SWORD) && swordGap.isEnabled())
            searching = Items.GOLDEN_APPLE;

        else if (forceGap.isEnabled() && Mouse.isButtonDown(1))
            searching = Items.GOLDEN_APPLE;

        if (mc.player.getHeldItemOffhand().getItem() == searching)
            return;

        if (mc.currentScreen != null)
            return;

        if (InventoryUtil.getInventoryItemSlot(searching, !hotbar.isEnabled()) != -1) {
            InventoryUtil.moveItemToOffhand(InventoryUtil.getInventoryItemSlot(searching, hotbar.isEnabled()));
            return;
        }

        switch (fallbackMode.getMode()) {
            case "Crystal":
                searching = Items.END_CRYSTAL;
                break;
            case "Gapple":
                searching = Items.GOLDEN_APPLE;
                break;
            case "Bed":
                searching = Items.BED;
                break;
            case "Chorus":
                searching = Items.CHORUS_FRUIT;
                break;
            case "Totem":
                searching = Items.TOTEM_OF_UNDYING;
                break;
        }

        
       
        if (mc.player.getHeldItemOffhand().getItem() == searching)
            return;

        if (InventoryUtil.getInventoryItemSlot(searching, !hotbar.isEnabled()) != -1)
            InventoryUtil.moveItemToOffhand(InventoryUtil.getInventoryItemSlot(searching, hotbar.isEnabled()));
    }
}