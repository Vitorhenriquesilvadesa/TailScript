package com.tail.runtime.parser.statement;

import com.tail.runtime.parser.expression.ExpressionNode;

public class LogStatement extends StatementNode {

    public final ExpressionNode expression;

    public LogStatement(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(StatementNodeProcessor<T> processor) {
        return processor.processLogStatement(this);
    }
}
