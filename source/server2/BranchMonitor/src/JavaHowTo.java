class JavaHowTo {

  public static final short FOREGROUND_BLACK = 0x0;
  public static final short FOREGROUND_BLUE  = 0x1;
  public static final short FOREGROUND_GREEN = 0x2;
  public static final short FOREGROUND_RED   = 0x4;
  public static final short FOREGROUND_WHITE = 0x7;
  public static final short FOREGROUND_INTENSITY = 0x8;

  public static final short BACKGROUND_BLUE  = 0x10;
  public static final short BACKGROUND_GREEN = 0x20;
  public static final short BACKGROUND_RED   = 0x40;
  public static final short BACKGROUND_INTENSITY = 0x80;
  public static final short BACKGROUND_BLACK = 0x0;
  // and so on...the definition for the other colors is
  // left as an exercise :-)


  public native void cls();
  public native void setCursorPosition( short x, short y);
  public native void keepColors();
  public native void restoreColors();
  public native void setColor( short foreground, short background);
  static {
    System.loadLibrary("jni3");
  }
}