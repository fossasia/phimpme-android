package org.fossasia.phimpme.editor.model;

import com.mikepenz.iconics.IconicsDrawable;

public class RatioItem {
	private String text;
	private Float ratio;
	private int index;
    private IconicsDrawable iconicsDrawable;

	public RatioItem(String text, Float ratio, IconicsDrawable iconicsDrawable) {
		super();
		this.text = text;
		this.ratio = ratio;
        this.iconicsDrawable=iconicsDrawable;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Float getRatio() {
		return ratio;
	}

	public void setRatio(Float ratio) {
		this.ratio = ratio;
	}
	

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setImage(IconicsDrawable iconicsDrawable){
        this.iconicsDrawable=iconicsDrawable;
    }

    public IconicsDrawable getImage(){
        return iconicsDrawable;
    }
}