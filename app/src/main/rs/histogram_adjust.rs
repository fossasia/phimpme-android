#pragma version(1)
#pragma rs java_package_name(vn.mbm.phimp.me.opencamera)
#pragma rs_fp_relaxed

rs_allocation c_histogram;

float hdr_alpha = 0.5f; // 0.0 means no change, 1.0 means fully equalise

// Global histogram equalisation:

/*uchar4 __attribute__((kernel)) histogram_adjust(uchar4 in, uint32_t x, uint32_t y) {
	float in_r = in.r;
	float in_g = in.g;
	float in_b = in.b;
	float value = fmax(in_r, in_g);
	value = fmax(value, in_b);
	int cdf_v = rsGetElementAt_int(c_histogram, value);
	int cdf_0 = rsGetElementAt_int(c_histogram, 0);
	int n_pixels = rsGetElementAt_int(c_histogram, 255);
	float num = (float)(cdf_v - cdf_0);
	float den = (float)(n_pixels - cdf_0);
	int equal_value = (int)( 255.0f * (num/den) ); // value that we should choose to fully equalise the histogram
	
	int new_value = (int)( (1.0f-hdr_alpha) * value + hdr_alpha * equal_value );
	
	float scale = ((float)new_value) / (float)value;

	uchar4 out;
	out.r = min(255, (int)(in.r * scale));
	out.g = min(255, (int)(in.g * scale));
	out.b = min(255, (int)(in.b * scale));
	
	return out;
}*/

// Local histogram equalisation:

int n_tiles = 0;
int width = 0;
int height = 0;

static int getEqualValue(int histogram_offset, int value) {
	int cdf_v = rsGetElementAt_int(c_histogram, histogram_offset+value);
	int cdf_0 = rsGetElementAt_int(c_histogram, histogram_offset);
	int n_pixels = rsGetElementAt_int(c_histogram, histogram_offset+255);
	float num = (float)(cdf_v - cdf_0);
	float den = (float)(n_pixels - cdf_0);
	int equal_value = (int)( 255.0f * (num/den) ); // value that we should choose to fully equalise the histogram
	return equal_value;
}

uchar4 __attribute__((kernel)) histogram_adjust(uchar4 in, uint32_t x, uint32_t y) {
	uchar value = max(in.r, in.g);
	value = max(value, in.b);

	float tx = ((float)x*n_tiles)/(float)width - 0.5f;
	float ty = ((float)y*n_tiles)/(float)height - 0.5f;
	
	int ix = (int)floor(tx);
	int iy = (int)floor(ty);
	int equal_value = 0;
	if( ix >= 0 && ix < n_tiles-1 && iy >= 0 && iy < n_tiles-1 ) {
		int histogram_offset00 = 256*(ix*n_tiles+iy);
		int histogram_offset10 = 256*((ix+1)*n_tiles+iy);
		int histogram_offset01 = 256*(ix*n_tiles+iy+1);
		int histogram_offset11 = 256*((ix+1)*n_tiles+iy+1);
		int equal_value00 = getEqualValue(histogram_offset00, value);
		int equal_value10 = getEqualValue(histogram_offset10, value);
		int equal_value01 = getEqualValue(histogram_offset01, value);
		int equal_value11 = getEqualValue(histogram_offset11, value);
		float alpha = tx - ix;
		float beta = ty - iy;
		
		float equal_value0 = (1.0f-alpha)*equal_value00 + alpha*equal_value10;
		float equal_value1 = (1.0f-alpha)*equal_value01 + alpha*equal_value11;
		equal_value = (1.0f-beta)*equal_value0 + beta*equal_value1;
	}
	else if( ix >= 0 && ix < n_tiles-1 ) {
		int this_y = (iy<0) ? iy+1 : iy;
		int histogram_offset0 = 256*(ix*n_tiles+this_y);
		int histogram_offset1 = 256*((ix+1)*n_tiles+this_y);
		int equal_value0 = getEqualValue(histogram_offset0, value);
		int equal_value1 = getEqualValue(histogram_offset1, value);
		float alpha = tx - ix;
		equal_value = (1.0f-alpha)*equal_value0 + alpha*equal_value1;
	}
	else if( iy >= 0 && iy < n_tiles-1 ) {
		int this_x = (ix<0) ? ix+1 : ix;
		int histogram_offset0 = 256*(this_x*n_tiles+iy);
		int histogram_offset1 = 256*(this_x*n_tiles+iy+1);
		int equal_value0 = getEqualValue(histogram_offset0, value);
		int equal_value1 = getEqualValue(histogram_offset1, value);
		float beta = ty - iy;
		equal_value = (1.0f-beta)*equal_value0 + beta*equal_value1;
	}
	else {
		int this_x = (ix<0) ? ix+1 : ix;
		int this_y = (iy<0) ? iy+1 : iy;
		int histogram_offset = 256*(this_x*n_tiles+this_y);
		equal_value = getEqualValue(histogram_offset, value);
	}
	
	int new_value = (int)( (1.0f-hdr_alpha) * value + hdr_alpha * equal_value );

	float scale = ((float)new_value) / (float)value;

	uchar4 out;
	// need to add +0.5 so that we round to nearest - particularly important as due to floating point rounding, we
	// can end up with incorrect behaviour even when new_value==value!
	out.r = min(255, (int)(in.r * scale + 0.5f));
	out.g = min(255, (int)(in.g * scale + 0.5f));
	out.b = min(255, (int)(in.b * scale + 0.5f));
	
	return out;
}
