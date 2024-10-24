package com.tail.runtime.parser.statement;

public interface StatementNodeProcessor<T> {

    T processLogStatement(LogStatement expression);

    T processExpressionStatement(ExpressionStatement statement);

    T processIfStatement(IfStatement statement);

    T processBlockStatement(BlockStatement statement);
}