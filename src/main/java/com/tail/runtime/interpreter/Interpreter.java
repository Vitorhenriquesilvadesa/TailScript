package com.tail.runtime.interpreter;

import com.tail.core.TailScript;
import com.tail.runtime.parser.expression.*;
import com.tail.runtime.parser.statement.*;
import com.tail.runtime.scanner.TokenType;

import java.util.List;

public class Interpreter implements ExpressionNodeProcessor<Object>, StatementNodeProcessor<Void> {

    public void interpret(List<StatementNode> statements) {
        for (StatementNode node : statements) {
            execute(node);
        }
    }

    private void execute(StatementNode statement) {
        statement.acceptProcessor(this);
    }

    private Object evaluate(ExpressionNode expression) {
        return expression.acceptProcessor(this);
    }

    @Override
    public Object processLiteralExpression(LiteralExpression expression) {
        Object literal = expression.literal.literal();

        if (expression.literal.type() == TokenType.True || expression.literal.type() == TokenType.False) {
            return Boolean.parseBoolean((String) literal);
        }

        return literal;
    }

    @Override
    public Object processLogicalExpression(LogicalExpression expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        TokenType operator = expression.operator.type();

        switch (operator) {
            case And:
                return isTruthy(left) && isTruthy(right);

            case Or:
                return isTruthy(left) || isTruthy(right);

            case EqualEqual:
                return isEqual(left, right);

            case Greater:
            case GreaterEqual:
            case Less:
            case LessEqual:
                if (left instanceof Float && right instanceof Float) {
                    return comparison((Float) left, (Float) right, operator);
                }

            default:
                TailScript.error("Operands must be numbers.", expression.operator);
                return null;
        }
    }

    private Object isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private Object comparison(Float a, Float b, TokenType operator) {
        return switch (operator) {
            case Less -> a < b;
            case LessEqual -> a <= b;
            case Greater -> a > b;
            case GreaterEqual -> a >= b;
            default -> null;
        };
    }

    @Override
    public Object processBinaryExpression(BinaryExpression expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        TokenType operator = expression.operator.type();

        if (left instanceof Float leftNum && right instanceof Float rightNum) {

            return switch (operator) {
                case Plus -> leftNum + rightNum;
                case Minus -> leftNum - rightNum;
                case Star -> leftNum * rightNum;
                case Slash -> {
                    if (rightNum == 0) {
                        TailScript.error("Division by zero.", expression.operator);
                        yield null;
                    }
                    yield leftNum / rightNum;
                }
                default -> {
                    TailScript.error("Unknown binary operator.", expression.operator);
                    yield null;
                }
            };
        } else {
            TailScript.error("Operands must be numbers.", expression.operator);
            return null;
        }
    }


    @Override
    public Object processUnaryExpression(UnaryExpression expression) {
        Object right = evaluate(expression.expression);

        return switch (expression.operator.type()) {
            case Minus -> {
                if (!(right instanceof Float)) {
                    TailScript.error("Operand must be number.", expression.operator);
                    yield null;
                }
                yield -((Float) right);
            }
            case Not -> !(isTruthy(right));
            default -> {
                TailScript.error("Invalid unary expression.", expression.operator);
                yield null;
            }
        };
    }

    @Override
    public Object processGroupExpression(GroupExpression expression) {
        return evaluate(expression.expression);
    }

    private String stringify(Object object) {
        if (object == null) return "null";

        if (object instanceof Float) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private boolean isTruthy(Object value) {
        return switch (value) {
            case null -> false;
            case Boolean b -> b;
            case Float fValue -> fValue.equals(0f);
            default -> true;
        };
    }

    @Override
    public Void processLogStatement(LogStatement statement) {
        String value = stringify(evaluate(statement.expression));
        System.out.println(value);

        return null;
    }

    @Override
    public Void processExpressionStatement(ExpressionStatement statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void processIfStatement(IfStatement statement) {
        Object condition = evaluate(statement.condition);

        if (isTruthy(condition)) {
            for (StatementNode node : statement.thenBranch) {
                execute(node);
            }
        } else if (statement.elseBranch != null) {
            for (StatementNode node : statement.elseBranch) {
                execute(node);
            }
        }

        return null;
    }

    @Override
    public Void processBlockStatement(BlockStatement statement) {

        for (StatementNode node : statement.statements) {
            execute(node);
        }

        return null;
    }
}
