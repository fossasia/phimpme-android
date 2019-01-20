package org.fossasia.phimpme.editor.font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class FontManager {

  public static HashMap<String, String> getFontsMap() {
    String[] fontdirs = {"/system/fonts", "/system/font", "/data/fonts"};
    HashMap<String, String> fonts = new HashMap<>();
    TTFAnalyzer analyzer = new TTFAnalyzer();

    for (String fontdir : fontdirs) {
      File dir = new File(fontdir);

      if (!dir.exists()) continue;

      File[] files = dir.listFiles();

      if (files == null) continue;

      for (File file : files) {
        String fontname = analyzer.getTtfFontName(file.getAbsolutePath());

        if (fontname != null) fonts.put(file.getAbsolutePath(), fontname);
      }
    }

    return fonts.isEmpty() ? null : fonts;
  }
}

class TTFAnalyzer {

  // Font file; must be seekable
  private RandomAccessFile m_file = null;

  public String getTtfFontName(String fontFilename) {
    try {

      m_file = new RandomAccessFile(fontFilename, "r");
      int version = readDword();

      // The version must be either 'true' (0x74727565) or 0x00010000
      if (version != 0x74727565 && version != 0x00010000) return null;

      // The TTF file consist of several sections called "tables", and we need to know how many of
      // them are there.
      int numTables = readWord();

      // Skip the rest in the header
      readWord(); // skip searchRange
      readWord(); // skip entrySelector
      readWord(); // skip rangeShift

      // Now we can read the tables
      for (int i = 0; i < numTables; i++) {
        // Read the table entry
        int tag = readDword();
        readDword();
        int offset = readDword();
        int length = readDword();

        // Now here' the trick. 'name' field actually contains the textual string name.
        // So the 'name' string in characters equals to 0x6E616D65
        if (tag == 0x6E616D65) {
          // Here's the name section. Read it completely into the allocated buffer
          byte[] table = new byte[length];

          m_file.seek(offset);
          read(table);

          int count = getWord(table, 2);
          int string_offset = getWord(table, 4);

          // Record starts from offset 6
          for (int record = 0; record < count; record++) {

            int nameid_offset = record * 12 + 6;
            int platformID = getWord(table, nameid_offset);
            int nameid_value = getWord(table, nameid_offset + 6);

            // Table 42 lists the valid name Identifiers. We're interested in 4 but not in Unicode
            // encoding (for simplicity).
            // The encoding is stored as PlatformID and we're interested in Mac encoding
            if (nameid_value == 4 && platformID == 1) {
              // We need the string offset and length, which are the word 6 and 5 respectively
              int name_length = getWord(table, nameid_offset + 8);
              int name_offset = getWord(table, nameid_offset + 10);

              // The real name string offset is calculated by adding the string_offset
              name_offset = name_offset + string_offset;

              // Make sure it is inside the array
              if (name_offset >= 0 && name_offset + name_length < table.length)
                return new String(table, name_offset, name_length);
            }
          }
        }
      }

      return null;
    } catch (FileNotFoundException e) {
      // Permissions?
      return null;
    } catch (IOException e) {
      // Most likely a corrupted font file
      return null;
    }
  }

  // Helper I/O functions
  private int readByte() throws IOException {
    return m_file.read() & 0xFF;
  }

  private int readWord() throws IOException {
    int b1 = readByte();
    int b2 = readByte();

    return b1 << 8 | b2;
  }

  private int readDword() throws IOException {
    int b1 = readByte();
    int b2 = readByte();
    int b3 = readByte();
    int b4 = readByte();

    return b1 << 24 | b2 << 16 | b3 << 8 | b4;
  }

  private void read(byte[] array) throws IOException {
    if (m_file.read(array) != array.length) throw new IOException();
  }

  // Helper
  private int getWord(byte[] array, int offset) {
    int b1 = array[offset] & 0xFF;
    int b2 = array[offset + 1] & 0xFF;

    return b1 << 8 | b2;
  }
}
