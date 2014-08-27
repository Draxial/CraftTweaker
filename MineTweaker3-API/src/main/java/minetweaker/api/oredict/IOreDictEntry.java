/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.api.oredict;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import zenscript.annotations.OperatorType;
import zenscript.annotations.ZenClass;
import zenscript.annotations.ZenGetter;
import zenscript.annotations.ZenMethod;
import zenscript.annotations.ZenOperator;

/**
 *
 * @author Stan
 */
@ZenClass("minetweaker.oredict.IOreDictEntry")
public interface IOreDictEntry extends IIngredient {
	@ZenGetter("name")
	public String getName();
	
	@ZenGetter("empty")
	public boolean isEmpty();
	
	@ZenMethod
	public void add(IItemStack item);
	
	@ZenMethod
	public void addAll(IOreDictEntry entry);
	
	@ZenMethod
	public void remove(IItemStack item);
	
	@ZenOperator(OperatorType.CONTAINS)
	public boolean contains(IItemStack item);
	
	@ZenMethod
	public void mirror(IOreDictEntry other);
}
