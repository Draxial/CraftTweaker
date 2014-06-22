/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.mc172.util;

import java.util.List;
import minetweaker.api.item.IItemStack;
import minetweaker.mc172.item.TweakerItemStack;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Stan
 */
public class MineTweakerUtil {
	public static ItemStack[] getItemStacks(IItemStack... items) {
		ItemStack[] output = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++) {
			Object internal = items[i].getInternal();
			if (internal != null && internal instanceof ItemStack) {
				output[i] = (ItemStack) internal;
			}
		}
		return output;
	}
	
	public static IItemStack[] getItemStacks(ItemStack... items) {
		IItemStack[] result = new IItemStack[items.length];
		for (int i = 0; i < items.length; i++) {
			result[i] = new TweakerItemStack(items[i]);
		}
		return result;
	}
	
	public static IItemStack[] getItemStacks(List<ItemStack> items) {
		IItemStack[] result = new IItemStack[items.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = new TweakerItemStack(items.get(i));
		}
		return result;
	}
}
