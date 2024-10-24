package com.tail.runtime.scanner;

public enum TokenType {
    Device, Begin, End, And, Or, Not, True, False, Dot, LeftParen, RightParen, LeftBracket, RightBracket,
    Var, Comma, String, Number, Null, If, Else, Elif, Equal, Greater, Less, GreaterEqual, LessEqual, Underscore,
    For, While, Colon, Comment, Function, Plus, Minus, Star, Slash, EqualEqual, Identifier, EndOfFile, Error, Log, Lambda
}
