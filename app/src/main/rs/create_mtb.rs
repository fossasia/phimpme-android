#pragma version(1)
#pragma rs java_package_name(vn.mbm.phimp.me.opencamera)
#pragma rs_fp_relaxed

rs_allocation out_bitmap; // the resultant median threshold bitmap

int median_value = 0;
int start_x = 0, start_y = 0;

void __attribute__((kernel)) create_mtb(uchar4 in, uint32_t x, uint32_t y) {
	uchar value = max(in.r, in.g);
	value = max(value, in.b);

    uchar out;
    // take care of being unsigned!
    // ignore small differences to reduce effect of noise - this helps testHDR22
    int diff;
    if( value > median_value )
        diff = value - median_value;
    else
        diff = median_value - value;
    if( diff <= 4 )
        out = 127;
    else if( value <= median_value )
        out = 0;
    else
        out = 255;
    rsSetElementAt_uchar(out_bitmap, out, x - start_x, y - start_y);
	//return out;
}
