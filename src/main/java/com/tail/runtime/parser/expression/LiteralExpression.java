package com.tail.runtime.parser.expression;

import com.tail.runtime.scanner.Token;

public class LiteralExpression extends ExpressionNode {

    public final Token literal;

    public LiteralExpression(Token literal) {
        this.literal = literal;
    }

    @Override
    public <T> T acceptProcessor(ExpressionNodeProcessor<T> processor) {
        return processor.processLiteralExpression(this);
    }

    @Override
    public String toString() {
        return "Literal(" + literal.literal().toString() + ")";
    }
}
