import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static HashMap<String, TokenType> keywords = new HashMap<>();
    public static Pattern LITERAL_INT_PATTERN = Pattern.compile("^-?[$%&]?\\d+");
    public static Pattern LITERAL_REAL_PATTERN = Pattern.compile("^-?\\d+(((\\.\\d+)?e-?\\d+)|(\\.\\d+))");
    public static Pattern IDENTIFIER_PATTERN = Pattern.compile("^\\D[^()\\[\\].:;,=+\\-*/<>\\s]*");

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

        keywords.put("and", TokenType.OP_AND);
        keywords.put("or", TokenType.OP_OR);
        keywords.put("not", TokenType.OP_NOT);
        keywords.put("program", TokenType.PROGRAM);
        keywords.put("procedure", TokenType.PROCEDURE);
        keywords.put("begin", TokenType.BEGIN);
        keywords.put("end", TokenType.END);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("halt", TokenType.HALT);
        keywords.put("const", TokenType.CONST_VAR);
        keywords.put("var", TokenType.VAR);
        keywords.put("until", TokenType.UNTIL);
        keywords.put("array", TokenType.ARRAY);
        keywords.put("of", TokenType.OF);
        keywords.put("for", TokenType.FOR);
        keywords.put("repeat", TokenType.REPEAT);
        keywords.put("to", TokenType.TO);
        keywords.put("do", TokenType.DO);
        keywords.put("then", TokenType.THEN);
        keywords.put("integer", TokenType.VARTYPE_INT);
        keywords.put("real", TokenType.VARTYPE_REAL);
        keywords.put("boolean", TokenType.VARTYPE_BOOL);
        keywords.put("true", TokenType.LITERAL_BOOL);
        keywords.put("false", TokenType.LITERAL_BOOL);

        analyze(txt.toString()).forEach(System.out::println);
    }

    public static List<Token> analyze(String code){
        ArrayList<Token> tokens = new ArrayList<>();

        char c, cc;
        int cst, codelen = code.length()-1;
        StringBuilder temp;
        String substr;
        Matcher matcher;

        for (int i = 0; i <= codelen; i++) {
            c = code.charAt(i);

            switch(c){
                case ' ':
                    break;
                case '{':
                    temp = new StringBuilder();
                    cst = i;
                    while((cc = code.charAt(++i)) != '}'){
                        temp.append(cc);
                        if(i == codelen){
                            System.err.println("Nie zamkniety komentarz! od pozycji" + cst);
                            break;
                        }
                    }
                    tokens.add(new Token(TokenType.COMMENT, temp.toString(), cst));
                    break;
                case '\'':
                    temp = new StringBuilder();
                    cst = i;
                    while((cc = code.charAt(++i)) != '\''){
                        temp.append(cc);
                        if(i == codelen){
                            System.err.println("Nie zamkniety ciąg znaków! od pozycji" + cst);
                            break;
                        }
                    }
                    tokens.add(new Token(TokenType.LITERAL_STRING, temp.toString(), cst));
                    break;
                case ';':
                    tokens.add(new Token(TokenType.EXPRESSION_END, ";", i));
                    break;
                case '.':
                    if(i != codelen && code.charAt(i+1) == '.') {
                        tokens.add(new Token(TokenType.RANGE, "..", i++));
                        break;
                    }
                case ',':
                case '(':
                case ')':
                case '[':
                case ']':
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
                    else {
                        substr = code.substring(i);
                        if((matcher = LITERAL_REAL_PATTERN.matcher(substr)).find()){
                            tokens.add(new Token(TokenType.LITERAL_REAL, matcher.group(), i));
                            i += matcher.end()-1;
                        }else if ((matcher = LITERAL_INT_PATTERN.matcher(substr)).find()) {
                            tokens.add(new Token(TokenType.LITERAL_INT, matcher.group(), i));
                            i += matcher.end()-1;
                        }else
                            tokens.add(new Token(TokenType.OP_SUB, "-", i));
                    }
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
                case '&':
                    tokens.add(new Token(TokenType.BOP_AND, "&", i));
                    break;
                case '|':
                case '!':
                    tokens.add(new Token(TokenType.BOP_OR, String.valueOf(c), i));
                    break;
                case '~':
                    tokens.add(new Token(TokenType.BOP_NEG, "~", i));
                    break;
                default:
                    substr = code.substring(i);
                    boolean hit = false;

                    for(Map.Entry<String, TokenType> e : keywords.entrySet()){
                        if(substr.startsWith(e.getKey())){
                            tokens.add(new Token(e.getValue(), e.getKey(), i));
                            i += e.getKey().length() - 1;
                            hit = true;
                            break;
                        }
                    }

                    if(!hit){
                        if((matcher = LITERAL_REAL_PATTERN.matcher(substr)).find()){
                            tokens.add(new Token(TokenType.LITERAL_REAL, matcher.group(), i));
                        }else if ((matcher = LITERAL_INT_PATTERN.matcher(substr)).find()) {
                            tokens.add(new Token(TokenType.LITERAL_INT, matcher.group(), i));
                        }else if ((matcher = IDENTIFIER_PATTERN.matcher(substr)).find()) {
                            tokens.add(new Token(TokenType.IDENTIFIER, matcher.group(), i));
                        } else {
                            System.err.println("Nie poprawne wyrazenie od pozycji " + i);
                            return tokens;
                        }

                        i += matcher.end()-1;
                    }
                    break;
            }
        }

        return tokens;
    }

    enum TokenType{
        LITERAL_INT(true),
        LITERAL_REAL(true),
        LITERAL_STRING(true),
        LITERAL_BOOL(true),
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
        PROGRAM,
        PROCEDURE,
        BEGIN,
        END,
        IF,
        ELSE,
        HALT,
        CONST_VAR,
        VAR,
        UNTIL,
        ARRAY,
        OF,
        FOR,
        REPEAT,
        TO,
        DO,
        THEN,
        VARTYPE_INT,
        VARTYPE_REAL,
        VARTYPE_BOOL,
        EXPRESSION_END,
        RANGE,
        IDENTIFIER(true),
        DELIMITER(true),
        COMMENT(true);

        public final boolean needsIdentifier;

        TokenType(boolean needsIdentifier){
            this.needsIdentifier = needsIdentifier;
        }

        TokenType(){
            this.needsIdentifier = false;
        }
    }

    record Token(TokenType type, String identifier, int position) {

        @Override
            public String toString() {
                return position + ": " + type + (type.needsIdentifier ? " '" + identifier + "'" : "");
            }
        }
}
