package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.collect.Multimap;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.ChatHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.EnumArmorType;
import moze_intel.projecte.utils.PEKeybind;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class GemFeet extends GemArmorBase implements IFlightProvider, IStepAssister
{
    public GemFeet()
    {
        super(EnumArmorType.FEET);
    }

    public static boolean isStepAssistEnabled(ItemStack boots)
    {
        return !boots.hasTagCompound() || !boots.getTagCompound().hasKey("StepAssist") || boots.getTagCompound().getBoolean("StepAssist");

    }

    public void toggleStepAssist(ItemStack boots, EntityPlayer player)
    {
        if (!boots.hasTagCompound())
        {
            boots.setTagCompound(new NBTTagCompound());
        }

        boolean value;

        if (boots.getTagCompound().hasKey("StepAssist"))
        {
            boots.getTagCompound().setBoolean("StepAssist", !boots.getTagCompound().getBoolean("StepAssist"));
            value = boots.getTagCompound().getBoolean("StepAssist");
        }
        else
        {
            boots.getTagCompound().setBoolean("StepAssist", false);
            value = false;
        }

        EnumChatFormatting e = value ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
        String s = value ? "pe.gem.enabled" : "pe.gem.disabled";
        player.addChatMessage(new ChatComponentTranslation("pe.gem.stepassist_tooltip").appendText(" ")
                .appendSibling(ChatHelper.modifyColor(new ChatComponentTranslation(s), e)));
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP playerMP = ((EntityPlayerMP) player);
            playerMP.fallDistance = 0;
        }
        else
        {
            if (!player.onGround)
            {
                if (player.motionY <= 0)
                {
                    player.motionY *= 0.90;
                }
                if (!player.capabilities.isFlying)
                {
                    player.motionX *= 1.1;
                    player.motionZ *= 1.1;
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean unused)
    {
        tooltips.add(StatCollector.translateToLocal("pe.gem.feet.lorename"));
        tooltips.add(String.format(
                StatCollector.translateToLocal("pe.gem.stepassist.prompt"), ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)));

        EnumChatFormatting e = canStep(stack) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
        String s = canStep(stack) ? "pe.gem.enabled" : "pe.gem.disabled";
        tooltips.add(StatCollector.translateToLocal("pe.gem.stepassist_tooltip") + " "
                + e + StatCollector.translateToLocal(s));
    }

    private boolean canStep(ItemStack stack)
    {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("StepAssist") && stack.getTagCompound().getBoolean("StepAssist");
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        Multimap multimap = super.getAttributeModifiers(stack);
        multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Armor modifier", 1.0, 2));
        return multimap;
    }

    @Override
    public boolean canProvideFlight(ItemStack stack, EntityPlayerMP player)
    {
        return player.getCurrentArmor(0) == stack;
    }

    @Override
    public boolean canAssistStep(ItemStack stack, EntityPlayerMP player)
    {
        return player.getCurrentArmor(0) == stack
                && canStep(stack);
    }
}
