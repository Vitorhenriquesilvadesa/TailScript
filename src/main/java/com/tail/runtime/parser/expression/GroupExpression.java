package com.tail.runtime.parser.expression;

import com.tail.runtime.scanner.Token;

public class GroupExpression extends ExpressionNode{

    public final Token paren;
    public final ExpressionNode expression;

    public GroupExpression(Token paren, ExpressionNode expression) {
        this.paren = paren;
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(ExpressionNodeProcessor<T> processor) {
        return processor.processGroupExpression(this);
    }
}
