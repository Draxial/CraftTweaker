package stanhebben.zenscript.type;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import stanhebben.zenscript.TypeExpansion;
import zenscript.annotations.CompareType;
import zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IScopeGlobal;
import stanhebben.zenscript.compiler.IScopeMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArithmeticBinary;
import stanhebben.zenscript.expression.ExpressionArithmeticCompare;
import stanhebben.zenscript.expression.ExpressionArithmeticUnary;
import stanhebben.zenscript.expression.ExpressionFloat;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.AnyClassWriter;
import static stanhebben.zenscript.util.AnyClassWriter.throwCastException;
import static stanhebben.zenscript.util.AnyClassWriter.throwUnsupportedException;
import stanhebben.zenscript.util.IAnyDefinition;
import stanhebben.zenscript.util.MethodOutput;
import static stanhebben.zenscript.util.ZenTypeUtil.internal;
import static stanhebben.zenscript.util.ZenTypeUtil.signature;
import zenscript.runtime.IAny;
import zenscript.symbolic.TypeRegistry;
import zenscript.symbolic.type.casting.CastingRuleD2F;
import zenscript.symbolic.type.casting.CastingRuleD2I;
import zenscript.symbolic.type.casting.CastingRuleD2L;
import zenscript.symbolic.type.casting.CastingRuleI2B;
import zenscript.symbolic.type.casting.CastingRuleI2S;
import zenscript.symbolic.type.casting.CastingRuleStaticMethod;
import zenscript.symbolic.type.casting.ICastingRuleDelegate;
import zenscript.symbolic.util.CommonMethods;
import zenscript.util.ZenPosition;

public class ZenTypeDouble extends ZenType {
	private static final String ANY_NAME = "any/AnyDouble";
	private static final String ANY_NAME_2 = "any.AnyDouble";
	
	public ZenTypeDouble(IScopeGlobal environment) {
		super(environment);
	}

	@Override
	public IZenIterator makeIterator(int numValues, MethodOutput output) {
		return null;
	}

	@Override
	public void constructCastingRules(ICastingRuleDelegate rules, boolean followCasters) {
		TypeRegistry types = getEnvironment().getTypes();
		CommonMethods methods = types.getCommonMethods();
		
		rules.registerCastingRule(types.BYTE, new CastingRuleI2B(new CastingRuleD2I(null, types), types));
		rules.registerCastingRule(types.BYTEOBJECT, new CastingRuleStaticMethod(methods.BYTE_VALUEOF, new CastingRuleI2B(new CastingRuleD2I(null, types), types)));
		rules.registerCastingRule(types.SHORT, new CastingRuleI2S(new CastingRuleD2I(null, types), types));
		rules.registerCastingRule(types.SHORTOBJECT, new CastingRuleStaticMethod(methods.SHORT_VALUEOF, new CastingRuleI2S(new CastingRuleD2I(null, types), types)));
		rules.registerCastingRule(types.INT, new CastingRuleD2I(null, types));
		rules.registerCastingRule(types.INTOBJECT, new CastingRuleStaticMethod(methods.INT_VALUEOF, new CastingRuleD2I(null, types)));
		rules.registerCastingRule(types.LONG, new CastingRuleD2L(null, types));
		rules.registerCastingRule(types.LONGOBJECT, new CastingRuleStaticMethod(methods.LONG_VALUEOF, new CastingRuleD2L(null, types)));
		rules.registerCastingRule(types.FLOAT, new CastingRuleD2F(null, types));
		rules.registerCastingRule(types.FLOATOBJECT, new CastingRuleStaticMethod(methods.FLOAT_VALUEOF, new CastingRuleD2F(null, types)));
		rules.registerCastingRule(types.DOUBLEOBJECT, new CastingRuleStaticMethod(methods.DOUBLE_VALUEOF));
		
		rules.registerCastingRule(types.STRING, new CastingRuleStaticMethod(methods.DOUBLE_TOSTRING_STATIC));
		rules.registerCastingRule(types.ANY, new CastingRuleStaticMethod(JavaMethod.getStatic(getAnyClassName(), "valueOf", types.ANY, types.DOUBLE)));
		
		if (followCasters) {
			constructExpansionCastingRules(rules);
		}
	}

	@Override
	public Type toASMType() {
		return Type.DOUBLE_TYPE;
	}
	
	@Override
	public Class toJavaClass() {
		return double.class;
	}

	@Override
	public int getNumberType() {
		return NUM_DOUBLE;
	}

	@Override
	public IPartialExpression getMember(ZenPosition position, IScopeMethod environment, IPartialExpression value, String name) {
		IPartialExpression result = memberExpansion(position, environment, value.eval(), name);
		if (result == null) {
			environment.error(position, "double value has no members");
			return new ExpressionInvalid(position, environment);
		} else {
			return result;
		}
	}

	@Override
	public IPartialExpression getStaticMember(ZenPosition position, IScopeMethod environment, String name) {
		IPartialExpression result = staticMemberExpansion(position, environment, name);
		if (result == null) {
			environment.error(position, "double value has no static members");
			return new ExpressionInvalid(position, environment);
		} else {
			return result;
		}
	}

	@Override
	public String getSignature() {
		return "D";
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	@Override
	public Expression unary(ZenPosition position, IScopeMethod environment, Expression value, OperatorType operator) {
		return new ExpressionArithmeticUnary(position, environment, operator, value);
	}

	@Override
	public Expression binary(ZenPosition position, IScopeMethod environment, Expression left, Expression right, OperatorType operator) {
		if (operator == OperatorType.CAT) {
			TypeRegistry types = environment.getTypes();
			
			return types.STRING.binary(
					position,
					environment,
					left.cast(position, types.STRING),
					right.cast(position, types.STRING),
					OperatorType.CAT);
		}
		
		return new ExpressionArithmeticBinary(position, environment, operator, left, right.cast(position, this));
	}
	
	@Override
	public Expression trinary(ZenPosition position, IScopeMethod environment, Expression first, Expression second, Expression third, OperatorType operator) {
		environment.error(position, "double doesn't support this operation");
		return new ExpressionInvalid(position, environment);
	}
	
	@Override
	public Expression compare(ZenPosition position, IScopeMethod environment, Expression left, Expression right, CompareType type) {
		return new ExpressionArithmeticCompare(position, environment, type, left, right.cast(position, this));
	}

	/*@Override
	public Expression call(
			ZenPosition position, IEnvironmentMethod environment, Expression receiver, Expression... arguments) {
		environment.error(position, "cannot call a double value");
		return new ExpressionInvalid(position, environment);
	}*/

	@Override
	public String getName() {
		return "double";
	}
	
	@Override
	public String getAnyClassName() {
		IScopeGlobal environment = getEnvironment();
		
		if (!environment.containsClass(ANY_NAME_2)) {
			environment.putClass(ANY_NAME_2, new byte[0]);
			environment.putClass(ANY_NAME_2, AnyClassWriter.construct(new AnyDefinitionDouble(environment), ANY_NAME, Type.DOUBLE_TYPE));
		}
		
		return ANY_NAME;
	}
	
	@Override
	public boolean isLarge() {
		return true;
	}

	@Override
	public Expression defaultValue(ZenPosition position, IScopeMethod environment) {
		return new ExpressionFloat(position, environment, 0.0, environment.getTypes().DOUBLE);
	}
	
	@Override
	public ZenType nullable() {
		return getEnvironment().getTypes().DOUBLEOBJECT;
	}
	
	@Override
	public ZenType nonNull() {
		return this;
	}
	
	private class AnyDefinitionDouble implements IAnyDefinition {
		private final IScopeGlobal environment;
		private final TypeRegistry types;
		
		public AnyDefinitionDouble(IScopeGlobal environment) {
			this.environment = environment;
			types = environment.getTypes();
		}

		@Override
		public void defineMembers(ClassVisitor output) {
			output.visitField(Opcodes.ACC_PRIVATE, "value", "D", null, null);
			
			MethodOutput valueOf = new MethodOutput(output, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "valueOf", "(D)" + signature(IAny.class), null, null);
			valueOf.start();
			valueOf.newObject(ANY_NAME);
			valueOf.dup();
			valueOf.load(Type.DOUBLE_TYPE, 0);
			valueOf.construct(ANY_NAME, "D");
			valueOf.returnObject();
			valueOf.end();
			
			MethodOutput constructor = new MethodOutput(output, Opcodes.ACC_PUBLIC, "<init>", "(D)V", null, null);
			constructor.start();
			constructor.loadObject(0);
			constructor.invokeSpecial(internal(Object.class), "<init>", "()V");
			constructor.loadObject(0);
			constructor.load(Type.DOUBLE_TYPE, 1);
			constructor.putField(ANY_NAME, "value", "D");
			constructor.returnType(Type.VOID_TYPE);
			constructor.end();
		}
		
		@Override
		public void defineStaticCanCastImplicit(MethodOutput output) {
			Label lblCan = new Label();
			
			output.constant(Type.BYTE_TYPE);
			output.loadObject(0);
			output.ifACmpEq(lblCan);
			
			output.constant(Type.SHORT_TYPE);
			output.loadObject(0);
			output.ifACmpEq(lblCan);
			
			output.constant(Type.INT_TYPE);
			output.loadObject(0);
			output.ifACmpEq(lblCan);
			
			output.constant(Type.LONG_TYPE);
			output.loadObject(0);
			output.ifACmpEq(lblCan);
			
			output.constant(Type.FLOAT_TYPE);
			output.loadObject(0);
			output.ifACmpEq(lblCan);
			
			output.constant(Type.DOUBLE_TYPE);
			output.loadObject(0);
			output.ifACmpEq(lblCan);
			
			TypeExpansion expansion = environment.getExpansion(getName());
			if (expansion != null) {
				expansion.compileAnyCanCastImplicit(types.FLOAT, output, environment, 0);
			}
			
			output.iConst0();
			output.returnInt();
			
			output.label(lblCan);
			output.iConst1();
			output.returnInt();
		}

		@Override
		public void defineStaticAs(MethodOutput output) {
			TypeExpansion expansion = environment.getExpansion(getName());
			if (expansion != null) {
				expansion.compileAnyCast(types.DOUBLE, output, environment, 0, 1);
			}
			
			throwCastException(output, "double", 1);
		}

		@Override
		public void defineNot(MethodOutput output) {
			throwUnsupportedException(output, "double", "not");
		}

		@Override
		public void defineNeg(MethodOutput output) {
			output.newObject(ANY_NAME);
			output.dup();
			getValue(output);
			output.dNeg();
			output.invokeSpecial(ANY_NAME, "<init>", "(D)V");
			output.returnObject();
		}

		@Override
		public void defineAdd(MethodOutput output) {
			output.newObject(ANY_NAME);
			output.dup();
			getValue(output);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asDouble", double.class);
			output.dAdd();
			output.invokeSpecial(ANY_NAME, "<init>", "(D)V");
			output.returnObject();
		}

		@Override
		public void defineSub(MethodOutput output) {
			output.newObject(ANY_NAME);
			output.dup();
			getValue(output);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asDouble", double.class);
			output.dSub();
			output.invokeSpecial(ANY_NAME, "<init>", "(D)V");
			output.returnObject();
		}

		@Override
		public void defineCat(MethodOutput output) {
			// StringBuilder builder = new StringBuilder();
			// builder.append(value);
			// builder.append(other.asString());
			// return new AnyString(builder.toString());
			output.newObject(StringBuilder.class);
			output.dup();
			output.invokeSpecial(internal(StringBuilder.class), "<init>", "()V");
			getValue(output);
			output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, double.class);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asString", String.class);
			output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
			output.invokeVirtual(StringBuilder.class, "toString", String.class);
			output.invokeStatic(types.STRING.getAnyClassName(), "valueOf", "(Ljava/lang/String;)" + signature(IAny.class));
			output.returnObject();
		}

		@Override
		public void defineMul(MethodOutput output) {
			output.newObject(ANY_NAME);
			output.dup();
			getValue(output);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asDouble", double.class);
			output.dMul();
			output.invokeSpecial(ANY_NAME, "<init>", "(D)V");
			output.returnObject();
		}

		@Override
		public void defineDiv(MethodOutput output) {
			output.newObject(ANY_NAME);
			output.dup();
			getValue(output);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asDouble", double.class);
			output.dDiv();
			output.invokeSpecial(ANY_NAME, "<init>", "(D)V");
			output.returnObject();
		}

		@Override
		public void defineMod(MethodOutput output) {
			output.newObject(ANY_NAME);
			output.dup();
			getValue(output);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asDouble", double.class);
			output.dRem();
			output.invokeSpecial(ANY_NAME, "<init>", "(D)V");
			output.returnObject();
		}

		@Override
		public void defineAnd(MethodOutput output) {
			throwUnsupportedException(output, "double", "and");
		}

		@Override
		public void defineOr(MethodOutput output) {
			throwUnsupportedException(output, "double", "or");
		}

		@Override
		public void defineXor(MethodOutput output) {
			throwUnsupportedException(output, "double", "xor");
		}

		@Override
		public void defineRange(MethodOutput output) {
			throwUnsupportedException(output, "double", "range");
		}

		@Override
		public void defineCompareTo(MethodOutput output) {
			// return Double.compare(x, y)
			getValue(output);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asDouble", double.class);
			output.invokeStatic(Float.class, "compare", int.class, double.class, double.class);
			output.returnInt();
		}

		@Override
		public void defineContains(MethodOutput output) {
			throwUnsupportedException(output, "double", "in");
		}

		@Override
		public void defineMemberGet(MethodOutput output) {
			// TODO
			output.aConstNull();
			output.returnObject();
		}

		@Override
		public void defineMemberSet(MethodOutput output) {
			// TODO
			output.returnType(Type.VOID_TYPE);
		}

		@Override
		public void defineMemberCall(MethodOutput output) {
			// TODO
			output.aConstNull();
			output.returnObject();
		}

		@Override
		public void defineIndexGet(MethodOutput output) {
			throwUnsupportedException(output, "double", "get []");
		}

		@Override
		public void defineIndexSet(MethodOutput output) {
			throwUnsupportedException(output, "double", "set []");
		}

		@Override
		public void defineCall(MethodOutput output) {
			throwUnsupportedException(output, "double", "call");
		}

		@Override
		public void defineAsBool(MethodOutput output) {
			throwCastException(output, ANY_NAME, "bool");
		}

		@Override
		public void defineAsByte(MethodOutput output) {
			getValue(output);
			output.d2i();
			output.i2b();
			output.returnType(Type.BYTE_TYPE);
		}

		@Override
		public void defineAsShort(MethodOutput output) {
			getValue(output);
			output.d2i();
			output.i2s();
			output.returnType(Type.SHORT_TYPE);
		}

		@Override
		public void defineAsInt(MethodOutput output) {
			getValue(output);
			output.d2i();
			output.returnType(Type.INT_TYPE);
		}

		@Override
		public void defineAsLong(MethodOutput output) {
			getValue(output);
			output.d2l();
			output.returnType(Type.LONG_TYPE);
		}

		@Override
		public void defineAsFloat(MethodOutput output) {
			getValue(output);
			output.d2f();
			output.returnType(Type.FLOAT_TYPE);
		}

		@Override
		public void defineAsDouble(MethodOutput output) {
			getValue(output);
			output.returnType(Type.DOUBLE_TYPE);
		}

		@Override
		public void defineAsString(MethodOutput output) {
			getValue(output);
			output.invokeStatic(Double.class, "toString", String.class, double.class);
			output.returnObject();
		}

		@Override
		public void defineAs(MethodOutput output) {
			int localValue = output.local(Type.DOUBLE_TYPE);
			
			getValue(output);
			output.store(Type.DOUBLE_TYPE, localValue);
			TypeExpansion expansion = environment.getExpansion(getName());
			if (expansion != null) {
				expansion.compileAnyCast(types.DOUBLE, output, environment, localValue, 1);
			}
			
			throwCastException(output, "double", 1);
		}

		@Override
		public void defineIs(MethodOutput output) {
			Label lblEq = new Label();
			
			output.loadObject(1);
			output.constant(Type.DOUBLE_TYPE);
			output.ifACmpEq(lblEq);
			output.iConst0();
			output.returnInt();
			output.label(lblEq);
			output.iConst1();
			output.returnInt();
		}
		
		@Override
		public void defineGetNumberType(MethodOutput output) {
			output.constant(IAny.NUM_DOUBLE);
			output.returnInt();
		}

		@Override
		public void defineIteratorSingle(MethodOutput output) {
			throwUnsupportedException(output, "double", "iterator");
		}

		@Override
		public void defineIteratorMulti(MethodOutput output) {
			throwUnsupportedException(output, "double", "iterator");
		}
		
		private void getValue(MethodOutput output) {
			output.loadObject(0);
			output.getField(ANY_NAME, "value", "D");
		}

		@Override
		public void defineEquals(MethodOutput output) {
			Label lblNope = new Label();
			
			output.loadObject(1);
			output.instanceOf(IAny.NAME);
			output.ifEQ(lblNope);
			
			getValue(output);
			output.loadObject(1);
			output.invokeInterface(IAny.class, "asDouble", double.class);
			output.dCmp();
			output.ifNE(lblNope);
			
			output.iConst1();
			output.returnInt();
			
			output.label(lblNope);
			output.iConst0();
			output.returnInt();
		}

		@Override
		public void defineHashCode(MethodOutput output) {
			// long bits = Double.doubleToLongBits(value);
			// return (int) (bits ^ bits >> 32)
			int bits = output.local(Type.LONG_TYPE);
			output.invokeStatic(Double.class, "doubleToLongBits", double.class, long.class);
			output.store(Type.LONG_TYPE, bits);
			
			output.load(Type.LONG_TYPE, bits);
			output.l2i();
			output.load(Type.LONG_TYPE, bits);
			output.constant(32);
			output.lShr();
			output.l2i();
			output.iXor();
			
			output.returnInt();
		}
	}
}
