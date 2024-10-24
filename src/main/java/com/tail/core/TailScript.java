package com.tail.core;

import com.tail.runtime.interpreter.Interpreter;
import com.tail.runtime.parser.Parser;
import com.tail.runtime.parser.expression.ExpressionNode;
import com.tail.runtime.parser.statement.StatementNode;
import com.tail.runtime.scanner.Scanner;
import com.tail.runtime.scanner.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TailScript {
    public static void main(String[] args) {
        try {
            String content = Files.readString(Path.of(args[0]));
            Scanner scanner = new Scanner();

            List<Token> tokens = scanner.scanTokens(content);

            Parser parser = new Parser();
            List<StatementNode> statementNodes = parser.parseTokens(tokens);

            Interpreter interpreter = new Interpreter();
            interpreter.interpret(statementNodes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void error(String message, Token location) {
        System.out.println("Error: " + message + " [At line " + location.line() + "].");
        System.exit(0);
    }
}