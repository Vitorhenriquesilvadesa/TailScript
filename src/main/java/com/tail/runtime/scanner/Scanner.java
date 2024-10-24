package com.tail.runtime.scanner;

import com.tail.core.TailScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tail.runtime.scanner.TokenType.*;

public class Scanner {

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();

        keywords.put("and", And);
        keywords.put("or", Or);
        keywords.put("begin", Begin);
        keywords.put("end", End);
        keywords.put("if", If);
        keywords.put("else", Else);
        keywords.put("elif", Elif);
        keywords.put("for", For);
        keywords.put("while", While);
        keywords.put("true", True);
        keywords.put("false", False);
        keywords.put("null", Null);
        keywords.put("log", Log);
    }

    private List<Token> tokens;
    private String source;
    private int line;
    private int start;
    private int current;

    public List<Token> scanTokens(String content) {
        resetInternalState(content);
        while (!isAtEnd()) {
            syncCursors();
            scanToken();
        }

        makeToken(TokenType.EndOfFile, null, null);
        return new ArrayList<>(tokens);
    }

    private void scanToken() {
        char c = advance();

        switch (c) {

            case '+':
                makeToken(Plus);
                break;

            case '-':
                makeToken(Minus);
                break;

            case '*':
                makeToken(Star);
                break;

            case '/':
                makeToken(Slash);
                break;

            case '=':
                if (match('=')) {
                    makeToken(EqualEqual);
                } else {
                    makeToken(Equal);
                }
                break;

            case '>':
                if (match('=')) {
                    makeToken(GreaterEqual);
                } else {
                    makeToken(Greater);
                }
                break;

            case '<':
                if (match('=')) {
                    makeToken(LessEqual);
                } else {
                    makeToken(Less);
                }
                break;

            case '(':
                makeToken(LeftParen);
                break;

            case ')':
                makeToken(RightParen);
                break;

            case '.':
                makeToken(Dot);
                break;

            case ' ':
            case '\t':
            case '\r':
                break;

            case '\n':
                line++;
                break;

            case '"':
                string();
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    TailScript.error("Unexpected character.", new Token(Error, "" + peek(), peek(), line));
                }
                break;
        }
    }

    private void identifier() {
        while (!isAtEnd() && isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        makeToken(keywords.getOrDefault(text, Identifier), text, text);
    }

    private void number() {
        while (isDigit(peek()) || peek() == '_') advance();
        if (peek() == '.' && isDigit(peekNext())) {
            do advance();
            while (isDigit(peek()));
        }

        Object number = Float.parseFloat(source.substring(start, current).replaceAll("_", ""));
        makeToken(Number, number);
    }

    private void string() {
        while (!match('"')) {
            advance();
        }

        String text = source.substring(start + 1, current - 1);
        makeToken(String, text, text);
    }


    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean match(char c) {
        if (isAtEnd()) return false;
        if (peek() != c) return false;
        advance();
        return true;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void syncCursors() {
        start = current;
    }

    private void resetInternalState(String file) {
        start = 0;
        current = 0;
        line = 1;
        tokens = new ArrayList<>();
        source = file;
    }

    private void makeToken(TokenType type) {
        makeToken(type, null);
    }

    private void makeToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        makeToken(type, lexeme, literal);
    }

    private void makeToken(TokenType type, String lexeme, Object literal) {
        Token token = new Token(type, lexeme, literal, line);
        tokens.add(token);
    }
}
