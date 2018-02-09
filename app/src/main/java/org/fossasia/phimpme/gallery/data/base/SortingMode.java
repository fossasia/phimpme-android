package org.fossasia.phimpme.gallery.data.base;

/**
 * Created by dnld on 18/08/16.
 */

public enum SortingMode {
  NAME (0), DATE (1), SIZE(2);

  int value;

  SortingMode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static SortingMode fromValue(int value) {
    switch (value) {
      case 0: return NAME;
      case 1: default: return DATE;
      case 2: return SIZE;
    }
  }
}
