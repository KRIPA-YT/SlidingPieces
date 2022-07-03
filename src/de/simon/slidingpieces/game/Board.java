package de.simon.slidingpieces.game;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The Board the game is going to be played on
 */
@Data
public class Board {
    public static final int HEIGHT = 5;
    public static final int WIDTH = 4;
    public static final int MAX_2x1_PIECES = 5;

    private Table<Integer, Integer, Piece> pieces;

    public Board(@NotNull List<Piece> starting) throws IllegalArgumentException {
        this(convertListToTable(starting));
    }

    /**
     * Constructor of Board
     * @param starting The starting position of the board
     * @throws IllegalArgumentException Throws IllegalArgumentException when the amount of 2x1 pieces is more than MAX_2x1_PIECES
     */
    public Board(@NotNull Table<Integer, Integer, Piece> starting) throws IllegalArgumentException {
        // Check if the amount of 2x1 Pieces is not bigger than MAX_2x1_PIECES
        int amount2x1Pieces = 0;
        for (Piece piece : starting.values()) {
            if (piece.getWidth() == 1 && piece.getHeight() == 2) {
                amount2x1Pieces++;
            } else if (piece.getWidth() == 2 && piece.getHeight() == 1) {
                amount2x1Pieces++;
            }
        }
        if (amount2x1Pieces > MAX_2x1_PIECES) {
            throw new IllegalArgumentException("Too many 2x1 pieces, Maximum is " + MAX_2x1_PIECES);
        }
        // Check that there is only one 2x2 piece
        int amount2x2Pieces = 0;
        for(Piece piece : starting.values()) {
            if (piece.getWidth() == 2 && piece.getHeight() == 2) {
                amount2x2Pieces++;
            }
        }
        if (amount2x2Pieces > MAX_2x1_PIECES) {
            throw new IllegalArgumentException("Too many 2x2 pieces, Maximum is 1");
        }
        if (amount2x2Pieces <= 0) {
            throw new IllegalArgumentException("No 2x2 piece available");
        }


        this.pieces = starting;
    }

    private static Table<Integer, Integer, Piece> convertListToTable(List<Piece> pieceList) {
        Table<Integer, Integer, Piece> pieces = HashBasedTable.create();
        for (Piece piece : pieceList) {
            pieces.put(piece.getX(), piece.getY(), piece);
        }
        return pieces;
    }

    private Piece.Position[] blankSpots(Piece[] pieces) {
        List<Piece.Position> blank = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                boolean found = false;
                for (Piece p : pieces) {
                    if(p.checkPos(x, y)) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    blank.add(new Piece.Position(x, y));
                }
            }
        }
        return blank.toArray(new Piece.Position[0]);
    }

    private boolean validMove(@NotNull Piece piece, Direction dir) {
        // Calculate blank spots, including the spots the piece itself is on
        Piece.Position[] blankSpots = blankSpots(this.pieces.values().toArray(new Piece[this.pieces.size()]));
        List<Piece.Position> blank = new ArrayList<>(List.of(blankSpots));
        blank.addAll(List.of(piece.getAllPositions()));

        // Check if piece is going to end up on a blank spot
        boolean notOutOfBounds = piece.move(dir);
        Piece.Position[] endPos = piece.getAllPositions();
        return new HashSet<>(blank).containsAll(List.of(endPos)) && notOutOfBounds;
    }

    /**
     * Move one piece around
     * @param x The x coordinate of the piece
     * @param y The y coordinate of the piece
     * @param dir The Direction the piece is going to be moved
     * @return if it moved successfully
     * @throws IllegalArgumentException when x and y point to a blank spot
     */
    public boolean move(int x, int y, Direction dir) throws IllegalArgumentException {
        Piece piece = this.pieces.get(x, y);
        if (piece == null) {
            // Check if spot is blank
            for (Piece.Position pos : blankSpots(pieces.values().toArray(new Piece[0]))) {
                if (pos.equals(new Piece.Position(x, y))) {
                    throw new IllegalArgumentException("x and y coordinate point to a blank spot");
                }
            }

            // Get which piece occupies that space
            boolean found = false;
            for (Piece p : pieces.values()) {
                for (Piece.Position pos : p.getAllPositions()) {
                    if (pos.equals(new Piece.Position(x, y))) {
                        piece = p;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
        }

        if (!validMove(piece, dir)) {
            return false;
        }
        piece.move(dir);
        return true;
    }

    public boolean won() {
        Piece winningPiece = pieces.get(HEIGHT - 2, WIDTH / 2);
        return ((winningPiece.getHeight() == 2) && (winningPiece.getWidth() == 2));
    }

}
