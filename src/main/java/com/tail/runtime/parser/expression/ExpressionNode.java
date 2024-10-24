package com.tail.runtime.parser.expression;

public abstract class ExpressionNode {

    public abstract <T> T acceptProcessor(ExpressionNodeProcessor<T> processor);
}
