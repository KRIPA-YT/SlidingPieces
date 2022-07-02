package de.simon.slidingpieces.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    public static final int MAX_HEIGHT = 2;
    public static final int MAX_WIDTH = 2;

    @Getter private final int x;
    @Getter private final int y;
    @Getter private final int width;
    @Getter private final int height;

    public Piece(int x, int y, int height, int width) {
        // Check if x is valid
        if (x > Board.WIDTH || x < 0) {
            throw new IllegalArgumentException("x has to be between 0 and " + Board.WIDTH);
        }
        // Check if y is valid
        if (y > Board.HEIGHT || y < 0) {
            throw new IllegalArgumentException("y has to be between 0 and " + Board.HEIGHT);
        }
        // Check if height is valid
        if(height > MAX_HEIGHT || height < 0) {
            throw new IllegalArgumentException("height has to be between 0 and " + MAX_HEIGHT);
        }
        // Check if width is valid
        if(width > MAX_WIDTH || width < 0) {
            throw new IllegalArgumentException("width has to be between 0 and " + MAX_WIDTH);
        }
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public boolean checkPos(int x, int y) {
        boolean xInc = (x >= this.x && x <= this.x + this.width);
        boolean yInc = (y >= this.y && y <= this.y + this.height);
        return xInc && yInc;
    }

    public Position calcTargetPos(@NotNull Direction dir) {
        int x = this.x;
        int y = this.y;
        int prevX = this.x;
        int prevY = this.y;
        switch (dir) {
            case UP -> y--;
            case DOWN -> y++;
            case RIGHT -> x++;
            case LEFT -> x--;
        }
        if (x < 0 || x > Board.WIDTH || y < 0 || y > Board.HEIGHT) {
            x = prevX;
            y = prevY;
        }
        return new Position(x, y);
    }

    public boolean move(@NotNull Direction dir) {
        return (!calcTargetPos(dir).equals(new Position(this.x, this.y)));
    }

    public Position[] getAllPositions() {
        List<Position> positions = new ArrayList<>();
        for(int xShift = 0; xShift < this.width; xShift++) {
            for(int yShift = 0; yShift < this.height; yShift++) {
                positions.add(new Position(this.x + xShift, this.y + yShift));
            }
        }

        return (Position[]) positions.toArray();
    }

    @AllArgsConstructor
    protected static class Position {
        @Setter @Getter private int x;
        @Setter @Getter private int y;
    }
}
