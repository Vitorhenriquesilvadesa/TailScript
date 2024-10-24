package com.tail.runtime.parser.expression;

import com.tail.runtime.scanner.Token;

public class UnaryExpression extends ExpressionNode {

    public final Token operator;
    public final ExpressionNode expression;

    public UnaryExpression(Token operator, ExpressionNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Unary(" + operator.type().toString() + ", " + expression.toString() + ")";
    }

    @Override
    public <T> T acceptProcessor(ExpressionNodeProcessor<T> processor) {
        return processor.processUnaryExpression(this);
    }
}
