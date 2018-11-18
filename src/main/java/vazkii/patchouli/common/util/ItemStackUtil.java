package vazkii.patchouli.common.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;

public class ItemStackUtil {
	
	public static String serializeStack(ItemStack stack) {
		StringBuilder builder = new StringBuilder();
		builder.append(stack.getItem().getRegistryName().toString());
		builder.append(":");
		builder.append(stack.getItemDamage());
		
		int count = stack.getCount();
		if (count > 1) {
			builder.append("#");
			builder.append(count);
		}
		
		if(stack.hasTagCompound())
			builder.append(stack.getTagCompound().toString());
		
		return builder.toString();
	}

	public static ItemStack loadStackFromString(String res) {
		String nbt = "";
		int nbtStart = res.indexOf("{");
		if(nbtStart > 0) {
			nbt = res.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
			res = res.substring(0, nbtStart);
		}
		
		int meta = 0;
		
		String[] upper = res.split("#");
		String count = "1";
		if(upper.length > 1) {
			res = upper[0];
			count = upper[1];
		}
		
		String[] tokens = res.split(":");
		if(tokens.length < 2)
			return ItemStack.EMPTY;
		
		if(tokens.length == 3)
			meta = Integer.parseInt(tokens[2]);
		
		int countn = Integer.parseInt(count);
		Item item = Item.REGISTRY.getObject(new ResourceLocation(tokens[0], tokens[1]));
		ItemStack stack = new ItemStack(item, countn, meta);
		if(!nbt.isEmpty())
			try {
				stack.setTagCompound(JsonToNBT.getTagFromJson(nbt));
			} catch (NBTException e) {
				throw new RuntimeException("Failed to parse ItemStack JSON" , e);
			}
		
		return stack;
	}
	
	public static StackWrapper wrapStack(ItemStack stack) {
		return stack.isEmpty() ? StackWrapper.EMPTY_WRAPPER : new StackWrapper(stack);
	}
	
	public static class StackWrapper {
		
		public static final StackWrapper EMPTY_WRAPPER = new StackWrapper(ItemStack.EMPTY);
		
		public final ItemStack stack;
		
		public StackWrapper(ItemStack stack) {
			this.stack = stack;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj == this || (obj instanceof StackWrapper && ItemStack.areItemsEqual(stack, ((StackWrapper) obj).stack));
		}
		
		@Override
		public int hashCode() {
			return stack.getItem().hashCode() ^ stack.getItemDamage() * 31;
		}
		
		@Override
		public String toString() {
			return "Wrapper[" + stack.toString() + "]";
		}
		
	}
	
}
