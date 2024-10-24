package com.tail.runtime.parser.statement;

import com.tail.runtime.parser.expression.ExpressionNode;

import java.util.List;

public class IfStatement extends StatementNode {

    public final ExpressionNode condition;
    public final List<StatementNode> thenBranch;
    public final List<StatementNode> elseBranch;

    public IfStatement(ExpressionNode condition, List<StatementNode> thenBranch, List<StatementNode> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <T> T acceptProcessor(StatementNodeProcessor<T> processor) {
        return processor.processIfStatement(this);
    }
}
