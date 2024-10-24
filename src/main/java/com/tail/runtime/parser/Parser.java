package com.tail.runtime.parser;

import com.tail.core.TailScript;
import com.tail.runtime.parser.expression.*;
import com.tail.runtime.parser.statement.*;
import com.tail.runtime.scanner.Token;
import com.tail.runtime.scanner.TokenType;

import static com.tail.runtime.scanner.TokenType.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private int current;
    private List<Token> tokens;

    public Parser() {

    }

    public List<StatementNode> parseTokens(List<Token> tokens) {
        resetInternalState(tokens);

        List<StatementNode> expressionNodes = new ArrayList<>();

        while (!isAtEnd()) {
            expressionNodes.add(statement());
        }

        return expressionNodes;
    }

    private StatementNode statement() {
        if (match(Log)) return logStatement();
        if (match(If)) return ifStatement();
        if (match(Begin)) return blockStatement();
        return expressionStatement();
    }

    private StatementNode blockStatement() {
        List<StatementNode> statements = new ArrayList<>();

        Token start = previous();

        while (!isAtEnd() && !check(End)) {
            statements.add(statement());
        }

        consume(End, "Expect 'end' after block starting in line " + start.line() + ".");

        return new BlockStatement(statements);
    }

    private StatementNode ifStatement() {
        ExpressionNode condition = expression();

        List<StatementNode> thenBranch = new ArrayList<>();
        List<StatementNode> elseBranch = new ArrayList<>();

        if(check(Begin)) {
            consume(Begin, "Optional 'begin' keyword.");
        }

        while (!isAtEnd() && !check(End, Else)) {
            thenBranch.add(statement());
        }

        if (match(Else)) {
            while (!isAtEnd() && !check(End)) {
                elseBranch.add(statement());
            }
        }

        consume(End, "Missing 'end' keyword after 'if' branches.");
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private StatementNode expressionStatement() {
        ExpressionNode expression = expression();
        return new ExpressionStatement(expression);
    }

    private StatementNode logStatement() {
        ExpressionNode expression = expression();
        return new LogStatement(expression);
    }

    private ExpressionNode expression() {
        return or();
    }

    private ExpressionNode or() {
        ExpressionNode expressionNode = and();

        while (match(Or)) {
            Token operator = previous();
            ExpressionNode right = and();
            expressionNode = new LogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private ExpressionNode and() {
        ExpressionNode expressionNode = equality();

        while (match(And)) {
            Token operator = previous();
            ExpressionNode right = equality();
            expressionNode = new LogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private ExpressionNode equality() {

        ExpressionNode expressionNode = comparison();

        while (match(EqualEqual)) {
            Token operator = previous();
            ExpressionNode right = comparison();
            expressionNode = new LogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private ExpressionNode comparison() {

        ExpressionNode expressionNode = term();

        if (match(Greater, GreaterEqual, Less, LessEqual)) {
            Token operator = previous();
            ExpressionNode right = term();
            expressionNode = new LogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private ExpressionNode term() {
        ExpressionNode expressionNode = factor();

        while (match(Plus, Minus)) {
            Token operator = previous();
            ExpressionNode right = factor();
            expressionNode = new BinaryExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private ExpressionNode factor() {
        ExpressionNode expressionNode = unary();

        while (match(Star, Slash)) {
            Token operator = previous();
            ExpressionNode right = factor();
            expressionNode = new BinaryExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private ExpressionNode unary() {
        if (match(Minus, Not)) {
            Token operator = previous();
            ExpressionNode expressionNode = unary();
            return new UnaryExpression(operator, expressionNode);
        }

        return primary();
    }

    private ExpressionNode primary() {
        if (match(Number, True, False, Null, String)) {
            return new LiteralExpression(previous());
        }

        if (match(LeftParen)) return group();

        error("Invalid expression.");
        return null;
    }

    private ExpressionNode group() {
        ExpressionNode expression = expression();
        consume(RightParen, "Expect ')' after group.");
        return expression;
    }

    private void resetInternalState(List<Token> input) {
        tokens = input;
        current = 0;
    }

    private void consume(TokenType type, String message) {
        if (!match(type)) {
            error(message);
        }
    }

    private void error(String message) {
        TailScript.error(message, peek());
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EndOfFile;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void advance() {
        current++;
    }

    private boolean match(TokenType... types) {
        if (check(types)) {
            advance();
            return true;
        }

        return false;
    }

    private boolean check(TokenType... types) {
        if (isAtEnd()) return false;
        for (TokenType type : types) {
            if (peek().type() == type) {
                return true;
            }
        }

        return false;
    }

    private Token peek() {
        return tokens.get(current);
    }
}
