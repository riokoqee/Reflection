package main;

final class ApartmentRoom {

    final int id;
    final String title;
    final int leftCol;
    final int topRow;
    final int rightExclusiveCol;
    final int bottomExclusiveRow;
    final boolean lockCamera;

    ApartmentRoom(int id, String title, int leftCol, int topRow, int rightExclusiveCol,
                  int bottomExclusiveRow, boolean lockCamera) {
        this.id = id;
        this.title = title;
        this.leftCol = leftCol;
        this.topRow = topRow;
        this.rightExclusiveCol = rightExclusiveCol;
        this.bottomExclusiveRow = bottomExclusiveRow;
        this.lockCamera = lockCamera;
    }

    boolean contains(int col, int row) {
        return col >= leftCol &&
                col < rightExclusiveCol &&
                row >= topRow &&
                row < bottomExclusiveRow;
    }
}
