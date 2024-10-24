package com.tail.runtime.parser.statement;

public abstract class StatementNode {

    public abstract <T> T acceptProcessor(StatementNodeProcessor<T> processor);
}
