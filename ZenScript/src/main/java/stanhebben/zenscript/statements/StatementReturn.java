package stanhebben.zenscript.statements;

import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IScopeMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.util.MethodOutput;
import zenscript.util.ZenPosition;

public class StatementReturn extends Statement {
	private final Expression expression;
	
	public StatementReturn(ZenPosition position, IScopeMethod environment, Expression expression) {
		super(position, environment);
		
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public boolean isReturn() {
		return false;
	}

	@Override
	public void compile(MethodOutput output) {
		output.position(getPosition());
		
		if (expression == null) {
			output.ret();
		} else {
			expression.compile(true, output);
			
			Type asmReturnType = expression.getType().toASMType();
			output.returnType(asmReturnType);
		}
	}
}
