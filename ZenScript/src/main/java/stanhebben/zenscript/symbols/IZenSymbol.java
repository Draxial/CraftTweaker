/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package stanhebben.zenscript.symbols;

import stanhebben.zenscript.compiler.IScopeMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import zenscript.util.ZenPosition;

/**
 *
 * @author Stanneke
 */
public interface IZenSymbol {
	public IPartialExpression instance(ZenPosition position, IScopeMethod environment);
}
