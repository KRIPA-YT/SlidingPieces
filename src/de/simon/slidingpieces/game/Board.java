package de.simon.slidingpieces.game;

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

    /**
     * Constructor of Board
     * @param starting The starting position of the board
     * @throws IllegalArgumentException Throws IllegalArgumentException when the amount of 2x1 pieces is more than MAX_2x1_PIECES
     */
    public Board(@NotNull Table<Integer, Integer, Piece> starting) throws IllegalArgumentException {
        int amount2x1Pieces = 0;
        for (Piece piece : starting.values()) {
            if (piece.getX() == 1 && piece.getY() == 2) {
                amount2x1Pieces++;
            } else if (piece.getX() == 2 && piece.getY() == 1) {
                amount2x1Pieces++;
            }
        }
        if (amount2x1Pieces > MAX_2x1_PIECES) {
            throw new IllegalArgumentException("Too many 2x1 pieces, Maximum is " + MAX_2x1_PIECES);
        }
        this.pieces = starting;
    }

    private boolean validMove(Piece piece, Direction dir) {
        // Calculate blank spots, including the spots the piece itself is on
        List<Piece.Position> blank = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                boolean found = false;
                for (Piece p : pieces.values()) {
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
     */
    public boolean move(int x, int y, Direction dir) {
        Piece piece = this.pieces.get(x, y);
        if (!validMove(piece, dir)) {
            return false;
        }
        piece.move(dir);
        return true;
    }
}
