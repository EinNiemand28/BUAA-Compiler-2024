package frontend.Lexer;

import java.util.List;

public class TokenStream {
    private int pos;
    private final List<Token> tokenList;

    public TokenStream(List<Token> tokenList) {
        this.tokenList = tokenList;
        pos = 0;
    }

    public Token read() {
        if (pos < tokenList.size()) {
            return tokenList.get(pos++);
        } else {
            return null;
        }
    }

    public void unread() {
        if (pos > 0) {
            pos--;
        }
    }

    public void unread(int n) {
        if (pos - n > 0) {
            pos -= n;
        } else {
            pos = 0;
        }
    }

    public int getPos() {
        return pos;
    }
}
