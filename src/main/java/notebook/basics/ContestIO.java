package notebook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/** Fast byte input for ASCII contest tokens. Use PrintWriter/StringBuilder for output. */
public final class ContestIO {
    private final InputStream in;
    private final byte[] buffer = new byte[1 << 16];
    private int pos, len;

    public ContestIO(InputStream input) { in = new BufferedInputStream(input); }

    private int read() throws IOException {
        if (pos >= len) {
            len = in.read(buffer);
            pos = 0;
            if (len < 0) return -1;
        }
        return buffer[pos++] & 0xff;
    }

    /** Returns null at EOF. Tokens are decoded as ASCII. */
    public String next() throws IOException {
        int c;
        do { c = read(); } while (c >= 0 && c <= ' ');
        if (c < 0) return null;
        StringBuilder token = new StringBuilder();
        while (c > ' ') { token.append((char) c); c = read(); }
        return token.toString();
    }

    public int nextInt() throws IOException { return Integer.parseInt(next()); }
    public long nextLong() throws IOException { return Long.parseLong(next()); }
}
