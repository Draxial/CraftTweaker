/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package stanhebben.zenscript.statements;

import stanhebben.zenscript.compiler.IScopeMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.util.MethodOutput;
import zenscript.util.ZenPosition;

/**
 *
 * @author Stanneke
 */
public class StatementVar extends Statement {
	private final SymbolLocal symbol;
	private final Expression initializer;
	
	public StatementVar(ZenPosition position, IScopeMethod method, SymbolLocal symbol, Expression initializer) {
		super(position, method);
		
		this.symbol = symbol;
		this.initializer = initializer;
	}

	@Override
	public void compile(MethodOutput output) {
		output.position(getPosition());
		
		if (initializer != null) {
			initializer.compile(true, output);
			output.store(symbol.getType().toASMType(), output.getLocal(symbol));
		}
	}
}
