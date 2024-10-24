package com.tail.runtime.parser.expression;

public interface ExpressionNodeProcessor<T> {
    T processLiteralExpression(LiteralExpression expression);

    T processLogicalExpression(LogicalExpression expression);

    T processBinaryExpression(BinaryExpression expression);

    T processUnaryExpression(UnaryExpression expression);

    T processGroupExpression(GroupExpression expression);
}
