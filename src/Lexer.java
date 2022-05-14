import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("Wymagany jest argument: plik do analizy");
            System.exit(0);
        }

        String line;
        StringBuilder txt = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        while((line = reader.readLine()) != null){
            txt.append(line);
        }
        reader.close();

        analyze(txt.toString()).forEach(System.out::println);
    }

    public static List<Token> analyze(String code){
        ArrayList<Token> tokens = new ArrayList<>();
        char c, cc;
        int cst, codelen = code.length()-1;

        for (int i = 0; i <= codelen; i++) {
            c = code.charAt(i);

            switch(c){
                case ' ':
                    break;
                case '{':
                    StringBuilder comment = new StringBuilder();
                    cst = i;
                    while((cc = code.charAt(++i)) != '}'){
                        comment.append(cc);
                        if(i == codelen){
                            System.err.println("Nie zamkniety komentarz! od pozycji" + i);
                            break;
                        }
                    }
                    tokens.add(new Token(TokenType.COMMENT, comment.toString(), cst));
                    break;
                case '.':
                case ',':
                case '(':
                case ')':
                case ';':
                    tokens.add(new Token(TokenType.DELIMITER, String.valueOf(c), i));
                    break;
                case '+':
                    if(i != codelen && code.charAt(i+1) == '=')
                        tokens.add(new Token(TokenType.OP_ADD_ASSIGN, "+=", i++));
                    else
                        tokens.add(new Token(TokenType.OP_ADD, "+", i));
                    break;
                case '-':
                    if(i != codelen && code.charAt(i+1) == '=')
                        tokens.add(new Token(TokenType.OP_SUB_ASSIGN, "-=", i++));
                    else
                        tokens.add(new Token(TokenType.OP_SUB, "-", i));
                    break;
                case '*':
                    if(i != codelen && code.charAt(i+1) == '=')
                        tokens.add(new Token(TokenType.OP_MUL_ASSIGN, "*=", i++));
                    else
                        tokens.add(new Token(TokenType.OP_MUL, "*", i));
                    break;
                case '/':
                    if(i != codelen && code.charAt(i+1) == '=')
                        tokens.add(new Token(TokenType.OP_DIV_ASSIGN, "/=", i++));
                    else
                        tokens.add(new Token(TokenType.OP_DIV, "/", i));
                    break;
                case '%':
                    if(i != codelen && code.charAt(i+1) == '=')
                        tokens.add(new Token(TokenType.OP_MOD_ASSIGN, "%=", i++));
                    else
                        tokens.add(new Token(TokenType.OP_MOD, "%", i));
                    break;
                case '=':
                    tokens.add(new Token(TokenType.OP_EQ, "=", i));
                    break;
                case '<':
                    if(i != codelen){
                        switch(code.charAt(i+1)){
                            case '<' -> tokens.add(new Token(TokenType.BOP_SHL, "<<", i++));
                            case '>' -> tokens.add(new Token(TokenType.OP_DIFF, "<>", i++));
                            case '=' -> tokens.add(new Token(TokenType.OP_LE, "<=", i++));
                            default -> tokens.add(new Token(TokenType.OP_LT, "<", i));
                        }
                    }else
                        tokens.add(new Token(TokenType.OP_LT, "<", i));
                    break;
                case '>':
                    if(i != codelen){
                        switch(code.charAt(i+1)){
                            case '>' -> tokens.add(new Token(TokenType.BOP_SHR, ">>", i++));
                            case '=' -> tokens.add(new Token(TokenType.OP_GE, ">=", i++));
                            default -> tokens.add(new Token(TokenType.OP_GT, ">", i));
                        }
                    }else
                        tokens.add(new Token(TokenType.OP_GT, ">", i));
                    break;
                case ':':
                    if(i != codelen && code.charAt(i+1) == '=')
                        tokens.add(new Token(TokenType.OP_ASSIGN, ":=", i++));
                    else
                        tokens.add(new Token(TokenType.DELIMITER, ":", i));
                    break;
                default:
                    cst = i;
                    break;
            }
        }

        return tokens;
    }

    enum TokenType{
        LIT_NUMERIC,
        LIT_STRING,
        OP_ADD,
        OP_SUB,
        OP_MUL,
        OP_DIV,
        OP_MOD,
        OP_ASSIGN,
        OP_ADD_ASSIGN,
        OP_SUB_ASSIGN,
        OP_MUL_ASSIGN,
        OP_DIV_ASSIGN,
        OP_MOD_ASSIGN,
        OP_AND,
        OP_OR,
        OP_NOT,
        OP_EQ,
        OP_DIFF,
        OP_LT,
        OP_GT,
        OP_LE,
        OP_GE,
        BOP_AND,
        BOP_OR,
        BOP_NEG,
        BOP_SHL,
        BOP_SHR,
        KWRD_PROGRAM,
        KWRD_PROCEDURE,
        KWRD_BEGIN,
        KWRD_END,
        KWRD_IF,
        KWRD_ELSE,
        KWRD_HALT,
        KWRD_CONST,
        KWRD_VAR,
        KWRD_UNTIL,
        KWRD_ARRAY,
        KWRD_OF,
        KRWD_FOR,
        KWRD_TO,
        KWRD_DO,
        KWRD_THEN,
        VAR_IDENTIFIER,
        VARTYPE_INT,
        VARTYPE_REAL,
        VARTYPE_BOOL,
        VARTYPE_CHAR,
        DELIMITER,
        COMMENT
    }

    static class Token{
        private final TokenType type;
        private final String identifier;
        private final int position;

        public Token(TokenType type, String identifier, int position) {
            this.type = type;
            this.identifier = identifier;
            this.position = position;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", identifier='" + identifier + '\'' +
                    ", position=" + position +
                    '}';
        }
    }
}
