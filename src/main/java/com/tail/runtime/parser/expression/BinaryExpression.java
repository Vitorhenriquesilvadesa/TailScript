package com.tail.runtime.parser.expression;

import com.tail.runtime.scanner.Token;

public class BinaryExpression extends ExpressionNode {

    public final ExpressionNode left;
    public final Token operator;
    public final ExpressionNode right;

    public BinaryExpression(ExpressionNode left, Token operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <T> T acceptProcessor(ExpressionNodeProcessor<T> processor) {
        return processor.processBinaryExpression(this);
    }

    @Override
    public String toString() {
        return "Binary(" + left.toString() + ", " + operator.type().toString() + ", " + right.toString() + ")";
    }
}
