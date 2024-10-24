package com.tail.runtime.parser.statement;

import java.util.List;

public class BlockStatement extends StatementNode {

    public final List<StatementNode> statements;

    public BlockStatement(List<StatementNode> statements) {
        this.statements = statements;
    }

    @Override
    public <T> T acceptProcessor(StatementNodeProcessor<T> processor) {
        return processor.processBlockStatement(this);
    }
}
