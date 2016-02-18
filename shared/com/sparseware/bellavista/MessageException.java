package com.sparseware.bellavista;

import java.io.PrintWriter;

public class MessageException extends RuntimeException {
  private boolean fatal=false;
  private Throwable myCause;

  public MessageException(String msg) {
    super(msg);
  }
  public MessageException(String msg,boolean fatal) {
    super(msg);
    this.fatal=fatal;
  }

   public boolean isFatal() {
     return fatal;
   }

  public MessageException(Throwable e) {
    super(e);
    myCause=e;
  }
  @Override
  public void printStackTrace(PrintWriter s) {
    if(myCause!=null) {
      super.printStackTrace(s);
    }
  }
}
