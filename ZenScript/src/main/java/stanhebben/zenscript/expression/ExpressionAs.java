package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IScopeMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import zenscript.symbolic.type.casting.ICastingRule;
import zenscript.util.ZenPosition;

public class ExpressionAs extends Expression {
	private final Expression value;
	private final ICastingRule castingRule;
	
	public ExpressionAs(ZenPosition position, IScopeMethod environment, Expression value, ICastingRule castingRule) {
		super(position, environment);
		
		this.value = value;
		this.castingRule = castingRule;
	}

	@Override
	public ZenType getType() {
		return castingRule.getResultingType();
	}

	@Override
	public void compile(boolean result, MethodOutput output) {
		value.compile(result, output);
		
		if (result) {
			castingRule.compile(output);
		}
	}
}
