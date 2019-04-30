package com.limelion.anscapes;

import java.awt.Color;
import java.io.IOException;

/**
 * Contains about everything you need to manipulate the terminal using ansi escape codes.
 */
public class Anscapes {

    public static final String CSI = "\033[",
        RESET = CSI + "m",
        CLEAR = CSI + "2J",
        CLEAR_BUFFER = CSI + "3J",
        RESET_CURSOR = CSI + "H",
        CLEAR_LINE = CSI + "2K",
        MOVE_UP = CSI + "A",
        MOVE_DOWN = CSI + "B",
        MOVE_RIGHT = CSI + "C",
        MOVE_LEFT = CSI + "D",
        MOVE_LINEUP = CSI + "E",
        MOVE_LINEDOWN = CSI + "F",
        BOLD = CSI + "1m",
        FAINT = CSI + "2m",
        ITALIC = CSI + "3m",
        UNDERLINE = CSI + "4m",
        BLINK_SLOW = CSI + "5m",
        BLINK_FAST = CSI + "6m",
        SWAP_COLORS = CSI + "7m",
        DEFAULT_FONT = CSI + "10m",
        FRAKTUR = CSI + "20m",
        UNDERLINE_DOUBLE = CSI + "21m",
        NORMAL = CSI + "22m",
        ITALIC_OFF = CSI + "23m",
        UNDERLINE_OFF = CSI + "24m",
        BLINK_OFF = CSI + "25m",
        INVERSE_OFF = CSI + "26m",
        DEFAULT_FOREGROUND = CSI + "39m",
        DEFAULT_BACKGROUND = CSI + "49m",
        FRAMED = CSI + "51m",
        ENCIRCLED = CSI + "52m",
        OVERLINED = CSI + "53m",
        FRAMED_OFF = CSI + "54m",
        OVERLINED_OFF = CSI + "55m";

    /**
     * Select an alternative font.
     *
     * @param n
     *     the font number to use. Between 0 and 9 where 0 is the default font.
     *
     * @return the corresponding ansi escape code.
     */
    public static String alternativeFont(int n) {

        if (n < 0 || n > 9)
            throw new IllegalArgumentException("Font number should be between 0 and 9.");

        return CSI + (n + 10) + 'm';
    }

    /**
     * Move cursor up n cells.
     *
     * @param n
     *     the number of cells
     *
     * @return the corresponding ansi escape code.
     */
    public static String moveUp(int n) {

        return CSI + n + "A";
    }

    /**
     * Move cursor down n cells.
     *
     * @param n
     *     the number of cells
     *
     * @return the corresponding ansi escape code.
     */
    public static String moveDown(int n) {

        return CSI + n + "B";
    }

    /**
     * Move cursor right n cells.
     *
     * @param n
     *     the number of cells
     *
     * @return the corresponding ansi escape code.
     */
    public static String moveRight(int n) {

        return CSI + n + "C";
    }

    /**
     * Move cursor left n cells.
     *
     * @param n
     *     the number of cells
     *
     * @return the corresponding ansi escape code.
     */
    public static String moveLeft(int n) {

        return CSI + n + "D";
    }

    /**
     * Move cursor n lines after.
     *
     * @param n
     *     the number of lines
     *
     * @return the corresponding ansi escape code.
     */
    public static String moveNextLine(int n) {

        return CSI + n + "E";
    }

    /**
     * Move cursor n lines before.
     *
     * @param n
     *     the number of lines
     *
     * @return the corresponding ansi escape code.
     */
    public static String movePreviousLine(int n) {

        return CSI + n + "F";
    }

    /**
     * Move cursor at given row and col.
     *
     * @param row
     *     the row number
     * @param col
     *     the column number
     *
     * @return the corresponding ansi escape code.
     */
    public static String cursorPos(int row, int col) {

        return CSI + row + ";" + col + "H";
    }

    /**
     * Retrieves cursor position (experimental)
     *
     * @return the cursor position.
     */
    public static CursorPos cursorPos() {

        System.out.print(CSI + "6n");
        try {
            System.in.read();
            System.in.read();
            int read = -1;
            StringBuilder row = new StringBuilder();
            while ((read = System.in.read()) != ';') {
                row.append((char) read);
            }
            StringBuilder col = new StringBuilder();
            while ((read = System.in.read()) != 'R') {
                col.append((char) read);
            }
            return new CursorPos(row.length() > 0 ? Integer.parseInt(row.toString()) : 1,
                                 col.length() > 0 ? Integer.parseInt(col.toString()) : 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a new AnsiColor from its rgb code, only for terminals supporting 24bit color.
     *
     * @param r
     *     the red component
     * @param g
     *     the green component
     * @param b
     *     the blue component
     *
     * @return the corresponding ansi escape code
     */
    public static AnsiColor rgb(int r, int g, int b) {

        return new AnsiColor() {

            @Override
            public Color color() {

                return new Color(r, g, b);
            }

            @Override
            public String fg() {

                return Anscapes.CSI + "38;2;" + r + ';' + g + ';' + b + 'm';
            }

            @Override
            public String bg() {

                return Anscapes.CSI + "48;2;" + r + ';' + g + ';' + b + 'm';
            }
        };
    }

    /**
     * @param c
     *     the color to convert
     * @param threshold
     *     distance to evaluate a spot-on
     *
     * @return the nearest ansi color
     */
    public static Colors findNearestColor(Color c, int threshold) {

        // TODO allow user to use its own color palette for ansi colors.

        Colors closest = null;
        float closestDist = Float.MAX_VALUE;

        for (Colors ansic : Colors.values()) {

            float dist = (float) Math.sqrt(Math.pow(ansic.color().getRed() - c.getRed(), 2) +
                                           Math.pow(ansic.color().getGreen() - c.getGreen(), 2) +
                                           Math.pow(ansic.color().getBlue() - c.getBlue(), 2));

            // Speedup, if low distance its a spot-on
            if (dist < threshold) {
                return ansic;
            }

            if (dist < closestDist) {
                closestDist = dist;
                closest = ansic;
            }
        }
        return closest;
    }

    /**
     * All 16 ANSI colors. RGB equivalents are taken to optimize approximation and are totally arbitrary.
     */
    public enum Colors implements AnsiColor {

        BLACK(30, new Color(0, 0, 0)),
        RED(31, new Color(178, 0, 0)),
        GREEN(32, new Color(50, 184, 26)),
        YELLOW(33, new Color(185, 183, 26)),
        BLUE(34, new Color(0, 21, 182)),
        MAGENTA(35, new Color(177, 0, 182)),
        CYAN(36, new Color(47, 186, 184)),
        WHITE(37, new Color(184, 184, 184)),

        BLACK_BRIGHT(90, new Color(58, 58, 58)),
        RED_BRIGHT(91, new Color(247, 48, 58)),
        GREEN_BRIGHT(92, new Color(89, 255, 68)),
        YELLOW_BRIGHT(93, new Color(255, 255, 67)),
        BLUE_BRIGHT(94, new Color(85, 91, 253)),
        MAGENTA_BRIGHT(95, new Color(246, 55, 253)),
        CYAN_BRIGHT(96, new Color(86, 255, 255)),
        WHITE_BRIGHT(97, new Color(255, 255, 255));

        private final int value;
        private final Color c;

        Colors(int value, Color c) {

            this.value = value;
            this.c = c;
        }

        @Override
        public Color color() {

            return c;
        }

        @Override
        public String fg() {

            return Anscapes.CSI + value + 'm';
        }

        @Override
        public String bg() {

            return Anscapes.CSI + (value + 10) + 'm';
        }
    }

    public static class CursorPos {

        private int row;
        private int col;

        public CursorPos(int row, int col) {

            this.row = row;
            this.col = col;
        }

        public int row() {

            return row;
        }

        public int col() {

            return col;
        }
    }
}
