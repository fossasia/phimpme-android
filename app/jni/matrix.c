// Based on work preseted here presented http://www.graficaobscura.com/matrix/index.html
// By Paul Haeberli - 1993

#include <math.h>
#include "bitmap.h"

#define RLUM    (0.3086f)
#define GLUM    (0.6094f)
#define BLUM    (0.0820f)

void applyMatrix(Bitmap* bitmap, float matrix[4][4])
{
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned int length = (*bitmap).width * (*bitmap).height;

    float r1, g1, b1, r2, g2, b2;

    unsigned int i;
    for(i = 0; i < length; i++) {
		r1 = red[i];
		g1 = green[i];
		b1 = blue[i];
		r2 = r1*matrix[0][0] + g1*matrix[1][0] + b1*matrix[2][0] + matrix[3][0];
		g2 = r1*matrix[0][1] + g1*matrix[1][1] + b1*matrix[2][1] + matrix[3][1];
		b2 = r1*matrix[0][2] + g1*matrix[1][2] + b1*matrix[2][2] + matrix[3][2];
		if(r2<0) r2 = 0;
		if(r2>255) r2 = 255;
		if(g2<0) g2 = 0;
		if(g2>255) g2 = 255;
		if(b2<0) b2 = 0;
		if(b2>255) b2 = 255;
		red[i] = r2;
		green[i] = g2;
		blue[i] = b2;
    }
}

void applyMatrixToPixel(unsigned char* red, unsigned char* green, unsigned char* blue, float matrix[4][4])
{
    float r1, g1, b1, r2, g2, b2;

	r1 = (*red);
	g1 = (*green);
	b1 = (*blue);
	r2 = r1*matrix[0][0] + g1*matrix[1][0] + b1*matrix[2][0] + matrix[3][0];
	g2 = r1*matrix[0][1] + g1*matrix[1][1] + b1*matrix[2][1] + matrix[3][1];
	b2 = r1*matrix[0][2] + g1*matrix[1][2] + b1*matrix[2][2] + matrix[3][2];
	if(r2<0) r2 = 0;
	if(r2>255) r2 = 255;
	if(g2<0) g2 = 0;
	if(g2>255) g2 = 255;
	if(b2<0) b2 = 0;
	if(b2>255) b2 = 255;
	(*red) = r2;
	(*green) = g2;
	(*blue) = b2;
}

identMatrix(float *matrix) {
    *matrix++ = 1.0f;    /* row 1        */
    *matrix++ = 0.0f;
    *matrix++ = 0.0f;
    *matrix++ = 0.0f;
    *matrix++ = 0.0f;    /* row 2        */
    *matrix++ = 1.0f;
    *matrix++ = 0.0f;
    *matrix++ = 0.0f;
    *matrix++ = 0.0f;    /* row 3        */
    *matrix++ = 0.0f;
    *matrix++ = 1.0f;
    *matrix++ = 0.0f;
    *matrix++ = 0.0f;    /* row 4        */
    *matrix++ = 0.0f;
    *matrix++ = 0.0f;
    *matrix++ = 1.0f;
}

void saturateMatrix(float matrix[4][4], float* saturation)
{
	float sat = (*saturation);
    float mmatrix[4][4];
    float a, b, c, d, e, f, g, h, i;
    float rwgt, gwgt, bwgt;

    rwgt = RLUM;
    gwgt = GLUM;
    bwgt = BLUM;

    a = (1.0f-sat)*rwgt + sat;
    b = (1.0f-sat)*rwgt;
    c = (1.0f-sat)*rwgt;
    d = (1.0f-sat)*gwgt;
    e = (1.0f-sat)*gwgt + sat;
    f = (1.0f-sat)*gwgt;
    g = (1.0f-sat)*bwgt;
    h = (1.0f-sat)*bwgt;
    i = (1.0f-sat)*bwgt + sat;
    mmatrix[0][0] = a;
    mmatrix[0][1] = b;
    mmatrix[0][2] = c;
    mmatrix[0][3] = 0.0f;

    mmatrix[1][0] = d;
    mmatrix[1][1] = e;
    mmatrix[1][2] = f;
    mmatrix[1][3] = 0.0f;

    mmatrix[2][0] = g;
    mmatrix[2][1] = h;
    mmatrix[2][2] = i;
    mmatrix[2][3] = 0.0f;

    mmatrix[3][0] = 0.0f;
    mmatrix[3][1] = 0.0f;
    mmatrix[3][2] = 0.0f;
    mmatrix[3][3] = 1.0f;
    multiplyMatricies(mmatrix, matrix, matrix);
}

multiplyMatricies(float a[4][4], float b[4][4], float c[4][4]) {
    int x, y;
    float temp[4][4];

    for(y=0; y<4 ; y++) {
        for(x=0 ; x<4 ; x++) {
            temp[y][x] = b[y][0] * a[0][x]
                       + b[y][1] * a[1][x]
                       + b[y][2] * a[2][x]
                       + b[y][3] * a[3][x];
        }
    }

    for(y=0; y<4; y++) {
        for(x=0; x<4; x++) {
            c[y][x] = temp[y][x];
        }
    }
}
