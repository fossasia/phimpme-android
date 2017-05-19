package com.xinlan.imageeditlibrary.picchooser;

class GridItem {
    final String name;
    final String path;
    final String imageTaken;
    final long imageSize;
    public GridItem(final String n, final String p,final String imageTaken,final long imageSize) {
        name = n;
        path = p;
        this.imageTaken = imageTaken;
        this.imageSize = imageSize;
    }
}
